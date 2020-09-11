package com.ds.feige.im.chat.dto;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class CreateGroupConversation {
    private long userId;
    private long groupId;
    private String name;
    private String avatar;
}
