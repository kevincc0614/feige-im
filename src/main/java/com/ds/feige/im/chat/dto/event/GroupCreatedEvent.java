package com.ds.feige.im.chat.dto.event;

import java.util.Map;

import lombok.Data;

/**
 * 创建群聊事件
 *
 * @author DC
 */
@Data
public class GroupCreatedEvent {
    private long groupId;
    private String groupName;
    private long operatorId;
    private String operatorName;
    private Map<Long, String> members;
}
