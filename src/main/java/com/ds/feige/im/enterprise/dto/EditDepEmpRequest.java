package com.ds.feige.im.enterprise.dto;

import java.util.Map;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class EditDepEmpRequest extends EnterpriseOpRequest {
    private long userId;
    private Map<Long, Boolean> departments;
}
