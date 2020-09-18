package com.ds.feige.im.chat.dto.event;

import lombok.Data;

import java.util.Set;

/**
 * 群聊解散事件
 *
 * @author DC
 */
@Data
public class GroupDisbandEvent {
    private long groupId;
    private long operatorId;
    private String operatorName;
    private Set<Long> members;
}
