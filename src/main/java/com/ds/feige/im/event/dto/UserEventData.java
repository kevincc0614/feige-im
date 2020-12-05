package com.ds.feige.im.event.dto;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class UserEventData {
    private String sourceId;
    private Long userId;
    /** 事件主题 */
    private String topic;
    /** 附带信息 */
    private Object content;

    public String getKey() {
        return this.userId + ":" + this.topic + ":" + this.sourceId + this.content;
    }

}
