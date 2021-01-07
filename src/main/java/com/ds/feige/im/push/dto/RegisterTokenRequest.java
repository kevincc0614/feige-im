package com.ds.feige.im.push.dto;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class RegisterTokenRequest {
    private Long userId;
    private String deviceId;
    private String deviceToken;
}
