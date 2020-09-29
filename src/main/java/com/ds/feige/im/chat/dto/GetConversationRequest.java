package com.ds.feige.im.chat.dto;

import javax.validation.constraints.Positive;

import com.ds.feige.im.account.dto.UserRequest;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class GetConversationRequest extends UserRequest {
    @Positive
    private long conversationId;
}
