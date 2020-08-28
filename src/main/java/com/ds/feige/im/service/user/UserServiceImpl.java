package com.ds.feige.im.service.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import com.ds.feige.im.common.util.BeansConverter;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.mapper.UserMapper;
import com.ds.feige.im.pojo.dto.user.GetTokenRequest;
import com.ds.feige.im.pojo.dto.user.UserInfo;
import com.ds.feige.im.pojo.dto.user.UserRegisterRequest;
import com.ds.feige.im.pojo.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * 用户服务,主要为无状态服务接口
 * */
@Service
public class UserServiceImpl implements UserService {
    static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserMapper userMapper;
    @Autowired
    @Qualifier("longIdKeyGenerator")
    IdKeyGenerator<Long> idKeyGenerator;
    @Autowired
    public UserServiceImpl(UserMapper userMapper){
        this.userMapper=userMapper;
    }
    @Override
    public String getToken(GetTokenRequest request) {
        //查询数据库
        User user=this.userMapper.getByMobileAndPassword(request.getLoginName(),request.getPassword());
        //用户不存在
        if(user==null){
            throw new WarnMessageException(FeigeWarn.NAME_OR_PWD_ERROR);
        }
        LocalDateTime expireAt=LocalDateTime.now().plusDays(90);
        Date expireDate=Date.from(expireAt.atZone(ZoneId.systemDefault()).toInstant());
        //生成token
        String token=JWT.create().withAudience(String.valueOf(user.getId()))
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC256(request.getPassword()));
        LOGGER.info("User get token success:userId={},token={}",user.getId(),token);
        return token;
    }
    @Override
    public UserInfo verifyToken(String token) {
        // 获取 token 中的 user id
        LOGGER.info("User verify token:token={}",token);
        Long userId = Long.valueOf(JWT.decode(token).getAudience().get(0));
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        // 验证 token
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try{
            DecodedJWT decodedJWT=jwtVerifier.verify(token);
        }catch (Exception e){//签名算法不匹配
            if(e instanceof TokenExpiredException){
                throw new WarnMessageException(e,FeigeWarn.TOKEN_VERIFY_ERROR);
            }
            throw new WarnMessageException(e,FeigeWarn.TOKEN_VERIFY_ERROR);
        }
        UserInfo userInfo=BeansConverter.userToUserInfo(user);
        return userInfo;
    }

    @Override
    public boolean userExists(long userId) {
        return userMapper.getCountById(userId)>=1;
    }

    @Override
    public long register(UserRegisterRequest registerRequest) {
        //判断手机号是否已存在
        int i=userMapper.getByMobile(registerRequest.getMobile());
        if(i>=1){
            throw new WarnMessageException(FeigeWarn.ACCOUNT_REGISTERD);
        }
        long id=idKeyGenerator.generateId();
        User user=new User();
        user.setMobile(registerRequest.getMobile());
        //TODO 密码加密
        user.setPassword(registerRequest.getPassword());
        user.setSource(registerRequest.getSource());
        user.setNickName("用户"+id);
        user.setGender(1);
        String accountIdSrc=String.valueOf(id);
        String accountId=Base64.getEncoder().encodeToString(accountIdSrc.getBytes());
        user.setAccountId(accountId);
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public boolean deleteUser(long userId) {
        return userMapper.deleteById(userId)==1;
    }

    @Override
    public UserInfo getUserById(long userId) {
        User user=userMapper.selectById(userId);
        UserInfo userInfo= BeansConverter.userToUserInfo(user);
        return userInfo;
    }

    @Override
    public List<UserInfo> getUserByIds(List<Long> userIdList) {
        List<User> users=userMapper.findUserByIds(userIdList);
        List<UserInfo> userInfos=BeansConverter.usersToUserInfos(users);
        return userInfos;
    }
}

