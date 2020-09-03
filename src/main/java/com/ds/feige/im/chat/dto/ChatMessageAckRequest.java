package com.ds.feige.im.chat.dto;

import com.ds.feige.im.account.dto.UserRequest;

import java.util.List;

public class ChatMessageAckRequest extends UserRequest {
    private List<Long> msgIds;

    public List<Long> getMsgIds() {
        return msgIds;
    }

    public void setMsgIds(List<Long> msgIds) {
        this.msgIds = msgIds;
    }
}
