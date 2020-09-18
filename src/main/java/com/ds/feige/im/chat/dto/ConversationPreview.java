package com.ds.feige.im.chat.dto;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class ConversationPreview {
    private String conversationName;
    private String conversationAvatar;
    private MessageToUser lastMsg;
    private long conversationId;
    private long lastMsgId;
    private int unreadCount;
}
