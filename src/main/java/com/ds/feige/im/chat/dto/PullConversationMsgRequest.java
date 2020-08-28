package com.ds.feige.im.chat.dto;

import com.ds.feige.im.account.dto.UserRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

public class PullConversationMsgRequest extends UserRequest {
    @Positive
    private long conversationId;
    @PositiveOrZero
    private long maxMsgId;
    @Size(min = 1,max = 100,message = "一次最多查询1到100条消息")
    private int pageSize;

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }

    public long getMaxMsgId() {
        return maxMsgId;
    }

    public void setMaxMsgId(long maxMsgId) {
        this.maxMsgId = maxMsgId;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}
