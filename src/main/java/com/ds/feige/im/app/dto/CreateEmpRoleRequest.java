package com.ds.feige.im.app.dto;

import com.ds.feige.im.enterprise.dto.EnterpriseOpRequest;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class CreateEmpRoleRequest extends EnterpriseOpRequest {
    private String roleName;
}
