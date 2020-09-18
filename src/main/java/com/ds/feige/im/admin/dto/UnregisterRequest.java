package com.ds.feige.im.admin.dto;

import lombok.Data;

/**
 * 注销
 *
 * @author DC
 */
@Data
public class UnregisterRequest {
    private long userId;
    private long operatorId;
}
