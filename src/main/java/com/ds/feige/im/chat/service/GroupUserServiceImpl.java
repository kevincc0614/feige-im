package com.ds.feige.im.chat.service;

import java.util.*;

import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.chat.dto.event.*;
import com.ds.feige.im.chat.dto.group.GroupInfo;
import com.ds.feige.im.chat.dto.group.Member;
import com.ds.feige.im.chat.entity.Group;
import com.ds.feige.im.chat.entity.GroupUser;
import com.ds.feige.im.chat.mapper.GroupMapper;
import com.ds.feige.im.chat.mapper.GroupUserMapper;
import com.ds.feige.im.common.util.BeansConverter;
import com.ds.feige.im.common.util.JsonUtils;
import com.ds.feige.im.constants.AMQPConstants;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.constants.GroupUserRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DC
 */
@Service
@Slf4j
public class GroupUserServiceImpl extends ServiceImpl<GroupUserMapper, GroupUser> implements GroupUserService {
    GroupMapper groupMapper;
    UserService userService;
    RabbitTemplate rabbitTemplate;
    RedissonClient redissonClient;

    @Autowired
    public GroupUserServiceImpl(GroupMapper groupMapper, UserService userService, RabbitTemplate rabbitTemplate,
        RedissonClient redissonClient) {
        this.groupMapper = groupMapper;
        this.userService = userService;
        this.rabbitTemplate = rabbitTemplate;
        this.redissonClient = redissonClient;
    }

