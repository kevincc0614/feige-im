package com.ds.feige.im.chat.dto.group;

import java.util.Set;

import javax.validation.constraints.Positive;

import lombok.Data;

/**
 * 用户加群请求
 *
 * @author DC
 */
@Data
public class InviteUserJoinGroupRequest extends GroupUserRequest {
    @Positive
    private Set<Long> inviteeIds;

}
