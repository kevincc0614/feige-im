package com.ds.feige.im.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_user_message")
public class UserMessage extends BaseEntity{
    private Long userId;
    private Long conversationId;
    private Long msgId;
    private Integer state;
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
