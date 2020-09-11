package com.ds.feige.im.enterprise.dto;

import java.util.List;

public class DepartmentInfo extends SimpleDepartmentInfo {
    private List<DepartmentInfo> departments;
    private List<EmployeeInfo> employees;

    public List<DepartmentInfo> getDepartments() {
        return departments;
    }

    public void setDepartments(List<DepartmentInfo> departments) {
        this.departments = departments;
    }

    public List<EmployeeInfo> getEmployees() {
        return employees;
    }

    public void setEmployees(List<EmployeeInfo> employees) {
        this.employees = employees;
    }

    @Override
    public String toString() {
        return "DepartmentInfo{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", name='" + name + '\'' +
                ", priority=" + priority +
                ", childDepartments=" + departments +
                ", employees=" + employees +
                '}';
    }
}
