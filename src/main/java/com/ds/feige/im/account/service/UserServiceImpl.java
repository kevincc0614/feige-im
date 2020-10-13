package com.ds.feige.im.account.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import com.ds.feige.im.account.dto.GetTokenRequest;
import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.dto.UserRegisterRequest;
import com.ds.feige.im.account.entity.User;
import com.ds.feige.im.account.mapper.UserMapper;
import com.ds.feige.im.common.util.BeansConverter;
import com.ds.feige.im.constants.FeigeWarn;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户服务,主要为无状态服务接口
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    @Autowired
    @Qualifier("longIdKeyGenerator")
    IdKeyGenerator<Long> idKeyGenerator;
    @Autowired
    RedissonClient redissonClient;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserInfo getUserByMobile(String mobile) {
        User user = userMapper.getByMobile(mobile);
        if (user == null) {
            return null;
        }
        return BeansConverter.userToUserInfo(user);
    }

    @Override
    public String getToken(GetTokenRequest request) {
        String loginName = request.getLoginName();
        User user = userMapper.getByMobile(loginName);
        if (user == null) {
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        byte[] key = user.getSalt().getBytes();
        String password =
            Hashing.hmacMd5(key).newHasher().putString(request.getPassword(), Charsets.UTF_8).hash().toString();
        // 查询数据库
        user = this.userMapper.getByMobileAndPassword(loginName, password);
        // 用户不存在
        if (user == null) {
            throw new WarnMessageException(FeigeWarn.PWD_ERROR);
        }
        LocalDateTime expireAt = LocalDateTime.now().plusDays(90);
        Date expireDate = Date.from(expireAt.atZone(ZoneId.systemDefault()).toInstant());
        // 生成token
        long userId = user.getId();
        String token =
            JWT.create().withClaim("userId", userId).withExpiresAt(expireDate).sign(Algorithm.HMAC256(password));
        // 放入缓存

        log.info("User get token success:userId={},token={}", user.getId(), token);
        return token;
    }

    // public User getUserFromCache(long userId) {
    // RBucket<User> bucket = redissonClient.getBucket(CacheKeys.USER_INFO + userId);
    // User user = bucket.get();
    // return user;
    // }
    //
    // public void setUserToCache(User user) {
    // RBucket<User> bucket = redissonClient.getBucket(CacheKeys.USER_INFO + user.getId());
    // bucket.set(user, 30, TimeUnit.DAYS);
    // }

    @Override
    public UserInfo verifyToken(String token) {
        // 获取 token 中的 user id
        log.info("User verify token:token={}", token);
        Long userId = JWT.decode(token).getClaim("userId").asLong();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        // 验证token合法性和签名,密码变更之后,旧token无法验证通过
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
        } catch (Exception e) {// 签名算法不匹配
            if (e instanceof TokenExpiredException) {
                throw new WarnMessageException(e, FeigeWarn.TOKEN_EXPIRED);
            }
            throw new WarnMessageException(e, FeigeWarn.TOKEN_VERIFY_ERROR);
        }
        UserInfo userInfo = BeansConverter.userToUserInfo(user);
        return userInfo;
    }

    @Override
    public long register(UserRegisterRequest request) {
        // 判断手机号是否已存在
        int i = userMapper.getCountByMobile(request.getMobile());
        if (i >= 1) {
            throw new WarnMessageException(FeigeWarn.ACCOUNT_REGISTERD);
        }
        long id = idKeyGenerator.generateId();
        // 随机生成字符串
        String salt = RandomStringUtils.randomAlphabetic(12);
        User user = new User();
        user.setMobile(request.getMobile());
        String password = Hashing.hmacMd5(salt.getBytes()).newHasher().putString(request.getPassword(), Charsets.UTF_8)
            .hash().toString();
        user.setPassword(password);
        user.setSalt(salt);
        user.setSource(request.getSource());
        user.setNickName("用户" + id);
        user.setGender(1);
        String accountIdSrc = String.valueOf(id);
        String accountId = Base64.getEncoder().encodeToString(accountIdSrc.getBytes());
        user.setAccountId(accountId);
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public boolean deleteUser(long userId) {
        return userMapper.deleteById(userId) == 1;
    }

    @Override
    public UserInfo getUserById(long userId) {
        User user = userMapper.selectById(userId);
        UserInfo userInfo = BeansConverter.userToUserInfo(user);
        return userInfo;
    }

    @Override
    public List<UserInfo> getUserByIds(Collection<Long> userIdList) {
        if (userIdList == null || userIdList.isEmpty()) {
            return Lists.newArrayListWithCapacity(0);
        }
        List<User> users = userMapper.findUserByIds(userIdList);
        List<UserInfo> userInfos = BeansConverter.usersToUserInfos(users);
        return userInfos;
    }

    @Override
    public void unregisterUser(long userId, long operatorId) {
        // TODO 缺少权限校验
        int i = userMapper.deleteById(userId);
        if (i < 1) {
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        log.info("Cancel user success:userId={},operatorId={}", userId, operatorId);
    }
}
