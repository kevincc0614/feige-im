package com.ds.feige.im.chat.dto;

import com.ds.feige.im.account.dto.UserRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 已读回执
 *
 * @author DC
 */
public class ReadMessageRequest extends UserRequest {
    @Size(min = 1, max = 500)
    private List<Long> msgIds;
    @Positive
    private long conversationId;

    public List<Long> getMsgIds() {
        return msgIds;
    }

    public void setMsgIds(List<Long> msgIds) {
        this.msgIds = msgIds;
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }
}
