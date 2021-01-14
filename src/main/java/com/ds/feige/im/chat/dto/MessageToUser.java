package com.ds.feige.im.chat.dto;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * @author DC
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageToUser {
    /** 会话ID */
    private long conversationId;
    /** 消息发送者ID */
    private long senderId;
    /** 消息ID */
    private long msgId;
    /** 消息内容 */
    private String msgContent;
    /** 消息类型 */
    private int msgType;
    /** 已读人数 */
    private int readCount;
    /** 消息应接收人数 */
    private int receiverCount;
    /** 创建时间 */
    private Date createTime;
    /** 标记类型 0无标记 1收藏 2备注 */
    private int markType;
    /** 已读回执 拉取时才会赋值 */
    private Map<Long, Long> readReceipts;
}
