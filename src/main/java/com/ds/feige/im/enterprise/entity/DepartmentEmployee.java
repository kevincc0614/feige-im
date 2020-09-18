package com.ds.feige.im.enterprise.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

import lombok.Data;

@TableName("t_department_employee")
@Data
public class DepartmentEmployee extends BaseEntity {
    private Long userId;
    private Long enterpriseId;
    private Long departmentId;
    private Boolean leader;
}
