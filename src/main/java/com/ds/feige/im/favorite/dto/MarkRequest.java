package com.ds.feige.im.favorite.dto;

import com.ds.feige.im.account.dto.UserRequest;

/**
 * @author DC
 */
public class MarkRequest extends UserRequest {
    private int markType;
    private long msgId;
    private String title;
    private String remark;

    public int getMarkType() {
        return markType;
    }

    public void setMarkType(int markType) {
        this.markType = markType;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
