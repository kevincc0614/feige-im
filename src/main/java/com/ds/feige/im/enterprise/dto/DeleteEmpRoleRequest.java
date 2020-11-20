package com.ds.feige.im.enterprise.dto;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class DeleteEmpRoleRequest extends EnterpriseOpRequest {
    private long roleId;
}
