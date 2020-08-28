package com.ds.feige.im.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

/**
 * @author DC
 */
public class BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    protected Long id;
    @TableField(fill = FieldFill.INSERT)
    protected Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected Date updateTime;
    @TableLogic
    protected Boolean deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
