package com.ds.feige.im.app.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

import lombok.Data;

/**
 * @author DC
 */
@TableName("t_app")
@Data
public class App extends BaseEntity {
    private Long enterpriseId;
    private String name;
    private String avatar;
    private String secret;
    private String config;
}
