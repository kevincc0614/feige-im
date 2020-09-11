package com.ds.feige.im.chat.dto.group;

import java.util.List;

/**
 * 群聊会话
 *
 * @author DC
 */
public class DeleteGroupConversationsResult {
    private long groupId;
    private long conversationId;
    private List<Long> members;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }

    public List<Long> getMembers() {
        return members;
    }

    public void setMembers(List<Long> members) {
        this.members = members;
    }
}
