package com.ds.feige.im.enterprise.dto;

/**
 * @author DC
 */
public class EditDepEmpRequest extends EnterpriseOpRequest {
    private long userId;
    private long departmentId;
    private boolean leader;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(long departmentId) {
        this.departmentId = departmentId;
    }

    public boolean isLeader() {
        return leader;
    }

    public void setLeader(boolean leader) {
        this.leader = leader;
    }

    @Override
    public String toString() {
        return "EditDepEmpRequest{" +
                "userId=" + userId +
                ", departmentId=" + departmentId +
                ", leader=" + leader +
                ", enterpriseId=" + enterpriseId +
                ", operatorId=" + operatorId +
                '}';
    }
}
