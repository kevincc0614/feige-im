package com.ds.feige.im.enterprise.dto;

/**
 * @author DC
 */
public class GetEmpRequest {
    private long enterpriseId;
    private long userId;

    public long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
