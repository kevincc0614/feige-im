package com.ds.feige.im.enterprise.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * @author DC
 */
@JsonIgnoreProperties
@Data
public class DepartmentDetails extends DepartmentOverview {
    private List<DepartmentDetails> departments;
    private List<EmpDetails> employees;
    /** 在线人数 */
    protected int online;
    /** 部门人数 */
    protected int total;
}
