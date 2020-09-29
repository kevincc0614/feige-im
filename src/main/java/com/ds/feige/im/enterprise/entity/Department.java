package com.ds.feige.im.enterprise.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

import lombok.Data;

@TableName("t_department")
@Data
public class Department extends BaseEntity {
    private String name;
    private Long parentId;
    private Integer priority;
    private String enName;
    private Long enterpriseId;
    private Boolean createGroup;
    private Long groupId;
}
