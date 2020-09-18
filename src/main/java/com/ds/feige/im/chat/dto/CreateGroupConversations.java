package com.ds.feige.im.chat.dto;

import lombok.Data;

import java.util.Set;

/**
 * @author DC
 */
@Data
public class CreateGroupConversations {
    private long groupId;
    private Set<Long> members;
    private String name;
    private String avatar;
}
