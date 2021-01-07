package com.ds.feige.im.chat.dto.event;

import java.util.Date;
import java.util.Set;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class ConversationMessageEvent {
    private long conversationId;
    private long senderId;
    private long targetId;
    private Set<String> excludeConnectionIds;
    private long msgId;
    private String msgContent;
    private Integer msgType;
    private String extra;
    private int receiverCount;
    private Date createTime;
}
