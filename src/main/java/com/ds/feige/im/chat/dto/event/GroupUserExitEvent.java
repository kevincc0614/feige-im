package com.ds.feige.im.chat.dto.event;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class GroupUserExitEvent {
    private long groupId;
    private long userId;
    private String userName;
}
