package com.ds.feige.im.chat.dto.event;

import lombok.Data;

import java.util.Map;

/**
 * 用户进群事件
 *
 * @author DC
 */
@Data
public class GroupInviteUsersJoinEvent {
    private long operatorId;
    private String operatorName;
    private long groupId;
    private Map<Long, String> inviteUsers;
}
