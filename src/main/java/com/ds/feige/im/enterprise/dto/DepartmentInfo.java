package com.ds.feige.im.enterprise.dto;

import java.util.List;

public class DepartmentInfo {
    private long id;
    private long parentId;
    private String name;
    private int priority;
    private List<DepartmentInfo> departments;
    private List<EmployeeInfo> employees;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

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
