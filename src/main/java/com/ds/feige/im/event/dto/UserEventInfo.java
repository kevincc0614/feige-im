package com.ds.feige.im.event.dto;

import java.util.Date;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class UserEventInfo {
    private Long userId;
    /** 用户空间的连续自增ID */
    private Long seqId;
    /** 事件主题 */
    private String topic;
    /** 附带信息 */
    private String content;
    private Date createTime;
}
