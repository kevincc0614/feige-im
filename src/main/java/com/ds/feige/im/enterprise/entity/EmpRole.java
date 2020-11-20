package com.ds.feige.im.enterprise.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

import lombok.Data;

/**
 * @author DC
 */
@TableName("t_emp_role")
@Data
public class EmpRole extends BaseEntity {
    private Long enterpriseId;
    private String roleName;
    private String authorityIds;
}
