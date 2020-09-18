package com.ds.feige.im.chat.dto.group;

import lombok.Data;

/**
 * 聊天成员
 *
 * @author DC
 */
@Data
public class Member {
    private long userId;
    private String name;
    private String avatar;
    private String role;
}
