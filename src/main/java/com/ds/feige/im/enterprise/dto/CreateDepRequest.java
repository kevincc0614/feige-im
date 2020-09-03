package com.ds.feige.im.enterprise.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

/**
 * @author caedmon
 */
public class CreateDepRequest extends EnterpriseOpRequest {
    @PositiveOrZero(message = "部门ID必须大于等于0")
    private long parentId;
    @NotBlank(message = "部门名称不能为空")
    private String departmentName;
    private String departmentEnName;
    private int priority;

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentEnName() {
        return departmentEnName;
    }

    public void setDepartmentEnName(String departmentEnName) {
        this.departmentEnName = departmentEnName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

}
