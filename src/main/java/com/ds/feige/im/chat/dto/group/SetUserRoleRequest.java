package com.ds.feige.im.chat.dto.group;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import lombok.Data;

/**
 * 设置群聊用户权限
 *
 * @author DC
 */
@Data
public class SetUserRoleRequest extends GroupUserRequest {
    @Positive
    private long memberId;
    @NotBlank
    private String role;
}
