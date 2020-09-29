package com.ds.feige.im.chat.dto;

import java.util.List;

import javax.validation.constraints.Size;

import com.ds.feige.im.account.dto.UserRequest;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class MessageAckRequest extends UserRequest {
    @Size(min = 1, max = 100)
    private List<Long> msgIds;
}
