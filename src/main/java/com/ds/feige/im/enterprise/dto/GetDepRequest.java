package com.ds.feige.im.enterprise.dto;

/**
 * 部门查询
 *
 * @author DC
 */
public class GetDepRequest {
    private long enterpriseId;
    private long departmentId;
    private boolean queryChild;

    public long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(long departmentId) {
        this.departmentId = departmentId;
    }

    public boolean isQueryChild() {
        return queryChild;
    }

    public void setQueryChild(boolean queryChild) {
        this.queryChild = queryChild;
    }

    public long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
}
