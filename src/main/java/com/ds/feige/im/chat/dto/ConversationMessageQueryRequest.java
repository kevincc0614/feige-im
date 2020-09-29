package com.ds.feige.im.chat.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.ds.feige.im.account.dto.UserRequest;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class ConversationMessageQueryRequest extends UserRequest {
    @Positive
    private long conversationId;
    @PositiveOrZero
    private long maxMsgId;
    @Min(1)
    @Max(100)
    private int pageSize;

}
