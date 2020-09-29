package com.ds.feige.im.chat.dto.group;

import com.ds.feige.im.common.domain.UserIdHolder;
import com.ds.feige.im.gateway.domain.UserState;

import lombok.Data;

/**
 * 聊天成员
 *
 * @author DC
 */
@Data
public class Member implements UserIdHolder {
    private Long userId;
    private String name;
    private String avatar;
    private String role;
    private UserState state;

}
