package com.ds.feige.im.pojo.dto.chat;

import com.ds.feige.im.pojo.dto.user.UserRequest;

import java.util.List;

public class ChatMsgAckRequest extends UserRequest {
    private List<Long> msgIds;

    public List<Long> getMsgIds() {
        return msgIds;
    }

    public void setMsgIds(List<Long> msgIds) {
        this.msgIds = msgIds;
    }
}
