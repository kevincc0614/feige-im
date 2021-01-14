package com.ds.feige.im.event.dto;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class UserEventData {
    /** 事件主题 */
    private String topic;
    /** 附带信息 */
    private Object content;

}