    @Override
    public GroupInfo createGroup(Set<Long> userIds, String groupName, long createUserId) {
        log.info("Ready to create group:userIds={},groupName={},createUserId={}", userIds, groupName, createUserId);
        UserInfo createUser = userService.getUserById(createUserId);
        if (createUser == null) {
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        userIds.add(createUserId);
        // 要把userIds里面的createUserId排除掉
        Collection<UserInfo> memberUsers = userService.getUserByIds(userIds);
        Group group = new Group();
        Map<Long, String> members = Maps.newHashMap();
        if (Strings.isNullOrEmpty(groupName)) {
            // 自动生成群名
            StringBuilder groupNameBuilder = new StringBuilder();
            for (UserInfo member : memberUsers) {
                groupNameBuilder.append(member.getNickName());
                members.put(member.getUserId(), member.getNickName());
                if (groupNameBuilder.length() < 50) {
                    groupNameBuilder.append(",");
                } else {
                    break;
                }
            }
            group.setName(groupNameBuilder.toString());
        } else {
            group.setName(groupName);
        }
        // TODO 生成群聊头像缩略图
        // TODO 群类型
        // TODO 群人数限制取配置
        group.setAvatar("");
        group.setType(0);
        group.setMaxUserLimit(500);
        groupMapper.insert(group);
        final long groupId = group.getId();
        // 构建GroupUser列表
        List<GroupUser> groupUsers = Lists.newArrayListWithCapacity(memberUsers.size());
        memberUsers.forEach(userInfo -> {
            GroupUser groupUser = buildGroupUser(userInfo, groupId, createUserId);
            if (groupUser.getUserId() == createUserId) {
                groupUser.setRole(GroupUserRole.ADMIN.name());
            } else {
                groupUser.setRole(GroupUserRole.ORDINARY.name());
            }
            groupUsers.add(groupUser);
        });
        boolean batchSaveResult = saveBatch(groupUsers);
        if (batchSaveResult) {
            // 发布群聊创建事件到MQ
            GroupCreatedEvent event = new GroupCreatedEvent();
            event.setGroupId(group.getId());
            event.setGroupName(groupName);
            event.setOperatorName(createUser.getNickName());
            event.setOperatorId(createUserId);
            event.setMembers(members);
            rabbitTemplate.convertAndSend(AMQPConstants.RoutingKeys.GROUP_CREATED, event);
        }
        group = groupMapper.selectById(group.getId());
        // RBucket<Group> groupInfoRBucket=redissonClient.getBucket(CacheKeys.CHAT_GROUP_INFO+groupId);
        // groupInfoRBucket.set(group,30, TimeUnit.DAYS);
        return BeansConverter.groupToGroupInfo(group);
    }

    @Override
    public void pubAnnouncement(long groupId, long operatorId, String announcement) {
        Group group = checkGroupExists(groupId);
        GroupUser operator = checkGroupAdmin(groupId, operatorId);
        try {
            HashMap map = JsonUtils.jsonToBean(announcement, HashMap.class);
            map.put("pubUserId", operator.getUserId());
            map.put("pubTime", System.currentTimeMillis());
            group.setAnnouncement(JsonUtils.toJson(map));
            groupMapper.updateById(group);
        } catch (JsonProcessingException e) {
            throw new WarnMessageException(FeigeWarn.REQUEST_VALIDATE_ERROR);
        }

    }

    private GroupUser buildGroupUser(UserInfo userInfo, long groupId, long createUserId) {
        GroupUser groupUser = new GroupUser();
        groupUser.setGroupId(groupId);
        groupUser.setInviteUserId(createUserId);
        groupUser.setJoinTime(new Date());
        groupUser.setUserId(userInfo.getUserId());
        groupUser.setUserName(userInfo.getNickName());
        return groupUser;
    }

    private Group checkGroupExists(long groupId) {
        // 查询group是否存在
        Group group = groupMapper.selectById(groupId);
        if (group == null) {
            throw new WarnMessageException(FeigeWarn.GROUP_NOT_EXISTS, String.valueOf(groupId));
        }
        return group;
    }

    private GroupUser checkGroupAdmin(long groupId, long operatorId) {
        // 判断用户权限
        GroupUser operator = baseMapper.getGroupUser(groupId, operatorId);
        if (!GroupUserRole.ADMIN.name().equals(operator.getRole())) {
            throw new WarnMessageException(FeigeWarn.GROUP_PERMISSION_NOT_ALLOWED);
        }
        return operator;
    }
    @Override
    public void disbandGroup(long groupId, long operatorId) {
        Group group = checkGroupExists(groupId);
        // 判断用户权限
        GroupUser operator = checkGroupAdmin(groupId, operatorId);
        disbandGroup(groupId);
    }

    @Override
    public void disbandGroup(long groupId) {
        // 删除群组
        groupMapper.deleteById(groupId);
        // 删除关系
        baseMapper.disbandGroup(groupId);
        // 发布事件
        GroupDisbandEvent event = new GroupDisbandEvent();
        event.setGroupId(groupId);
        // TODO operatorId=0表示系统解散
        event.setOperatorId(0L);
        rabbitTemplate.convertAndSend(AMQPConstants.RoutingKeys.GROUP_DISBANDED, event);
    }

    @Override
    public void inviteJoinGroup(long groupId, Set<Long> inviteeIds, long operatorId) {
        // 查询group是否存在
        Group group = checkGroupExists(groupId);
        GroupUser operatorGroupUser = baseMapper.getGroupUser(groupId, operatorId);
        if (operatorGroupUser == null) {
            throw new WarnMessageException(FeigeWarn.GROUP_USER_NOT_EXISTS);
        }
        // 判断群人数是否超限
        Set<Long> groupMembers = baseMapper.findUserIdsByGroup(groupId);
        if (groupMembers.size() >= group.getMaxUserLimit()) {
            throw new WarnMessageException(FeigeWarn.GROUP_USER_OVER_LIMIT);
        }
        // 删除已经在群的ID
        inviteeIds.removeAll(groupMembers);
        // 查询user是否存在
        Collection<UserInfo> inviteeUserInfos = userService.getUserByIds(inviteeIds);
        if (inviteeUserInfos.isEmpty()) {
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        // MQ通知
        GroupInviteUsersJoinEvent event = new GroupInviteUsersJoinEvent();
        event.setGroupId(groupId);
        event.setOperatorId(operatorId);
        event.setOperatorName(operatorGroupUser.getUserName());
        Map<Long, String> inviteUsers = Maps.newHashMap();
        //
        List<GroupUser> newGroupUsers = Lists.newArrayListWithCapacity(inviteeUserInfos.size());
        for (UserInfo inviteeUser : inviteeUserInfos) {
            GroupUser groupUser = new GroupUser();
            groupUser.setGroupId(groupId);
            groupUser.setUserName(inviteeUser.getNickName());
            groupUser.setUserId(inviteeUser.getUserId());
            groupUser.setJoinTime(new Date());
            groupUser.setInviteUserId(operatorId);
            groupUser.setJoinType("user_invite");
            groupUser.setRole(GroupUserRole.ORDINARY.name());
            inviteUsers.put(groupUser.getUserId(), groupUser.getUserName());
            newGroupUsers.add(groupUser);
        }
        event.setInviteUsers(inviteUsers);
        saveBatch(newGroupUsers);
        log.info("Add user to group success:userIds={},groupId={}", inviteeIds, groupId);
        rabbitTemplate.convertAndSend(AMQPConstants.RoutingKeys.GROUP_USER_JOINED, event);
    }

    @Override
    public void exitGroup(long groupId, long userId) {
        GroupUser groupUser = baseMapper.getGroupUser(groupId, userId);
        if (groupUser == null) {
            log.error("User not in the group,can not exit:groupId={},userId={}", groupId, userId);
            return;
        }
        int i = baseMapper.deleteByGroupAndUserId(groupId, userId);
        if (i > 0) {
            GroupUserExitEvent event = new GroupUserExitEvent();
            event.setGroupId(groupId);
            event.setUserId(userId);
            event.setUserName(groupUser.getUserName());
            // TODO 如果是唯一管理员退出,需要重新设置一名管理员
            rabbitTemplate.convertAndSend(AMQPConstants.RoutingKeys.GROUP_USER_EXITED, event);
        }
    }

    @Override
    public void kickUser(long groupId, Set<Long> kickUsers, long operatorId) {
        List<GroupUser> groupUsers = baseMapper.findByGroupAndUserIds(groupId, kickUsers);
        if (groupUsers == null || groupUsers.isEmpty()) {
            log.warn("Kick operation has users:groupId={},kickUsers={},operatorId={}", groupId, kickUsers, operatorId);
            return;
        }
        // 确定要被踢的用户
        Set<Long> kickedUsers = Sets.newHashSet();
        groupUsers.forEach(u -> kickedUsers.add(u.getUserId()));
        int deleted = baseMapper.deleteBatchIds(kickUsers);
        if (deleted >= 1) {
            log.info("Kick group user:groupId={},kickUsers={}", groupId, kickedUsers);
            GroupUserKickEvent event = new GroupUserKickEvent();
            event.setGroupId(groupId);
            event.setKickedUsers(kickUsers);
            event.setOperatorId(operatorId);
            rabbitTemplate.convertAndSend(AMQPConstants.RoutingKeys.GROUP_USER_KICKED, event);
        }
    }

    @Override
    public void setUserRole(long groupId, long userId, GroupUserRole role, long operatorId) {
        GroupUser operator = baseMapper.getGroupUser(groupId, operatorId);
        // 判断是否有操作权限
        if (!GroupUserRole.ADMIN.name().equals(operator.getRole())) {
            throw new WarnMessageException(FeigeWarn.GROUP_PERMISSION_NOT_ALLOWED);
        }
        int i = baseMapper.updateUserRole(groupId, userId, role.name());
        log.info("Update group user role:groupId={},userId={},role={}", groupId, userId, role);
        // rabbitTemplate.convertAndSend("");
    }

    @Override
    public GroupInfo getGroupInfo(long groupId) {
        // RBucket<Group> groupRBucket=redissonClient.getBucket(CacheKeys.CHAT_GROUP_INFO+groupId);
        // Group group=null;
        // if(groupRBucket==null){
        // group=groupMapper.selectById(groupId);
        // groupRBucket.set(group,30,TimeUnit.DAYS);
        // }else {
        // group=groupRBucket.get();
        // }
        Group group = checkGroupExists(groupId);
        return BeansConverter.groupToGroupInfo(group);
    }

    @Override
    public boolean groupExists(long groupId) {
        return groupMapper.selectById(groupId) != null;
    }

    @Override
    public Set<Long> getUserIds(long groupId) {
        Set<Long> userIds = baseMapper.findUserIdsByGroup(groupId);
        return userIds;
    }

    @Override
    public void groupConversationsCreated(long groupId, long conversationId) {
        groupMapper.conversationCreated(groupId, conversationId);
        log.info("Group conversations created:groupId={},conversationId={}", groupId, conversationId);
    }

    @Override
    public Collection<Member> getGroupMembers(long groupId) {
        List<GroupUser> groupUsers = baseMapper.findByGroup(groupId);
        List<Member> members = Lists.newArrayListWithCapacity(groupUsers.size());
        groupUsers.forEach(groupUser -> {
            Member member = new Member();
            member.setName(groupUser.getUserName());
            member.setUserId(groupUser.getUserId());
            member.setRole(groupUser.getRole());
            members.add(member);
        });
        return members;
    }
}
