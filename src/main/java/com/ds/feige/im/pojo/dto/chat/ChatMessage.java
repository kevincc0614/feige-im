package com.ds.feige.im.pojo.dto.chat;

import java.util.Date;

/**
 * @author DC
 */
public class ChatMessage {
    private long conversationId;
    /**发送消息的用户ID*/
    private long senderId;
    /**客户端消息ID,去重,排序*/
    private long sendSeqId;
    /**消息ID*/
    private long msgId;
    /**消息内容*/
    private String msgContent;
    /**消息类型  文本,图片,文件,语音等*/
    private int msgType;
    private Date createTime;

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

    public long getSendSeqId() {
        return sendSeqId;
    }

    public void setSendSeqId(long sendSeqId) {
        this.sendSeqId = sendSeqId;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "ChatMsg{" +
                "conversationId=" + conversationId +
                ", senderId=" + senderId +
                ", sendSeqId=" + sendSeqId +
                ", msgId=" + msgId +
                ", msgContent='" + msgContent + '\'' +
                ", msgType=" + msgType +
                ", createTime=" + createTime +
                '}';
    }
}
