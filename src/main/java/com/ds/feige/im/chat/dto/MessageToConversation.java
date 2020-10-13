package com.ds.feige.im.chat.dto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import com.ds.feige.im.account.dto.UserRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * 会话消息
 *
 * @author DC
 */
@Data
@JsonIgnoreProperties
public class MessageToConversation extends UserRequest {
    @Positive(message = "targetId不合法")
    private long targetId;
    @PositiveOrZero(message = "会话类型不合法")
    private int conversationType;
    @PositiveOrZero(message = "消息类型不合法")
    private int msgType;
    @Size(min = 1, max = 10000, message = "消息内容长度超出限制")
    private String msgContent;
    private String option;
    private String senderConnectionId;
}
