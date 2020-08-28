package com.ds.feige.im.pojo.dto.user;

import javax.validation.constraints.NotBlank;

public class LoginRequest extends UserRequest {
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;
    private int deviceType;
    @NotBlank(message = "token不能为空")
    private String token;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }
}
