package com.ds.feige.im.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_department_employee")
public class DepartmentEmployee extends BaseEntity{
    private Long userId;
    private Long departmentId;
    private Boolean leader;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Boolean getLeader() {
        return leader;
    }

    public void setLeader(Boolean leader) {
        this.leader = leader;
    }
}
