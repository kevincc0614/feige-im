package com.ds.feige.im.pojo.dto.message;

public class SendMsgResult {
    private Long msgId;
    private Long conversationId;

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    public String toString() {
        return "SendMsgResult{" +
                "msgId=" + msgId +
                ", conversationId=" + conversationId +
                '}';
    }
}
