package com.ds.feige.im.chat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

import lombok.Data;

@TableName("t_group")
@Data
public class Group extends BaseEntity {
    private String name;
    private String avatar;
    private Integer type;
    private Long conversationId;
    private Integer maxUserLimit;
    private String announcement;
    private Long announcePubTime;
    private String announcePubUser;
}
