package com.ds.feige.im.enterprise.dto;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class EditDepEmpRequest extends EnterpriseOpRequest {
    private long userId;
    private long departmentId;
    private boolean leader;
}
