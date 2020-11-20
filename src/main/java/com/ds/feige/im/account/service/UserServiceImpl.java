package com.ds.feige.im.account.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import com.ds.feige.im.account.cache.UserCacheProvider;
import com.ds.feige.im.account.dto.GetTokenRequest;
import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.dto.UserRegisterRequest;
import com.ds.feige.im.account.entity.User;
import com.ds.feige.im.account.mapper.UserMapper;
import com.ds.feige.im.common.util.BeansConverter;
import com.ds.feige.im.constants.FeigeWarn;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
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
    UserCacheProvider userCacheProvider;
    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void updateUser(long userId, String loginName, String password) {
        if (Strings.isNullOrEmpty(loginName)) {
            throw new WarnMessageException(FeigeWarn.LOGIN_NAME_IS_EMPTY);
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        boolean needUpdate = false;
        // 密码不为空修改密码
        if (!Strings.isNullOrEmpty(password)) {
            // 登录名发生变更,判断是否存在相同用户名
            String salt = user.getSalt();
            String passwordHash = passwordHash(salt, password);
            user.setPassword(passwordHash);
            log.info("Change user password:userId={}", userId);
            needUpdate = true;
        }
        String oldLoginName = user.getLoginName();
        // 登录名变更修改登录名
        if (!oldLoginName.equals(loginName)) {
            User userByLoginName = userMapper.getByLoginName(loginName);
            if (userByLoginName != null && !userByLoginName.getId().equals(user.getId())) {
                throw new WarnMessageException(FeigeWarn.LOGIN_NAME_EXISTS);
            }
            needUpdate = true;
            user.setLoginName(loginName);
            log.info("Change user loginName:userId={},newLoginName={},oldLoginName={}", userId, oldLoginName,
                loginName);
        }
        if (needUpdate) {
            userCacheProvider.putUser(user);
            userMapper.updateById(user);
        }

    }

    @Override
    public String getToken(GetTokenRequest request) {
        String loginName = request.getLoginName();
        User user = userMapper.getByLoginName(loginName);
        if (user == null) {
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        byte[] key = user.getSalt().getBytes();
        String password =
            Hashing.hmacMd5(key).newHasher().putString(request.getPassword(), Charsets.UTF_8).hash().toString();
        // 查询数据库
        user = this.userMapper.getByLoginNameAndPassword(loginName, password);
        // 用户不存在
        if (user == null) {
            throw new WarnMessageException(FeigeWarn.PWD_ERROR);
        }
        userCacheProvider.putUser(user);
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

    @Override
    public UserInfo verifyToken(String token) {
        // 获取 token 中的 user id
        Long userId = JWT.decode(token).getClaim("userId").asLong();
        UserInfo user = getUserById(userId);
        if (user == null) {
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        // 验证token合法性和签名,密码变更之后,旧token无法验证通过
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try {
            jwtVerifier.verify(token);
        } catch (Exception e) {// 签名算法不匹配
            log.warn("User verify token fail:token={}", token);
            if (e instanceof TokenExpiredException) {
                throw new WarnMessageException(e, FeigeWarn.TOKEN_EXPIRED);
            }
            throw new WarnMessageException(e, FeigeWarn.TOKEN_VERIFY_ERROR);
        }
        return user;
    }

    static String passwordHash(String salt, String password) {
        String result =
            Hashing.hmacMd5(salt.getBytes()).newHasher().putString(password, Charsets.UTF_8).hash().toString();
        return result;
    }
    @Override
    public long register(UserRegisterRequest request) {
        // 判断手机号是否已存在
        int i = userMapper.getCountByLoginName(request.getLoginName());
        if (i >= 1) {
            throw new WarnMessageException(FeigeWarn.ACCOUNT_REGISTERD);
        }
        long id = idKeyGenerator.generateId();
        // 随机生成字符串
        String salt = RandomStringUtils.randomAlphabetic(12);
        User user = new User();
        user.setLoginName(request.getLoginName());
        String passwordHash = passwordHash(salt, request.getPassword());
        user.setPassword(passwordHash);
        user.setSalt(salt);
        user.setSource(request.getSource());
        String nickName = request.getNickName();
        user.setNickName(!Strings.isNullOrEmpty(nickName) ? nickName : "用户" + id);
        user.setGender(1);
        userMapper.insert(user);
        userCacheProvider.putUser(user);
        log.info("Register user success:{}", user);
        return user.getId();
    }

    @Override
    public boolean deleteUser(long userId) {
        userCacheProvider.deleteUser(userId);
        return userMapper.deleteById(userId) == 1;
    }

    @Override
    public UserInfo getUserById(long userId) {
        User user = userCacheProvider.getUser(userId);
        if (user == null) {
            user = userMapper.selectById(userId);
            if (user != null) {
                userCacheProvider.putUser(user);
            } else {
                return null;
            }
        }
        UserInfo userInfo = BeansConverter.userToUserInfo(user);
        return userInfo;
    }

    @Override
    public Collection<UserInfo> getUserByIds(Collection<Long> userIdList) {
        if (userIdList == null || userIdList.isEmpty()) {
            return Lists.newArrayListWithCapacity(0);
        }

        Collection<User> users = userCacheProvider.getUsers(userIdList);
        if (users == null || users.isEmpty()) {
            users = userMapper.findUserByIds(userIdList);
        }
        List<UserInfo> userInfos = BeansConverter.usersToUserInfos(users);
        return userInfos;
    }

    @Override
    public void unregisterUser(long userId, long operatorId) {
        // TODO 缺少权限校验
        boolean deleted = deleteUser(userId);
        if (!deleted) {
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        log.info("Cancel user success:userId={},operatorId={}", userId, operatorId);
    }
}
