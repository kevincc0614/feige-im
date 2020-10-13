package com.ds.feige.im.chat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

import lombok.Data;

@TableName("t_conversation_message")
@Data
public class ConversationMessage extends BaseEntity {
    /**
     * 会话ID
     */
    private Long conversationId;
    /**
     * 会话类型
     */
    private Integer conversationType;
    /**
     * 发送消息的用户ID
     */
    private Long senderId;
    /**
     * 接收消息的目标ID
     */
    private Long targetId;
    /**
     * 消息ID
     */
    private Long msgId;
    /**
     * 消息内容
     */
    private String msgContent;
    /**
     * 消息类型  文本,图片,文件,语音等
     */
    private Integer msgType;
    /**
     * 消息相关参数配置,比如是否漫游,是否离线推送,是否计入未读等等
     */
    private String extra;
    /**
     * 已读人数
     */
    private Integer readCount;
    /**
     * 接收人数
     */
    private Integer receiverCount;
}
