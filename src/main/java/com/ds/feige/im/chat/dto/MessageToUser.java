package com.ds.feige.im.chat.dto;

/**
 * 用户收件箱消息DTO封装
 *
 * @author DC
 */
public class MessageToUser {
    private long userId;
    private long msgId;
    private long conversationId;
    private long senderId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }
}
