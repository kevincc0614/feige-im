package com.ds.feige.im.pojo.dto.group;

import com.ds.feige.im.pojo.dto.user.UserRequest;

import javax.validation.constraints.Positive;

/**
 * 用户退出群聊请求
 *
 * @author DC
 */
public class UserExitGroupRequest extends UserRequest {
    @Positive
    private long groupId;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
}
