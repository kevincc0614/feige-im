package com.ds.feige.im.chat.service;

import java.util.Collection;
import java.util.Set;

import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.chat.dto.group.GroupInfo;
import com.ds.feige.im.chat.dto.group.Member;
import com.ds.feige.im.constants.GroupUserRole;

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
    GroupInfo createGroup(Set<Long> userIds, String groupName, long createUserId);

    /**
     * 发布公告
     * 
     * @param groupId
     * @param operatorId
     * @param announcement
     */
    void pubAnnouncement(long groupId, long operatorId, String announcement);
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
     * @param inviteeIds
     *            被邀请人ID
     * @param operatorId
     *            邀请人ID
     */
    void inviteJoinGroup(long groupId, Set<Long> inviteeIds, long operatorId);

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
     * @param kickUsers
     * @param operatorId
     */
    void kickUser(long groupId, Set<Long> kickUsers, long operatorId);

    /**
     * 设置用户群权限,比如设置管理员
     *
     * @param groupId
     * @param userId
     * @param role
     * @param operatorId
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
    Set<Long> getUserIds(long groupId);

    /**
     * @param groupId
     * @return GroupInfo
     * @throws WarnMessageException
     *             群不存在时会抛出异常
     */
    GroupInfo getGroupInfo(long groupId);

    /**
     * 更新群组会话创建状态
     */
    void groupConversationsCreated(long groupId, long conversationId);

    /**
     * @param groupId
     */
    Collection<Member> getGroupMembers(long groupId);
}
