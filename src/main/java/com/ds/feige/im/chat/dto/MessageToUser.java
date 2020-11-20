package com.ds.feige.im.chat.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * @author DC
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageToUser {
    private long conversationId;
    private String conversationName;
    private String conversationAvatar;
    private int conversationType;
    private long targetId;
    private long senderId;
    private long msgId;
    private String msgContent;
    private int msgType;
    private int readCount;
    private int receiverCount;
    private Date createTime;
    private int markType;
}
