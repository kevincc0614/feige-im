package com.ds.feige.im.common.util;

import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.entity.User;
import com.ds.feige.im.chat.dto.ChatMessage;
import com.ds.feige.im.chat.dto.group.GroupInfo;
import com.ds.feige.im.chat.entity.ConversationMessage;
import com.ds.feige.im.chat.entity.Group;
import com.ds.feige.im.enterprise.dto.EnterpriseInfo;
import com.ds.feige.im.enterprise.entity.Enterprise;
import com.ds.feige.im.oss.dto.UploadCompleteRequest;
import com.ds.feige.im.oss.entity.OSSFile;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean转换工具类
 *
 * @author DC
 */
public class BeansConverter {
    public static UserInfo userToUserInfo(User user) {
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        userInfo.setUserId(user.getId());
        return userInfo;
    }

    public static List<UserInfo> usersToUserInfos(List<User> users) {
        List<UserInfo> result = new ArrayList<>(users.size());
        users.forEach(u -> result.add(userToUserInfo(u)));
        return result;
    }

    public static GroupInfo groupToGroupInfo(Group group) {
        GroupInfo groupInfo = new GroupInfo();
        BeanUtils.copyProperties(group, groupInfo);
        groupInfo.setGroupId(group.getId());
        return groupInfo;
    }

    public static ChatMessage conversationMsgToChatMsg(ConversationMessage message) {
        ChatMessage chatMessage = new ChatMessage();
        BeanUtils.copyProperties(message, chatMessage);
        return chatMessage;
    }

    public static OSSFile uploadCompleteToOSSFile(UploadCompleteRequest request) {
        OSSFile ossFile = new OSSFile();
        BeanUtils.copyProperties(request, ossFile);
        return ossFile;
    }

    public static EnterpriseInfo enterpriseToEnterpriseInfo(Enterprise enterprise) {
        EnterpriseInfo result = new EnterpriseInfo();
        BeanUtils.copyProperties(enterprise, result);
        return result;
    }

    public static List<EnterpriseInfo> enterprisesToEnterpriseInfos(List<Enterprise> enterprises) {
        List<EnterpriseInfo> result = new ArrayList<>(enterprises.size());
        enterprises.forEach(e -> result.add(enterpriseToEnterpriseInfo(e)));
        return result;
    }
}
