package com.ds.feige.im.chat.dto;

import com.ds.feige.im.account.dto.UserRequest;
import lombok.Data;

import java.util.List;

/**
 * @author DC
 */
@Data
public class MessageAckRequest extends UserRequest {
    private List<Long> msgIds;
}
