package com.ds.feige.im.chat.consumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.chat.dto.MessageOfUser;
import com.ds.feige.im.chat.dto.MessageToUser;
import com.ds.feige.im.chat.dto.ReadReceiptNotice;
import com.ds.feige.im.chat.dto.UserConversationInfo;
import com.ds.feige.im.chat.dto.event.ConversationMessageEvent;
import com.ds.feige.im.chat.dto.event.ReadMessageEvent;
import com.ds.feige.im.chat.service.ConversationService;
import com.ds.feige.im.chat.service.GroupUserService;
import com.ds.feige.im.chat.service.UserMessageService;
import com.ds.feige.im.common.util.JsonUtils;
import com.ds.feige.im.constants.AMQPConstants;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.constants.MsgType;
import com.ds.feige.im.event.constants.Topics;
import com.ds.feige.im.event.dto.UserEventData;
import com.ds.feige.im.event.service.UserEventService;
import com.ds.feige.im.gateway.service.SessionUserService;
import com.ds.feige.im.push.dto.PushMessage;
import com.ds.feige.im.push.service.PushService;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * 会话消息相关MQ处理
 *
 * @author caedmon
 */
@Component
@Slf4j
public class ChatMessageListener {
    @Autowired
    UserMessageService userMessageService;
    @Autowired
    GroupUserService groupUserService;
    @Autowired
    SessionUserService sessionUserService;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    ConversationService conversationService;
    @Autowired
    UserEventService userEventService;
    @Autowired
    PushService pushService;
    @Autowired
    UserService userService;

    @RabbitListener(queues = AMQPConstants.QueueNames.CONVERSATION_SEND_MESSAGE_SYNC)
    public void syncToUserMessages(@Payload ConversationMessageEvent event) {
        log.info("Sync conversation message to users:event={}", event);
        Set<String> excludeConnectionIds = event.getExcludeConnectionIds();
        // 会话服务逻辑处理
        List<MessageOfUser> userMessages = conversationService.handleNewMessage(event);
        // 用户收件箱处理
        userMessageService.store(userMessages);
        List<PushMessage> pushMessages = Lists.newArrayListWithCapacity(userMessages.size());
        List<UserEventData> eventDataList = Lists.newArrayListWithCapacity(userMessages.size());
        long conversationId = event.getConversationId();
        List<UserConversationInfo> conversationsById = conversationService.getConversationsById(conversationId);
        Map<Long, UserConversationInfo> conversationsByIdMap =
            conversationsById.stream().collect(Collectors.toMap(UserConversationInfo::getUserId, Function.identity()));
        long senderId = event.getSenderId();
        UserInfo senderInfo = userService.getUserById(senderId);
        // 事件处理
        userMessages.forEach(m -> {
            UserEventData data = new UserEventData();
            long userId = m.getUserId();
            data.setUserId(userId);
            data.setTopic(Topics.CHAT_CONVERSATION_MESSAGE);
            data.setSourceId(String.valueOf(event.getMsgId()));
            MessageToUser messageToUser = buildMessageToUser(m, event);
            data.setContent(messageToUser);
            eventDataList.add(data);
            // 如果接受人是自己，不需要发离线推送
            if (userId != senderId) {
                UserConversationInfo userConversationInfo = conversationsByIdMap.get(userId);
                PushMessage pushMessage = buildPushMessage(userId, senderInfo, userConversationInfo, event);
                pushMessages.add(pushMessage);
            }
        });
        // 发布事件
        userEventService.publishEvents(eventDataList, excludeConnectionIds);
        // 离线推送
        pushService.push(pushMessages);
    }

    MessageToUser buildMessageToUser(MessageOfUser messageOfUser, ConversationMessageEvent event) {
        MessageToUser dataContent = new MessageToUser();
        dataContent.setMsgId(event.getMsgId());
        dataContent.setMsgType(event.getMsgType());
        dataContent.setMsgContent(event.getMsgContent());
        dataContent.setReceiverCount(event.getReceiverCount());
        dataContent.setCreateTime(event.getCreateTime());
        dataContent.setConversationId(event.getConversationId());
        dataContent.setSenderId(event.getSenderId());
        dataContent.setTargetId(event.getTargetId());
        return dataContent;
    }

    PushMessage buildPushMessage(long userId, UserInfo senderInfo, UserConversationInfo userConversationInfo,
        ConversationMessageEvent event) {
        if (userConversationInfo != null) {
            PushMessage pushMessage = new PushMessage();
            String body = null;
            switch (event.getMsgType()) {
                case MsgType.TEXT:
                    try {
                        Map<String, Object> msgMap = JsonUtils.jsonToBean(event.getMsgContent(), HashMap.class);
                        body = (String)msgMap.get("text");
                    } catch (Exception e) {
                        log.error("消息内容反序列化异常:", e);
                    }

                    break;
                case MsgType.FILE:
                    body = "文件";
                    break;
                case MsgType.PIC:
                    body = "图片";
                    break;
                default:
                    body = "未知消息类型";
                    break;
            }
            pushMessage.setBadgeNumber(userMessageService.getUserTotalUnread(userId));
            pushMessage.setTitle("新消息通知");
            pushMessage.setSubTitle(userConversationInfo.getConversationName());
            pushMessage.setBody(senderInfo.getNickName() + ":" + body);
            pushMessage.setUserId(userId);
            pushMessage.addProperty("conversationId", event.getConversationId());
            pushMessage.addProperty("eventTopic", Topics.CHAT_CONVERSATION_MESSAGE);
            return pushMessage;
        } else {
            throw new WarnMessageException(FeigeWarn.CONVERSATION_NOT_EXISTS);
        }
    }

    @RabbitListener(queues = AMQPConstants.QueueNames.CONVERSATION_READ_MESSAGE_RECEIPT)
    public void readMessageFeedback(@Payload ReadMessageEvent event) {
        Map<Long, List<Long>> senderAndMsgIds = event.getSenderAndMsgIds();
        List<UserEventData> dataList = Lists.newArrayList();
        senderAndMsgIds.entrySet().forEach(entry -> {
            try {
                long senderId = entry.getKey();
                List<Long> msgIds = entry.getValue();
                long readerId = event.getReaderId();
                ReadReceiptNotice notice = new ReadReceiptNotice();
                notice.setMsgIds(msgIds);
                notice.setReaderId(readerId);
                notice.setReadTime(event.getReadTime());
                notice.setConversationId(event.getConversationId());
                UserEventData eventData = new UserEventData();
                eventData.setTopic(Topics.CHAT_CONVERSATION_READ_MESSAGE);
                eventData.setUserId(senderId);
                eventData.setContent(notice);
                dataList.add(eventData);
            } catch (Exception e) {
                log.error("Send read receipt notice to user error:value={}", entry, e);
            }
        });
        userEventService.publishEvents(dataList, event.getExcludeConnectionIds());
    }

}
