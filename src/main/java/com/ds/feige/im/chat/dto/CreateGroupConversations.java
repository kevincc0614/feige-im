package com.ds.feige.im.chat.dto;

import lombok.Data;

import java.util.List;

/**
 * @author DC
 */
@Data
public class CreateGroupConversations {
    private long groupId;
    private List<Long> members;
    private String name;
    private String avatar;
}
