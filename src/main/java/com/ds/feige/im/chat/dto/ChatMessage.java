package com.ds.feige.im.chat.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author DC
 */
@Data
public class ChatMessage {
    private long conversationId;
    /**发送消息的用户ID*/
    private long senderId;
    /**客户端消息ID,去重,排序*/
    private long sendSeqId;
    /**消息ID*/
    private long msgId;
    /**消息内容*/
    private String msgContent;
    /**消息类型  文本,图片,文件,语音等*/
    private int msgType;
    private Date createTime;
}
