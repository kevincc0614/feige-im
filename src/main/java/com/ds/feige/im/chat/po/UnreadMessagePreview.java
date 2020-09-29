package com.ds.feige.im.chat.po;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class UnreadMessagePreview {
    protected long conversationId;
    protected String conversationName;
    protected String conversationAvatar;
    protected long lastMsgId;
    protected int unreadCount;
    protected long targetId;
}
