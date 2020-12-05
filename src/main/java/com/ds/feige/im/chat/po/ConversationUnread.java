package com.ds.feige.im.chat.po;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class ConversationUnread {
    private long conversationId;
    private Long lastUnreadMsgId;
    private Integer unreadCount;
}
