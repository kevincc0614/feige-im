package com.ds.feige.im.gateway.service;

import java.util.List;

import com.ds.feige.im.account.dto.LoginRequest;
import com.ds.feige.im.gateway.entity.UserDevice;
import com.ds.feige.im.push.dto.RegisterTokenRequest;

/**
 * 用户设备管理
 *
 * @author DC
 */
public interface UserDeviceService {
    /**
     * 设备登录
     *
     * @param loginRequest
     */
    void deviceLogin(LoginRequest loginRequest);

    /**
     * 设备登出
     *
     * @param userId
     * @param deviceId
     */
    void deviceLogout(long userId, String deviceId);

    /**
     * 获取用户设备列表
     *
     * @param userId
     */
    List<UserDevice> getPushableDevices(long userId);

    /**
     * 更新设备token,调用此方法之前,用户必须已经登陆
     */
    void registerToken(RegisterTokenRequest request);
}
