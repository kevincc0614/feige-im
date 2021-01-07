package com.ds.feige.im.gateway.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class UserDeviceQuery {
    private Set<Long> userIds;
    private Set<Integer> statusSet;
    private String deviceId;
    private String deviceType;

    public void addUserId(long userId) {
        if (userIds == null) {
            userIds = new HashSet<>();
        }
        userIds.add(userId);
    }

    public void addStatus(int status) {
        if (statusSet == null) {
            statusSet = new HashSet<>();
        }
        statusSet.add(status);
    }
}
