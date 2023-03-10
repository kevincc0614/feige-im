package com.ds.feige.im.chat.dto.group;

import lombok.Data;

/**
 * 聊天群组信息
 *
 * @author DC
 */
@Data
public class GroupInfo {
    private long groupId;
    private String name;
    private String avatar;
    private int type;
    private Long conversationId;
    private int maxUserLimit;
    private String announcement;
    private String extra;
}
