package com.ds.feige.im.chat.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

import lombok.Data;

@TableName("t_user_conversation")
@Data
public class UserConversation extends BaseEntity {
    /** * 会话ID,双方保持一致 */
    private Long conversationId;
    /** 会话名 */
    private String conversationName;
    /** 会话头像 */
    private String conversationAvatar;
    /** 用户ID */
    private Long userId;
    /** 对方ID */
    private Long targetId;
    /** 会话类型 1 单聊 2 群聊 */
    private Integer conversationType;
    /** * 会话展示优先级 */
    private Integer priority;
    /** * 其他针对会话的配置信息 **/
    private String extra;
    /** 最后会话事件事件 */
    private Date lastEventTime;
    // /** 未读数 */
    // private Integer unreadCount;
    // /** 最后一条消息ID */
    // private Long lastMsgId;
}
