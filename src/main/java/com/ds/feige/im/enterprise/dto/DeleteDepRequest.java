package com.ds.feige.im.enterprise.dto;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class DeleteDepRequest extends EnterpriseOpRequest {
    private long departmentId;
}
