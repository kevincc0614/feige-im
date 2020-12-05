package com.ds.feige.im.chat.dto;

import java.util.Date;

import lombok.Data;

@Data
public class UserConversationInfo {
    /** 数据库唯一ID */
    private Long id;
    /** 会话ID */
    private Long conversationId;
    /*** 会话名 */
    private String conversationName;
    /** 会话头像 */
    private String conversationAvatar;
    /** 会话所有者ID */
    private Long userId;
    /** 目标ID */
    private Long targetId;
    /** 会话类型 */
    private Integer conversationType;
    /** 会话优先级 */
    private Integer priority;
    /** 会话私有配置信息 */
    private String extra;
    /** 最后事件时间 */
    private Date lastEventTime;
    /** 未读消息数 */
    private Integer unreadCount;
    /** 最后一条消息ID */
    private Long lastMsgId;
}
