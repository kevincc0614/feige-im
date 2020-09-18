package com.ds.feige.im.chat.dto.group;

import java.util.Set;

import javax.validation.constraints.Positive;

import lombok.Data;

/**
 * 踢人请求
 *
 * @author DC
 */
@Data
public class KickGroupUserRequest extends GroupUserRequest {
    @Positive
    private Set<Long> kickUserIds;
}
