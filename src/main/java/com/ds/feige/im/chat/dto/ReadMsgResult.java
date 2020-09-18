package com.ds.feige.im.chat.dto;

import java.util.List;

import lombok.Data;

/**
 * 读取消息返回结果
 *
 * @author DC
 */
@Data
public class ReadMsgResult {
    private List<Long> senderIds;
}
