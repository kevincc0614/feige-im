package com.ds.feige.im.common.util;

import com.ds.feige.im.pojo.dto.group.GroupInfo;
import com.ds.feige.im.pojo.dto.user.UserInfo;
import com.ds.feige.im.pojo.entity.Group;
import com.ds.feige.im.pojo.entity.User;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean转换工具类
 *
 * @author DC
 */
public class BeansConverter {
    public static UserInfo userToUserInfo(User user){
        UserInfo userInfo=new UserInfo();
        BeanUtils.copyProperties(user,userInfo);
        userInfo.setUserId(user.getId());
        return userInfo;
    }
    public static List<UserInfo> usersToUserInfos(List<User> users){
        List<UserInfo> result=new ArrayList<>(users.size());
        users.forEach(u->result.add(userToUserInfo(u)));
        return result;
    }
    public static GroupInfo groupToGroupInfo(Group group){
        GroupInfo groupInfo=new GroupInfo();
        BeanUtils.copyProperties(group,groupInfo);
        groupInfo.setGroupId(group.getId());
        return groupInfo;
    }
}
