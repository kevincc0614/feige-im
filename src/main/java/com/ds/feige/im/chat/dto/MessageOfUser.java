package com.ds.feige.im.chat.dto;

import lombok.Data;

/**
 * 用户收件箱消息DTO封装
 *
 * @author DC
 */
@Data
public class MessageOfUser {
    private long userId;
    private long msgId;
    private long conversationId;
    private long senderId;
    private int msgType;
    private String msgContent;
}
