package com.ds.feige.im.enterprise.dto;

/**
 * @author DC
 */
public class EnterpriseOpRequest {
    protected long enterpriseId;
    protected long operatorId;

    public long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(long operatorId) {
        this.operatorId = operatorId;
    }

    @Override
    public String toString() {
        return "EnterpriseOpRequest{" +
                "enterpriseId=" + enterpriseId +
                ", operatorId=" + operatorId +
                '}';
    }
}
