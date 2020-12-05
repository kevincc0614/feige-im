package com.ds.feige.im.chat.dto.event;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Data;

/**
 * 已读消息事件
 *
 * @author DC
 */
@Data
public class ReadMessageEvent {
    private long readerId;
    private Map<Long, List<Long>> senderAndMsgIds;
    private long readTime;
    private Long conversationId;
    /** 排除推送的链接ID */
    private Set<String> excludeConnectionIds;
}
