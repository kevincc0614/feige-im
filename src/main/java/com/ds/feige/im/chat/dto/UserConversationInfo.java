package com.ds.feige.im.chat.dto;

public class UserConversationInfo {
    private Long id;
    /**会话ID,双方保持一致*/
    private Long conversationId;
    /**用户ID*/
    private Long userId;
    /**对方ID*/
    private Long targetId;
    /**会话类型 1 单聊  2 群聊*/
    private Integer conversationType;
    /**会话展示优先级*/
    private Integer priority;
    /**其他针对会话的配置信息**/
    private String meta;
    /**最后已读消息ID*/
    private Long maxReadMsgId;
    /**客户端已确认最大消息ID*/
    private Long maxAckMsgId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Integer getConversationType() {
        return conversationType;
    }

    public void setConversationType(Integer conversationType) {
        this.conversationType = conversationType;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Long getMaxReadMsgId() {
        return maxReadMsgId;
    }

    public void setMaxReadMsgId(Long maxReadMsgId) {
        this.maxReadMsgId = maxReadMsgId;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public Long getMaxAckMsgId() {
        return maxAckMsgId;
    }

    public void setMaxAckMsgId(Long maxAckMsgId) {
        this.maxAckMsgId = maxAckMsgId;
    }

    @Override
    public String toString() {
        return "UserConversationInfo{" +
                "id=" + id +
                ", conversationId=" + conversationId +
                ", userId=" + userId +
                ", targetId=" + targetId +
                ", conversationType=" + conversationType +
                ", priority=" + priority +
                ", meta='" + meta + '\'' +
                ", maxReadMsgId=" + maxReadMsgId +
                ", maxAckMsgId=" + maxAckMsgId +
                '}';
    }
}
