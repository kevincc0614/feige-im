package com.ds.feige.im.enterprise.dto;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class SimpleDepartmentInfo {
    protected long id;
    protected long parentId;
    protected String name;
    protected int priority;
}
