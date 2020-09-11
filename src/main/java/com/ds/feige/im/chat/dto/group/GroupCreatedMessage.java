package com.ds.feige.im.chat.dto.group;

import java.util.Map;

/**
 * @author DC
 */
public class GroupCreatedMessage {
    private long groupId;
    private String groupName;
    private long operatorId;
    private String operatorName;
    private Map<Long, String> members;
    private long conversationId;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public Map<Long, String> getMembers() {
        return members;
    }

    public void setMembers(Map<Long, String> members) {
        this.members = members;
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }
}
