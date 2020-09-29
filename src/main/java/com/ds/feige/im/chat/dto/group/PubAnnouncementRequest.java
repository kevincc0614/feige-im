package com.ds.feige.im.chat.dto.group;

import javax.validation.constraints.Size;

import lombok.Data;

/**
 * 发布群公告
 *
 * @author DC
 */
@Data
public class PubAnnouncementRequest extends GroupUserRequest {
    @Size(min = 1, max = 1024)
    private String announcement;
}
