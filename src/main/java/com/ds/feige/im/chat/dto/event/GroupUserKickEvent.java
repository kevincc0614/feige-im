package com.ds.feige.im.chat.dto.event;

import java.util.Set;

import lombok.Data;

/**
 * 用户被踢出群聊事件
 *
 * @author DC
 */
@Data
public class GroupUserKickEvent {
    private long groupId;
    private Set<Long> kickedUsers;
    private long operatorId;
}
