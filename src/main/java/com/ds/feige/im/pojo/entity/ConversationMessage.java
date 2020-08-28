package com.ds.feige.im.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_conversation_message")
public class ConversationMessage extends BaseEntity {
    /**会话ID*/
    private Long conversationId;
    /**会话类型*/
    private Integer conversationType;
    /**发送消息的用户ID*/
    private Long senderId;
    /**接收消息的目标ID*/
    private Long targetId;
    /**客户端消息ID,去重,排序*/
    private Long sendSeqId;
    /**消息ID*/
    private Long msgId;
    /**消息内容*/
    private String msgContent;
    /**消息类型  文本,图片,文件,语音等*/
    private Integer msgType;

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Integer getConversationType() {
        return conversationType;
    }

    public void setConversationType(Integer conversationType) {
        this.conversationType = conversationType;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public Long getSendSeqId() {
        return sendSeqId;
    }

    public void setSendSeqId(Long sendSeqId) {
        this.sendSeqId = sendSeqId;
    }

}
