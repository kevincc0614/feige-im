package com.ds.feige.im.pojo.dto.group;

/**
 * 用户进群事件
 *
 * @author DC
 */
public class GroupUserJoinEvent {
    private long userId;
    private long groupId;
    private long inviteUserId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getInviteUserId() {
        return inviteUserId;
    }

    public void setInviteUserId(long inviteUserId) {
        this.inviteUserId = inviteUserId;
    }
}
