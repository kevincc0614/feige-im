package com.ds.feige.im.enterprise.dto;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class DeleteEmpRequest extends EnterpriseOpRequest {
    private long userId;
}
