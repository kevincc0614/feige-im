package com.ds.feige.im.enterprise.dto;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class EditDepRequest extends EnterpriseOpRequest {
    private long departmentId;
    private long parentId;
    private String name;
    private String enName;
    private int priority;
}

