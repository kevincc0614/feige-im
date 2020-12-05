package com.ds.feige.im.chat.dto;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class UserConversationDetails {

    /** 会话类型 */
    private int conversationType;
    /** 会话ID */
    private long conversationId;
    /** 会话名 */
    private String conversationName;
    /** 会话头像 */
    private String conversationAvatar;
    /** 未读消息数 */
    private int unreadCount;
    /** 目标ID */
    private long targetId;
    /** 会话展示优先级 */
    private Integer priority;
    /** 其他针对会话的配置信息 **/
    private String extra;
    /** 最后一条消息详情 */
    private MessageToUser lastMsg;
}
