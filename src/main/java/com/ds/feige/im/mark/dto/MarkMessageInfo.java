package com.ds.feige.im.mark.dto;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class MarkMessageInfo {
    private Long id;
    private Long conversationId;
    private Long msgId;
    private Integer msgType;
    private String msgContent;
    private Long userId;
    private String title;
    private String remark;
    private Integer markType;
}
