package com.ds.feige.im.enterprise.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

import lombok.Data;

/**
 * @author DC
 */
@TableName("t_emp_role_binding")
@Data
public class EmpRoleBinding extends BaseEntity {
    private Long enterpriseId;
    private Long userId;
    private Long roleId;
}
