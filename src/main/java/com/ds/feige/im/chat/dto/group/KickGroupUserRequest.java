package com.ds.feige.im.chat.dto.group;

import com.ds.feige.im.account.dto.UserRequest;

import javax.validation.constraints.Positive;

/**
 * 踢人请求
 *
 * @author DC
 */
public class KickGroupUserRequest extends UserRequest {
    @Positive
    private long groupId;
    @Positive
    private long kickUserId;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getKickUserId() {
        return kickUserId;
    }

    public void setKickUserId(long kickUserId) {
        this.kickUserId = kickUserId;
    }
}
