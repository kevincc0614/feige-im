package com.ds.feige.im.enterprise.dto;

import java.util.ArrayList;
import java.util.List;

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
    private List<DepKeyInfo> departments = new ArrayList<>();
    private UserState state;

    @Data
    public static class DepKeyInfo {
        private Long departmentId;
        private String departmentName;
        private Boolean leader;
    }
}
