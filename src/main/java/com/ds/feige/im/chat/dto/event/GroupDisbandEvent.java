package com.ds.feige.im.chat.dto.event;

import java.util.Set;

import lombok.Data;

/**
 * 群聊解散事件
 *
 * @author DC
 */
@Data
public class GroupDisbandEvent {
    private long groupId;
    private long operatorId;
    private Set<Long> members;
}
