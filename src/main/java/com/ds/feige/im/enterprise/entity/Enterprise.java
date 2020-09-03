package com.ds.feige.im.enterprise.entity;

import com.ds.feige.im.common.entity.BaseEntity;

/**
 * 公司
 *
 * @author DC
 */
public class Enterprise extends BaseEntity {
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
