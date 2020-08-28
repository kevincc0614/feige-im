package com.ds.feige.im.chat.dto.group;

/**
 * 用户被踢出群聊事件
 *
 * @author DC
 */
public class GroupUserKickEvent {
    private long groupId;
    private long kickUserId;
    private long operatorId;

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

    public long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(long operatorId) {
        this.operatorId = operatorId;
    }
}
