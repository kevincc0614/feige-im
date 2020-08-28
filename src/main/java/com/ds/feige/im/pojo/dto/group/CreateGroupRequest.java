package com.ds.feige.im.pojo.dto.group;

import com.ds.feige.im.pojo.dto.user.UserRequest;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author caedmon
 */
public class CreateGroupRequest extends UserRequest {
    @Size(max = 50,message = "群名过长")
    private String groupName;
    private int groupType;
    @Size(min = 2,max = 500,message = "群聊人数最少2个,最多500个")
    private List<Long> groupUserIds;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }


    public List<Long> getGroupUserIds() {
        return groupUserIds;
    }

    public void setGroupUserIds(List<Long> groupUserIds) {
        this.groupUserIds = groupUserIds;
    }
}
