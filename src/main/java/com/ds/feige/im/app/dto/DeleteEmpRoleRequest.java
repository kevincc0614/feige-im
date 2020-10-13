package com.ds.feige.im.app.dto;

import com.ds.feige.im.enterprise.dto.EnterpriseOpRequest;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class DeleteEmpRoleRequest extends EnterpriseOpRequest {
    private long roleId;
}
