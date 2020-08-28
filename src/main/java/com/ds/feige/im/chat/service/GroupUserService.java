package com.ds.feige.im.chat.service;

import com.ds.feige.im.chat.dto.group.GroupInfo;
import com.ds.feige.im.constants.GroupUserRole;

import java.util.List;

/**
 * @author DC
 */
public interface GroupUserService {
    /**
     * 创建群聊
     *
     * @param userIds
     * @param groupName
     * @param createUserId
     * @return GroupInfo
     */
    GroupInfo createGroup(List<Long> userIds, String groupName, long createUserId);

    /**
     * 解散群聊
     *
     * @param groupId
     * @param operatorId
     */
    void disbandGroup(long groupId, long operatorId);

    /**
     * 添加用户进群
     *
     * @param groupId
     * @param inviteeId
     * @param operatorId
     */
    void inviteJoinGroup(long groupId, long inviteeId,long operatorId);

    /**
     * 用户退出群聊
     *
     * @param groupId
     * @param userId
     */
    void exitGroup(long groupId, long userId);

    /**
     * 踢出用户
     *
     * @param groupId
     * @param kickUserId
     * @param operatorId
     */
    void kickUser(long groupId, long kickUserId, long operatorId);

    /**
     * 设置用户群权限,比如设置管理员
     *
     * @param groupId
     * @param role
     * @param operatorId
     */
    void setUserRole(long groupId, long userId, GroupUserRole role, long operatorId);

    /**
     * @param groupId
     * @return 是否存在
     */
    boolean groupExists(long groupId);

    /**
     * @param groupId
     * @return 用户ID列表
     */
    List<Long> getUserIds(long groupId);

    /**
     * @param groupId
     * @return GroupInfo
     */
    GroupInfo getGroupInfo(long groupId);
}
