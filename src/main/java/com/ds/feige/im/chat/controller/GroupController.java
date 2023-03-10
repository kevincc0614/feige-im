package com.ds.feige.im.chat.controller;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.ds.feige.im.chat.dto.group.*;
import com.ds.feige.im.chat.service.GroupUserService;
import com.ds.feige.im.constants.GroupUserRole;
import com.ds.feige.im.constants.SocketPaths;
import com.ds.feige.im.gateway.domain.UserState;
import com.ds.feige.im.gateway.service.SessionUserService;
import com.ds.feige.im.gateway.socket.annotation.SocketController;
import com.ds.feige.im.gateway.socket.annotation.SocketRequestMapping;

/**
 * 群聊controller
 *
 * @author DC
 */
@SocketController
public class GroupController {
    @Autowired
    GroupUserService groupUserService;
    @Autowired
    SessionUserService sessionUserService;
    @SocketRequestMapping(SocketPaths.CS_CREATE_GROUP)
    public GroupInfo createGroup(@RequestBody @Valid CreateGroupRequest request) {
        Set<Long> groupUserIds = request.getGroupUserIds();
        long createUserId = request.getUserId();
        String groupName = request.getGroupName();
        return groupUserService.createGroup(groupUserIds, groupName, createUserId);
    }

    @SocketRequestMapping(SocketPaths.CS_PUB_GROUP_ANNOUNCEMENT)
    public void pubAnnouncement(@RequestBody @Valid PubAnnouncementRequest request) {
        groupUserService.pubAnnouncement(request.getGroupId(), request.getUserId(), request.getAnnouncement());
    }
    @SocketRequestMapping(SocketPaths.CS_DISBAND_GROUP)
    public void disbandGroup(@RequestBody @Valid GroupUserRequest request) {
        groupUserService.disbandGroup(request.getGroupId(), request.getUserId());
    }

    @SocketRequestMapping(SocketPaths.CS_USER_JOIN_GROUP)
    public void joinGroup(@RequestBody @Valid InviteUserJoinGroupRequest request) {
        groupUserService.inviteJoinGroup(request.getGroupId(), request.getInviteeIds(), request.getUserId());
    }

    @SocketRequestMapping(SocketPaths.CS_USER_EXIT_GROUP)
    public void exitGroup(@RequestBody @Valid GroupUserRequest request) {
        groupUserService.exitGroup(request.getGroupId(), request.getUserId());
    }

    @SocketRequestMapping(SocketPaths.CS_KICK_GROUP_USER)
    public void kickUser(@RequestBody @Valid KickGroupUserRequest request) {
        groupUserService.kickUser(request.getGroupId(), request.getKickUserIds(), request.getUserId());
    }

    @SocketRequestMapping(SocketPaths.CS_SET_GROUP_USER_ROLE)
    public void setRole(@RequestBody @Valid SetUserRoleRequest request) {
        GroupUserRole role = GroupUserRole.valueOf(request.getRole());
        groupUserService.setUserRole(request.getGroupId(), request.getUserId(), role, request.getUserId());
    }

    @SocketRequestMapping(SocketPaths.CS_GET_GROUP_INFO)
    public GroupInfo getInfo(@RequestParam("groupId") @Valid long groupId) {
        return groupUserService.getGroupInfo(groupId);
    }

    @SocketRequestMapping(SocketPaths.CS_GET_GROUP_MEMBERS)
    public Collection<Member> getMembers(@RequestParam("groupId") @Valid long groupId) {
        Collection<Member> members = groupUserService.getGroupMembers(groupId);
        Map<Long, UserState> stateMap = sessionUserService.getUserStates(members);
        members.forEach(m -> m.setState(stateMap.get(m.getUserId())));
        return members;
    }
}
