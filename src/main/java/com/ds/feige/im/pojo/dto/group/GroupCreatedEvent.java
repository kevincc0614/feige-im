package com.ds.feige.im.pojo.dto.group;

/**
 * 创建群聊事件
 *
 * @author DC
 */
public class GroupCreatedEvent {
    private long groupId;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
}
