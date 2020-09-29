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
    private String conversationName;
    private String conversationAvatar;
    private int conversationType;
    private long targetId;
    private long senderId;
    private Set<Long> receiverIds;
    private long msgId;
    private String msgContent;
    private Integer msgType;
    private String option;
    private int readCount;
    private int receiverCount;
    private Date createTime;
}
