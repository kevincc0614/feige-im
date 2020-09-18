package com.ds.feige.im.enterprise.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * @author DC
 */
@JsonIgnoreProperties
@Data
public class DepartmentInfo extends SimpleDepartmentInfo {
    private List<DepartmentInfo> departments;
    private List<EmployeeInfo> employees;
}
