package com.ds.feige.im.app.dto;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class RoleAuthorityInfo {
    private Long appId;
    private String appName;
    private Long roleId;
    private String roleName;
    private Long authorityId;
    private String authorityName;
    private String resource;
    private String method;
}
