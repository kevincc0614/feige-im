package com.ds.feige.im.common.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * @author DC MQ死信队列的数据库模型
 */
@Data
@TableName("t_mq_fail_message")
public class MQFailMessage extends BaseEntity {
    private String messageId;
    private String exchange;
    private String queue;
    private String routingKeys;
    private Date produceTime;
    private String reason;
    private String body;
    private Integer status;
}
