package com.ds.feige.im.enterprise.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class PermissionCheckRequest {
    private long enterpriseId;
    private long appId;
    private long userId;
    @NotBlank
    private String resource;
    @NotBlank
    private String method;
}
