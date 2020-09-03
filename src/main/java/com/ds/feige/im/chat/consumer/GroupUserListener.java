package com.ds.feige.im.chat.consumer;

import com.ds.feige.im.chat.dto.group.GroupCreatedEvent;
import com.ds.feige.im.chat.dto.group.GroupUserJoinEvent;
import com.ds.feige.im.chat.service.GroupUserService;
import com.ds.feige.im.constants.DynamicQueues;
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
