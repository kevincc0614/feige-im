package com.ds.feige.im.chat.dto.group;

import com.ds.feige.im.account.dto.UserRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * 设置群聊用户权限
 *
 * @author DC
 */
public class SetGroupUserRoleRequest extends UserRequest {
    @Positive
    private long groupId;
    @Positive
    private long roleUserId;
    @NotBlank
    private String role;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getRoleUserId() {
        return roleUserId;
    }

    public void setRoleUserId(long roleUserId) {
        this.roleUserId = roleUserId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
