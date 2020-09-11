package com.ds.feige.im.chat.dto.event;

import com.ds.feige.im.account.dto.UserRequest;

import javax.validation.constraints.Positive;

/**
 * 用户加群请求
 *
 * @author DC
 */
public class InviteUserJoinGroupRequest extends UserRequest {
    @Positive
    private long inviteeId;
    @Positive
    private long groupId;

    public long getInviteeId() {
        return inviteeId;
    }

    public void setInviteeId(long inviteeId) {
        this.inviteeId = inviteeId;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

}
