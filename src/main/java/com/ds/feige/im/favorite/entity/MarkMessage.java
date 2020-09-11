package com.ds.feige.im.favorite.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;
import lombok.Data;

/**
 * @author DC
 */
@TableName("t_mark_message")
@Data
public class MarkMessage extends BaseEntity {
    private Long conversationId;
    private Long msgId;
    private Integer msgType;
    private String msgContent;
    private Long userId;
    private String title;
    private String remark;
    private Integer markType;
}
