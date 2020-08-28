package com.ds.feige.im.pojo.dto.enterprise;

public class EmployeeInfo {
    private long userId;
    private String name;
    private String title;
    private String workEmail;
    private boolean leader;
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

    public boolean isLeader() {
        return leader;
    }

    public void setLeader(boolean leader) {
        this.leader = leader;
    }

    @Override
    public String toString() {
        return "EmployeeInfo{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", workEmail='" + workEmail + '\'' +
                ", leader=" + leader +
                '}';
    }
}
