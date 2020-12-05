package com.ds.feige.im.chat.dto;

import java.util.List;

import com.ds.feige.im.account.dto.UserRequest;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class RecentConversationsRequest extends UserRequest {
    private List<Long> lastSendMsgIds;
    private Long lastEventTime = 0L;
}
