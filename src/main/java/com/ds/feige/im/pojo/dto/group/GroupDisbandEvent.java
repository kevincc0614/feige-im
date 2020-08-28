package com.ds.feige.im.pojo.dto.group;

/**
 * 群聊解散事件
 *
 * @author DC
 */
public class GroupDisbandEvent {
    private long groupId;
    private long operatorId;
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
}
