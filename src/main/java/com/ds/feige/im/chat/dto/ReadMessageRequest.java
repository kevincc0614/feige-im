package com.ds.feige.im.chat.dto;

import java.util.Set;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.ds.feige.im.account.dto.UserRequest;

import lombok.Data;

/**
 * 已读回执
 *
 * @author DC
 */
@Data
public class ReadMessageRequest extends UserRequest {
    @Size(min = 1, max = 100)
    private Set<Long> msgIds;
    @Positive
    private long conversationId;
    private String readerConnectionId;
}
