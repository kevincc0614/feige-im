package com.ds.feige.im.gateway.dto;

import lombok.Data;

/**
 * 异地登录通知
 *
 * @author DC
 */
@Data
public class RemoteLoginPayload {
    private String deviceId;
    private String ipAddress;
}
