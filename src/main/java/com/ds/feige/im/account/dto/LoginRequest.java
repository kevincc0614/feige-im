package com.ds.feige.im.account.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.ds.feige.im.constants.DeviceType;

import lombok.Data;

@Data
public class LoginRequest extends UserRequest {
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;
    @NotNull(message = "设备类型不能为空")
    private DeviceType deviceType;
    @NotBlank(message = "设备名不能为空")
    private String deviceName;
    private String pushToken;
    @NotBlank(message = "token不能为空")
    private String authToken;

}
