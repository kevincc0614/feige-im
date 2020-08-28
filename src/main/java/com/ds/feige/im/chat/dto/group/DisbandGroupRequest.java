package com.ds.feige.im.chat.dto.group;

import com.ds.feige.im.account.dto.UserRequest;

/**
 * 解散群请求
 *
 * @author DC
 */
public class DisbandGroupRequest extends UserRequest {
    private long groupId;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
}
