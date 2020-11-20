package com.ds.feige.im.enterprise.dto;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class RoleAuthoritiesQuery extends EnterpriseOpRequest {
    private Long roleId;
}
