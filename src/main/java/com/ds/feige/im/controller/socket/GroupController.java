package com.ds.feige.im.controller.socket;

import com.ds.feige.im.constants.GroupUserRole;
import com.ds.feige.im.constants.SocketPaths;
import com.ds.feige.im.pojo.dto.group.*;
import com.ds.feige.im.service.group.GroupUserService;
import com.ds.feige.im.socket.annotation.SocketController;
import com.ds.feige.im.socket.annotation.SocketRequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * 群聊controller
 *
 * @author DC
 */
@SocketController
public class GroupController {
    @Autowired
    GroupUserService groupUserService;
    @SocketRequestMapping(SocketPaths.CS_CREATE_GROUP)
    public GroupInfo createGroup(@RequestBody @Valid CreateGroupRequest request){
        List<Long> groupUserIds=request.getGroupUserIds();
        long createUserId=request.getUserId();
        String groupName=request.getGroupName();
        return groupUserService.createGroup(groupUserIds,groupName,createUserId);
    }
    @SocketRequestMapping(SocketPaths.CS_DISBAND_GROUP)
    public void disbandGroup(@RequestBody @Valid DisbandGroupRequest request){
        groupUserService.disbandGroup(request.getGroupId(),request.getUserId());
    }
    @SocketRequestMapping(SocketPaths.CS_USER_JOIN_GROUP)
    public void joinGroup(@RequestBody @Valid InviteUserJoinGroupRequest request){
        groupUserService.inviteJoinGroup(request.getGroupId(),request.getInviteeId(),request.getUserId());
    }
    @SocketRequestMapping(SocketPaths.CS_USER_EXIT_GROUP)
    public void exitGroup(@RequestBody @Valid UserExitGroupRequest request){
        groupUserService.exitGroup(request.getGroupId(),request.getUserId());
    }
    @SocketRequestMapping(SocketPaths.CS_KICK_GROUP_USER)
    public void kickUser(@RequestBody @Valid KickGroupUserRequest request){
        groupUserService.kickUser(request.getGroupId(),request.getKickUserId(),request.getUserId());
    }
    @SocketRequestMapping(SocketPaths.CS_SET_GROUP_USER_ROLE)
    public void setRole(@RequestBody @Valid SetGroupUserRoleRequest request){
        GroupUserRole role=GroupUserRole.valueOf(request.getRole());
        groupUserService.setUserRole(request.getGroupId(),request.getUserId(),role,request.getUserId());
    }
    @SocketRequestMapping(SocketPaths.CS_GET_GROUP_INFO)
    public GroupInfo getInfo(@RequestParam("groupId") @Valid long groupId){
        return groupUserService.getGroupInfo(groupId);
    }
    @SocketRequestMapping(SocketPaths.CS_GET_GROUP_USERS)
    public void getUsers(@RequestParam("groupId") @Valid long groupId){
        //TODO 待实现
    }
}
