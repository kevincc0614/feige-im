package com.ds.feige.im.enterprise.dto;

import java.util.List;

import com.ds.feige.im.gateway.domain.UserState;

import lombok.Data;

@Data
public class EmployeeInfo {
    private long userId;
    private String name;
    private String avatar;
    private String title;
    private String workEmail;
    private Boolean leader;
    private List<Long> departments;
    private UserState state;
}
