package com.ds.feige.im.app.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.app.dto.AppInfo;
import com.ds.feige.im.app.entity.App;
import com.ds.feige.im.app.mapper.AppMapper;
import com.ds.feige.im.common.util.BeansConverter;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.enterprise.dto.CreateAppRequest;
import com.google.common.collect.Lists;

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
    public List<AppInfo> getApps(Collection<Long> appIds) {
        if (appIds != null && !appIds.isEmpty()) {
            List<App> apps = baseMapper.selectBatchIds(appIds);
            List<AppInfo> result = Lists.newArrayListWithCapacity(apps.size());
            apps.forEach(app -> {
                AppInfo appInfo = new AppInfo();
                BeanUtils.copyProperties(app, appInfo, "secret");
                result.add(appInfo);
            });
            return result;
        }
        return Lists.newArrayListWithCapacity(0);
    }

    @Override
    public AppInfo createApp(CreateAppRequest request) {
        long enterpriseId = request.getEnterpriseId();
        String appName = request.getName();
        String config = request.getConfig();
        String avatar = request.getAvatar();
        // 判断是否重名
        App app = baseMapper.getByEntAndName(enterpriseId, appName);
        if (app != null) {
            throw new WarnMessageException(FeigeWarn.APP_NAME_EXISTS);
        }
        app = new App();
        app.setEnterpriseId(enterpriseId);
        app.setName(appName);
        app.setAvatar(avatar);
        app.setConfig(config);
        String secret = RandomStringUtils.randomAlphabetic(32);
        app.setSecret(secret);
        save(app);
        return BeansConverter.convertToAppInfo(app);
    }

    @Override
    public String getAccessToken(long enterpriseId, String secret) {
        App app = baseMapper.getByEntAndSecret(enterpriseId, secret);
        if (app == null) {
            throw new WarnMessageException(FeigeWarn.APP_SECRET_INVALID);
        }
        LocalDateTime expireAt = LocalDateTime.now().plusHours(3);
        Date expireDate = Date.from(expireAt.atZone(ZoneId.systemDefault()).toInstant());
        String token =
            JWT.create().withClaim("appId", app.getId()).withExpiresAt(expireDate).sign(Algorithm.HMAC256(secret));
        // 放入缓存
        log.info("App get access token success:appId={},token={}", enterpriseId, token);
        return token;
    }

    @Override
    public AppInfo verifyToken(String token) {
        AppInfo appInfo;
        try {
            Long appId = JWT.decode(token).getClaim("appId").asLong();
            appInfo = getApp(appId);
            // 验证token合法性和签名,密码变更之后,旧token无法验证通过
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(appInfo.getSecret())).build();
            jwtVerifier.verify(token);
        } catch (Exception e) {// 签名算法不匹配
            log.warn("App verify token fail:token={}", token);
            if (e instanceof TokenExpiredException) {
                throw new WarnMessageException(e, FeigeWarn.APP_TOKEN_EXPIRED);
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
        log.info("App refresh secret success:appId={},newSecret={}", app.getId(), newSecret);
        return newSecret;
    }

    @Override
    public AppInfo getApp(long appId) {
        App app = getById(appId);
        if (app == null) {
            throw new WarnMessageException(FeigeWarn.APP_NOT_EXISTS);
        }
        return BeansConverter.convertToAppInfo(app);
    }
}
