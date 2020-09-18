package com.ds.feige.im.chat.dto.event;

import java.util.Set;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class ConversationMessageEvent {
    private long conversationId;
    private int conversationType;
    private long senderId;
    private Set<Long> receiverIds;
    private long msgId;
    private String msgContent;
    private Integer msgType;
    private String option;
    private int readCount;
    private int receiverCount;
}
