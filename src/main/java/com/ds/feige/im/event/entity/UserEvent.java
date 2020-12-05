package com.ds.feige.im.event.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

import lombok.Data;

/**
 * @author DC
 */
@Data
@TableName("t_user_event")
public class UserEvent extends BaseEntity {
    private Long userId;
    private String sourceId;
    /** 用户空间的连续自增ID */
    private Long seqId;
    /** 事件主题 */
    private String topic;
    /** 附带信息 */
    private String content;
    /** 事件消费状态 */
    private Integer state;
}
