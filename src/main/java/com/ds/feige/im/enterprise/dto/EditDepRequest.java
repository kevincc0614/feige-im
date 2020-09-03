package com.ds.feige.im.enterprise.dto;

/**
 * @author DC
 */
public class EditDepRequest extends EnterpriseOpRequest {
    private long departmentId;
    private long parentId;
    private String name;
    private String enName;
    private int priority;

    public long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(long departmentId) {
        this.departmentId = departmentId;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "EditDepRequest{" +
                "departmentId=" + departmentId +
                ", parentId=" + parentId +
                ", name='" + name + '\'' +
                ", enName='" + enName + '\'' +
                ", priority=" + priority +
                ", enterpriseId=" + enterpriseId +
                ", operatorId=" + operatorId +
                '}';
    }
}

