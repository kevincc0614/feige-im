package com.ds.feige.im.chat.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author DC
 */
@Data
public class MessageToUser {
    private long conversationId;
    private long senderId;
    private long msgId;
    private String msgContent;
    private int msgType;
    private int readCount;
    private int receiverCount;
    private Date createTime;
}
