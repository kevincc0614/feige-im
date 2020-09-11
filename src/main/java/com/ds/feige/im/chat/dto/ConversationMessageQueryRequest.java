package com.ds.feige.im.chat.dto;

import com.ds.feige.im.account.dto.UserRequest;
import lombok.Data;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

/**
 * @author DC
 */
@Data
public class ConversationMessageQueryRequest extends UserRequest {
    @Positive
    private long conversationId;
    @PositiveOrZero
    private long maxMsgId;
    @Size(min = 1, max = 100, message = "一次最多查询1到100条消息")
    private int pageSize;

}
