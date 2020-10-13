package com.ds.feige.im.chat.dto;

import lombok.Data;

@Data
public class UserConversationInfo {
    private Long id;
    /**
     * 会话ID,双方保持一致
     */
    private Long conversationId;
    private String conversationName;
    private String conversationAvatar;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 对方ID
     */
    private Long targetId;
    /**
     * 会话类型 1 单聊  2 群聊
     */
    private Integer conversationType;
    /**
     * 会话展示优先级
     */
    private Integer priority;
    /**
     * 其他针对会话的配置信息
     **/
    private String extra;
    /**
     * 最后已读消息ID
     */
    private Long maxReadMsgId;
    /**
     * 客户端已确认最大消息ID
     */
    private Long maxAckMsgId;
}
