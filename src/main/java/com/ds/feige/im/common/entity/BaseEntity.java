package com.ds.feige.im.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author DC
 */
@Data
public class BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    protected Long id;
    @TableField(fill = FieldFill.INSERT)
    protected Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected Date updateTime;
}
