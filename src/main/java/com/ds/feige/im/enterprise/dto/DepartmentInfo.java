package com.ds.feige.im.enterprise.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties
@Data
public class DepartmentInfo extends SimpleDepartmentInfo {
    private List<DepartmentInfo> departments;
    private List<EmployeeInfo> employees;
}
