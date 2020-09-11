package com.ds.feige.im.chat.dto.event;

import java.util.Map;

/**
 * 创建群聊事件
 *
 * @author DC
 */
public class GroupCreatedEvent {
    private long groupId;
    private String groupName;
    private long operatorId;
    private String operatorName;
    private Map<Long, String> members;

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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Map<Long, String> getMembers() {
        return members;
    }

    public void setMembers(Map<Long, String> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "GroupCreatedEvent{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", creatorId=" + operatorId +
                ", creatorName='" + operatorName + '\'' +
                ", members=" + members +
                '}';
    }
}
