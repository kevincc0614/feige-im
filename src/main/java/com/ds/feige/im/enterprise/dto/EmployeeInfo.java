package com.ds.feige.im.enterprise.dto;

import java.util.List;

public class EmployeeInfo {
    private long userId;
    private String name;
    private String avatar;
    private String title;
    private String workEmail;
    private Boolean leader;
    private List<Long> departments;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail;
    }

    public Boolean getLeader() {
        return leader;
    }

    public void setLeader(Boolean leader) {
        this.leader = leader;
    }

    public List<Long> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Long> departments) {
        this.departments = departments;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "EmployeeInfo{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", workEmail='" + workEmail + '\'' +
                ", leader=" + leader +
                ", departments=" + departments +
                '}';
    }
}
