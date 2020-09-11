package com.ds.feige.im.chat.dto.event;

/**
 * @author DC
 */
public class GroupUserExitEvent {
    private long groupId;
    private long userId;
    private String userName;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
