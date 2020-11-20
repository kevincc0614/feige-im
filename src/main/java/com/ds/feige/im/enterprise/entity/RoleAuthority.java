package com.ds.feige.im.enterprise.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

import lombok.Data;

/**
 * @author DC
 */
@TableName("t_emp_role_authority")
@Data
public class RoleAuthority extends BaseEntity {
    private Long enterpriseId;
    private Long appId;
    private String name;
    private String resource;
    private String method;
}
