package com.ds.feige.im.chat.dto.event;

import java.util.List;
import java.util.Map;

/**
 * 已读消息事件
 *
 * @author DC
 */
public class ReadMessageEvent {
    private long readerId;
    private Map<Long, List<Long>> senderAndMsgIds;

    public long getReaderId() {
        return readerId;
    }

    public void setReaderId(long readerId) {
        this.readerId = readerId;
    }

    public Map<Long, List<Long>> getSenderAndMsgIds() {
        return senderAndMsgIds;
    }

    public void setSenderAndMsgIds(Map<Long, List<Long>> senderAndMsgIds) {
        this.senderAndMsgIds = senderAndMsgIds;
    }
}
