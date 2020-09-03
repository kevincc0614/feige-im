package com.ds.feige.im.chat.dto;

import java.util.List;

/**
 * 已读回执通知
 *
 * @author DC
 */
public class ReadReceiptNotice {
    private long readerId;
    private List<Long> msgIds;

    public long getReaderId() {
        return readerId;
    }

    public void setReaderId(long readerId) {
        this.readerId = readerId;
    }

    public List<Long> getMsgIds() {
        return msgIds;
    }

    public void setMsgIds(List<Long> msgIds) {
        this.msgIds = msgIds;
    }
}
