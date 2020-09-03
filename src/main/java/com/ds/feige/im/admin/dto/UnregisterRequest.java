package com.ds.feige.im.admin.dto;

/**
 * 注销
 *
 * @author DC
 */
public class UnregisterRequest {
    private long userId;
    private long operatorId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(long operatorId) {
        this.operatorId = operatorId;
    }
}
