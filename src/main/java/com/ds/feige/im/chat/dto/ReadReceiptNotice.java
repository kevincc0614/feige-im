package com.ds.feige.im.chat.dto;

import java.util.List;

import lombok.Data;

/**
 * 已读回执通知
 *
 * @author DC
 */
@Data
public class ReadReceiptNotice {
    private long readerId;
    private List<Long> msgIds;
}
