package com.ds.feige.im.chat.dto.event;

/**
 * 用户被踢出群聊事件
 *
 * @author DC
 */
public class GroupUserKickEvent {
    private long groupId;
    private long kickUserId;
    private String kickUserName;
    private long operatorId;
    private String operatorName;
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

    public String getKickUserName() {
        return kickUserName;
    }

    public void setKickUserName(String kickUserName) {
        this.kickUserName = kickUserName;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
}
