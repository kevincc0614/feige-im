package com.ds.feige.im.amqp;

import com.ds.feige.im.constants.DynamicQueues;
import com.ds.feige.im.pojo.dto.group.GroupCreatedEvent;
import com.ds.feige.im.pojo.dto.group.GroupUserJoinEvent;
import com.ds.feige.im.service.group.GroupUserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 群聊MQ消费者
 *
 * @author DC
 */
@Component
public class GroupUserListener {
    @Autowired
    GroupUserService groupUserService;
    @RabbitListener(queues = DynamicQueues.QueueNames.GROUP_CREATED_BROADCAST)
    public void broadcastGroupCreated(GroupCreatedEvent event) {

    }
    @RabbitListener(queues = DynamicQueues.QueueNames.GROUP_USER_JOINED_BROADCAST)
    public void broadcastUserJoinGroup(GroupUserJoinEvent event){

    }
}
