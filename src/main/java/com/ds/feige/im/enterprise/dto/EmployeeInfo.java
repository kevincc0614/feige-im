package com.ds.feige.im.enterprise.dto;

import java.util.Map;

import com.ds.feige.im.gateway.domain.UserState;

import lombok.Data;

@Data
public class EmployeeInfo {
    private long userId;
    private String name;
    private String avatar;
    private String title;
    private String workEmail;
    private Map<Long, Boolean> departments;
    private UserState state;
}
