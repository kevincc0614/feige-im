package com.ds.feige.im.chat.dto;

import lombok.Data;

import java.util.List;

/**
 * 读取消息返回结果
 *
 * @author DC
 */
@Data
public class ReadMsgResult {
    private List<Long> senderIds;

    public List<Long> getSenderIds() {
        return senderIds;
    }

    public void setSenderIds(List<Long> senderIds) {
        this.senderIds = senderIds;
    }
}
