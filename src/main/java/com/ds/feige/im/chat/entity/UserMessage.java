package com.ds.feige.im.chat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

import lombok.Data;

@TableName("t_user_message")
@Data
public class UserMessage extends BaseEntity {
    private Long userId;
    private Long senderId;
    private Long conversationId;
    private Long msgId;
    private Long seqId;
    private String msgContent;
    private Integer msgType;
    private Integer state;
}
