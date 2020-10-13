package com.ds.feige.im.app.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.app.dto.AppInfo;
import com.ds.feige.im.app.entity.App;
import com.ds.feige.im.app.mapper.AppMapper;
import com.ds.feige.im.common.util.BeansConverter;
import com.ds.feige.im.constants.FeigeWarn;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DC
 */
@Service
@Slf4j
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Autowired
    RedissonClient redissonClient;

    @Override
    public AppInfo createApp(long enterpriseId, String appName, String avatar) {
        // 判断是否重名
        App app = baseMapper.getByEntAndName(enterpriseId, appName);
        if (app != null) {
            throw new WarnMessageException(FeigeWarn.APP_NAME_EXISTS);
        }
        app = new App();
        app.setEnterpriseId(enterpriseId);
        app.setName(appName);
        app.setAvatar(avatar);
        String secret = RandomStringUtils.randomAlphabetic(32);
        app.setSecret(secret);
        save(app);
        return BeansConverter.appToAppInfo(app);
    }

    @Override
    public String getAccessToken(long enterpriseId, String secret) {
        App app = baseMapper.getByEntAndSecret(enterpriseId, secret);
        if (app == null) {
            throw new WarnMessageException(FeigeWarn.APP_SECRET_INVALID);
        }
        LocalDateTime expireAt = LocalDateTime.now().plusHours(2);
        Date expireDate = Date.from(expireAt.atZone(ZoneId.systemDefault()).toInstant());
        String token =
            JWT.create().withClaim("appId", app.getId()).withExpiresAt(expireDate).sign(Algorithm.HMAC256(secret));
        // 放入缓存
        log.info("User get token success:userId={},token={}", enterpriseId, token);
        return token;
    }

    @Override
    public AppInfo verifyToken(String token) {
        // 获取 token 中的 user id
        log.info("User verify token:token={}", token);
        Long appId = JWT.decode(token).getClaim("appId").asLong();
        AppInfo appInfo = getApp(appId);
        // 验证token合法性和签名,密码变更之后,旧token无法验证通过
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(appInfo.getSecret())).build();
        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
        } catch (Exception e) {// 签名算法不匹配
            if (e instanceof TokenExpiredException) {
                throw new WarnMessageException(e, FeigeWarn.TOKEN_EXPIRED);
            }
            throw new WarnMessageException(e, FeigeWarn.TOKEN_VERIFY_ERROR);
        }
        return appInfo;
    }

    @Override
    public String refreshSecret(long enterpriseId, String secret) {
        App app = baseMapper.getByEntAndSecret(enterpriseId, secret);
        if (app == null) {
            throw new WarnMessageException(FeigeWarn.APP_NOT_EXISTS);
        }
        String newSecret = RandomStringUtils.randomAlphabetic(32);
        app.setSecret(newSecret);
        save(app);
        return newSecret;
    }

    @Override
    public AppInfo getApp(long appId) {
        App app = getById(appId);
        if (app == null) {
            throw new WarnMessageException(FeigeWarn.APP_NOT_EXISTS);
        }
        return BeansConverter.appToAppInfo(app);
    }
}
