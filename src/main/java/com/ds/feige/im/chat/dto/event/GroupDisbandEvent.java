package com.ds.feige.im.chat.dto.event;

/**
 * 群聊解散事件
 *
 * @author DC
 */
public class GroupDisbandEvent {
    private long groupId;
    private long operatorId;
    private String operatorName;
    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
}
