package com.ds.feige.im.app.dto;

import com.ds.feige.im.enterprise.dto.EnterpriseOpRequest;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class CreateRoleAuthority extends EnterpriseOpRequest {
    private Long appId;
    private Long roleId;
    private String name;
    private String resource;
    private String method;
}
