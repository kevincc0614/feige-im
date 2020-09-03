package com.ds.feige.im.enterprise.dto;

/**
 * @author DC
 */
public class DeleteDepRequest extends EnterpriseOpRequest {
    private long departmentId;

    public long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(long departmentId) {
        this.departmentId = departmentId;
    }
}
