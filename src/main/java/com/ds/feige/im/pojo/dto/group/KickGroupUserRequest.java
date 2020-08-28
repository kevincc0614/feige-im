package com.ds.feige.im.pojo.dto.group;

import com.ds.feige.im.pojo.dto.user.UserRequest;

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
