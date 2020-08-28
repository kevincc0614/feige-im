package com.ds.feige.im.chat.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.chat.dto.group.*;
import com.ds.feige.im.chat.entity.Group;
import com.ds.feige.im.chat.entity.GroupUser;
import com.ds.feige.im.chat.mapper.GroupMapper;
import com.ds.feige.im.chat.mapper.GroupUserMapper;
import com.ds.feige.im.common.util.BeansConverter;
import com.ds.feige.im.constants.DynamicQueues;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.constants.GroupUserRole;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author DC
 */
@Service
public class GroupUserUserServiceImpl extends ServiceImpl<GroupUserMapper, GroupUser> implements GroupUserService {
    GroupMapper groupMapper;
    UserService userService;
    RabbitTemplate rabbitTemplate;
    static final Logger LOGGER= LoggerFactory.getLogger(GroupUserUserServiceImpl.class);
    @Autowired
    public GroupUserUserServiceImpl(GroupMapper groupMapper, UserService userService, RabbitTemplate rabbitTemplate){
        this.groupMapper=groupMapper;
        this.userService=userService;
        this.rabbitTemplate=rabbitTemplate;
    }
    @Override
    public GroupInfo createGroup(List<Long> userIds, String groupName, long createUserId) {
        LOGGER.info("Ready to create group:userIds={},groupName={},createUserId={}",userIds,groupName,createUserId);
        UserInfo createUser=userService.getUserById(createUserId);
        if(createUser==null){
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        //要把userIds里面的createUserId排除掉
        List<UserInfo> existsUsers=userService.getUserByIds(userIds);
        Group group=new Group();
        if(Strings.isNullOrEmpty(groupName)){
            //自动生成群名
            StringBuilder groupNameBuilder=new StringBuilder();
            for(UserInfo userInfo:existsUsers){
                groupNameBuilder.append(userInfo.getNickName());
                if(groupNameBuilder.length()<50){
                    groupNameBuilder.append(",");
                }else{
                    break;
                }
            }
            group.setName(groupNameBuilder.toString());
        }else{
            group.setName(groupName);
        }
        //TODO 生成群聊头像缩略图
        //TODO 群类型
        //TODO 群人数限制取配置
        group.setAvatar("");
        group.setType(0);
        group.setMaxUserLimit(500);
        groupMapper.insert(group);
        final long groupId=group.getId();
        List<GroupUser> groupUsers= Lists.newArrayListWithCapacity(existsUsers.size());
        if(!userIds.contains(createUserId)){
            groupUsers.add(buildGroupUser(createUser,groupId,createUserId));
        }
        existsUsers.forEach(userInfo -> {
            GroupUser groupUser=buildGroupUser(userInfo,groupId,createUserId);
            if(groupUser.getUserId()==createUserId){
                groupUser.setRole(GroupUserRole.ADMIN.name());
            }else{
                groupUser.setRole(GroupUserRole.ORDINARY.name());
            }
            groupUsers.add(groupUser);
        });
        boolean batchSaveResult=saveBatch(groupUsers);
        if(batchSaveResult){
            GroupCreatedEvent event=new GroupCreatedEvent();
            event.setGroupId(group.getId());
            //TODO 要创建聊天会话,不确定是同步创建还是异步创建
            rabbitTemplate.convertAndSend(DynamicQueues.RoutingKeys.GROUP_CREATED,event);
        }
        group=groupMapper.selectById(group.getId());
        return BeansConverter.groupToGroupInfo(group);
    }
    private GroupUser buildGroupUser(UserInfo userInfo,long groupId,long createUserId){
        GroupUser groupUser=new GroupUser();
        groupUser.setGroupId(groupId);
        groupUser.setInviteUserId(createUserId);
        groupUser.setJoinTime(new Date());
        groupUser.setUserId(userInfo.getUserId());
        groupUser.setUserName(userInfo.getNickName());
        return groupUser;
    }
    @Override
    public void disbandGroup(long groupId, long operatorId) {
        //判断用户权限
        GroupUser operator=baseMapper.getGroupUser(groupId, operatorId);
        if(!GroupUserRole.ADMIN.equals(operator)){
            throw new WarnMessageException(FeigeWarn.GROUP_PERMISSION_NOT_ALLOWED);
        }
        //删除群组
        groupMapper.deleteById(groupId);
        //删除关系
        baseMapper.disbandGroup(groupId);
        //发布事件
        GroupDisbandEvent event=new GroupDisbandEvent();
        event.setGroupId(groupId);
        event.setOperatorId(operatorId);
        rabbitTemplate.convertAndSend(DynamicQueues.RoutingKeys.GROUP_DISBANDED,event);
    }

    @Override
    public void inviteJoinGroup(long groupId, long inviteeId,long operatorId) {
        //查询user是否存在
        UserInfo inviteeUserInfo=userService.getUserById(inviteeId);
        if(inviteeUserInfo==null){
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        //TODO inviteUserId

        //查询group是否存在
        Group group=groupMapper.selectById(groupId);
        if(group==null){
            throw new WarnMessageException(FeigeWarn.GROUP_NOT_EXISTS);
        }
        //判断群人数是否超限
        List<Long> groupUserIds=baseMapper.findUserIdsByGroup(groupId);
        if(groupUserIds.contains(inviteeId)){
            LOGGER.warn("The user already in the group:inviteeId={},groupId={}",inviteeId,groupId);
            return;
        }
        if(groupUserIds.size()>=group.getMaxUserLimit()){
            throw new WarnMessageException(FeigeWarn.GROUP_USER_OVER_LIMIT);
        }
        GroupUser groupUser=new GroupUser();
        groupUser.setUserName(inviteeUserInfo.getNickName());
        groupUser.setUserId(inviteeUserInfo.getUserId());
        groupUser.setJoinTime(new Date());
        groupUser.setInviteUserId(operatorId);
        groupUser.setJoinType("USER_INVITE");
        save(groupUser);
        LOGGER.info("Add user to group success:userId={},groupId={}",inviteeId,groupId);
        //MQ通知
        GroupUserJoinEvent event=new GroupUserJoinEvent();
        event.setGroupId(groupId);
        event.setUserId(inviteeId);
        event.setInviteUserId(operatorId);
        rabbitTemplate.convertAndSend(DynamicQueues.RoutingKeys.GROUP_USER_JOINED,event);
    }

    @Override
    public void exitGroup(long groupId, long userId) {
//        rabbitTemplate.convertAndSend("group.user.exited");
    }

    @Override
    public void kickUser(long groupId, long kickUserId, long operatorId) {
        GroupUser groupUser=baseMapper.getGroupUser(groupId,kickUserId);
        if(groupUser==null){
            throw new WarnMessageException(FeigeWarn.GROUP_NOT_EXISTS,String.valueOf(kickUserId));
        }
        int deleted=baseMapper.deleteById(groupUser.getId());
        if(deleted==1){
            LOGGER.info("Kick group user:groupId={},userId={}",groupId,kickUserId);
            GroupUserKickEvent event=new GroupUserKickEvent();
            event.setGroupId(groupId);
            event.setKickUserId(kickUserId);
            event.setOperatorId(operatorId);
            rabbitTemplate.convertAndSend(DynamicQueues.RoutingKeys.GROUP_USER_KICKED,event);
        }
    }

    @Override
    public void setUserRole(long groupId, long userId, GroupUserRole role, long operatorId) {
        GroupUser operator=baseMapper.getGroupUser(groupId,operatorId);
        //判断是否有操作权限
        if(!GroupUserRole.ADMIN.name().equals(operator.getRole())){
            throw new WarnMessageException(FeigeWarn.GROUP_PERMISSION_NOT_ALLOWED);
        }
        int i=baseMapper.updateUserRole(groupId,userId,role.name());
        LOGGER.info("Update group user role:groupId={},userId={},role={}",groupId,userId,role);
//        rabbitTemplate.convertAndSend("");
    }

    @Override
    public GroupInfo getGroupInfo(long groupId) {
        Group group=groupMapper.selectById(groupId);
        if(group==null){
            throw new WarnMessageException(FeigeWarn.GROUP_NOT_EXISTS);
        }
        return BeansConverter.groupToGroupInfo(group);
    }

    @Override
    public boolean groupExists(long groupId) {
        return groupMapper.selectById(groupId)!=null;
    }

    @Override
    public List<Long> getUserIds(long groupId) {
        List<Long> userIds=baseMapper.findUserIdsByGroup(groupId);
        return userIds;
    }
}
