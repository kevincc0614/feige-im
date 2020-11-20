package com.ds.feige.im.app.service;

import java.util.Collection;
import java.util.List;

import com.ds.feige.im.app.dto.AppInfo;
import com.ds.feige.im.enterprise.dto.CreateAppRequest;

/**
 * @author DC App应用管理
 */
public interface AppService {

    List<AppInfo> getApps(Collection<Long> appIds);

    AppInfo createApp(CreateAppRequest request);

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
