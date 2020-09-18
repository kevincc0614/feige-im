package com.ds.feige.im.chat.dto;

import lombok.Data;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

/**
 * @author DC
 */
@Data
public class ConversationMessageRequest {
    private long senderId;
    @Positive(message = "conversationId不合法")
    private long conversationId;
    @PositiveOrZero(message = "消息类型不合法")
    private int msgType;
    @Size(min = 1, max = 10000, message = "消息内容长度超出限制")
    private String msgContent;
    private String option;
}
