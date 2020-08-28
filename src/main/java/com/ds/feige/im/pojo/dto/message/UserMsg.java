package com.ds.feige.im.pojo.dto.message;

/**
 * 用户收件箱消息DTO封装
 *
 * @author DC
 */
public class UserMsg {
    private long userId;
    private long msgId;
    private long conversationId;

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
}
