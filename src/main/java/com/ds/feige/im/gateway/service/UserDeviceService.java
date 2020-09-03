package com.ds.feige.im.gateway.service;

import com.ds.feige.im.account.dto.LoginRequest;
import com.ds.feige.im.gateway.entity.UserDevice;

import java.util.List;

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
    List<UserDevice> getDevices(long userId, int status);
}
