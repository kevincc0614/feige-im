package com.ds.feige.im.enterprise.dto;

import java.util.Map;

import com.ds.feige.im.common.domain.UserIdHolder;
import com.ds.feige.im.gateway.domain.UserState;

import lombok.Data;

@Data
public class EmpDetails implements UserIdHolder {
    private Long userId;
    private String name;
    private String avatar;
    private String title;
    private String workEmail;
    private Map<Long, Boolean> departments;
    private UserState state;
}
