package com.ds.feige.im.app.service;

import com.ds.feige.im.app.dto.AppInfo;

/**
 * @author DC App应用管理
 */
public interface AppService {
    /**
     * @param enterpriseId
     * @param appName
     * @param avatar
     * @return secret
     */
    AppInfo createApp(long enterpriseId, String appName, String avatar);

    /**
     * @param enterpriseId
     * @param secret
     * @return token
     */
    String getAccessToken(long enterpriseId, String secret);

    /**
     * @param enterpriseId
     * @param secret
     * @return new secret
     */
    String refreshSecret(long enterpriseId, String secret);

    /**
     * @param token
     * @return app info
     */
    AppInfo verifyToken(String token);

    /***
     * @param appId
     * 
     */
    AppInfo getApp(long appId);
}
