package com.ds.feige.im.pojo.dto.chat;

public class ChatMsgAckResult {
    private int ackCount;

    public int getAckCount() {
        return ackCount;
    }

    public void setAckCount(int ackCount) {
        this.ackCount = ackCount;
    }

    @Override
    public String toString() {
        return "ChatMsgAckResult{" +
                "ackCount=" + ackCount +
                '}';
    }
}
