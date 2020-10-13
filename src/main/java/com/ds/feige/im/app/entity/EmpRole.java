package com.ds.feige.im.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

import lombok.Data;

/**
 * @author DC
 */
@TableName("t_app_emp_role")
@Data
public class EmpRole extends BaseEntity {
    private Long enterpriseId;
    private String roleName;
}
