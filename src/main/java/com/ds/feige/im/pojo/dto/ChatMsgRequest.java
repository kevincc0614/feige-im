package com.ds.feige.im.pojo.dto;

import com.ds.feige.im.pojo.dto.user.UserRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

public class ChatMsgRequest extends UserRequest {
    @Positive(message = "targetId不合法")
    private long targetId;
    @PositiveOrZero(message = "会话类型不合法")
    private int conversationType;
    @PositiveOrZero(message = "消息类型不合法")
    private int msgType;
    @PositiveOrZero(message = "发送消息时序ID不合法")
    private long sendSeqId;
    @Size(min = 1,max = 10000,message = "消息内容长度超出限制")
    private String msgContent;
    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public int getConversationType() {
        return conversationType;
    }

    public void setConversationType(int conversationType) {
        this.conversationType = conversationType;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public long getSendSeqId() {
        return sendSeqId;
    }

    public void setSendSeqId(long sendSeqId) {
        this.sendSeqId = sendSeqId;
    }
}
