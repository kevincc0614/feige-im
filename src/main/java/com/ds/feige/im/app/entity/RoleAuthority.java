package com.ds.feige.im.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

import lombok.Data;

/**
 * @author DC
 */
@TableName("t_app_role_authority")
@Data
public class RoleAuthority extends BaseEntity {
    private Long appId;
    private Long roleId;
    private String name;
    private String resource;
    private String method;
}
