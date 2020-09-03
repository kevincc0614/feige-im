package com.ds.feige.im.chat.po;

/**
 * @author DC
 */
public class SenderAndMsg {
    private Long senderId;
    private Long msgId;

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }
}
