package com.ds.feige.im.chat.dto.group;

import com.ds.feige.im.account.dto.UserRequest;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class GroupUserRequest extends UserRequest {
    protected long groupId;
}
