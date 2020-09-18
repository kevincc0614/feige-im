package com.ds.feige.im.enterprise.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

import lombok.Data;

/**
 * 公司
 *
 * @author DC
 */
@TableName("t_enterprise")
@Data
public class Enterprise extends BaseEntity {
    private String name;
    private String description;
}
