package com.ds.feige.im.chat.dto.group;

import java.util.Set;

import javax.validation.constraints.Size;

import com.ds.feige.im.account.dto.UserRequest;

import lombok.Data;

/**
 * @author caedmon
 */
@Data
public class CreateGroupRequest extends UserRequest {
    @Size(max = 50,message = "群名过长")
    private String groupName;
    private int groupType;
    @Size(min = 2,max = 500,message = "群聊人数最少2个,最多500个")
    private Set<Long> groupUserIds;
}
