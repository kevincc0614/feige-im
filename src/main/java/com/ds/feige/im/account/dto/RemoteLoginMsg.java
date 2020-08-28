package com.ds.feige.im.account.dto;

/**
 * 异地登录通知
 *
 * @author DC
 */
public class RemoteLoginMsg {
    private String deviceId;
    private String ipAddress;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
