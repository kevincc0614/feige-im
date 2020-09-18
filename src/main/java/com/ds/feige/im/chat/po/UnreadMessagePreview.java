package com.ds.feige.im.chat.po;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class UnreadMessagePreview {
    private long conversationId;
    private long lastMsgId;
    private int unreadCount;
    private String conversationName;
}
