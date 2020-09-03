package com.ds.feige.im.chat.dto;

public class ChatMessageAckResult {
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
