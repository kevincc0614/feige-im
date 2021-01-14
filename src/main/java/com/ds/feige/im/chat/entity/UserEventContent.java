package com.ds.feige.im.chat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

import lombok.Data;

/**
 * @author DC
 */
@Data
@TableName("t_event_content")
public class UserEventContent extends BaseEntity {
    private String content;
}
