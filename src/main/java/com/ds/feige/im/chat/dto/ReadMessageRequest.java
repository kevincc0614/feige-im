package com.ds.feige.im.chat.dto;

import com.ds.feige.im.account.dto.UserRequest;
import lombok.Data;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 已读回执
 *
 * @author DC
 */
@Data
public class ReadMessageRequest extends UserRequest {
    @Size(min = 1, max = 500)
    private List<Long> msgIds;
    @Positive
    private long conversationId;
}
