package com.ds.feige.im.enterprise.dto;

import lombok.Data;

/**
 * 部门查询
 *
 * @author DC
 */
@Data
public class GetDepRequest {
    private long enterpriseId;
    private long departmentId;
    private boolean queryChild;
}
