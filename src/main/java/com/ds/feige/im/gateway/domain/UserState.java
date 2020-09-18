package com.ds.feige.im.gateway.domain;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class UserState {
    private Boolean online;
    private Long offlineTime;

    public long getOfflineDuration() {
        return offlineTime == null ? -1 : (System.currentTimeMillis() - offlineTime) / 1000;
    }
}
