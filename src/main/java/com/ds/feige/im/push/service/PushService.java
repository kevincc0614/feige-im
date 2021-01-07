package com.ds.feige.im.push.service;

import java.util.List;

import com.ds.feige.im.push.dto.PushMessage;
import com.google.firebase.messaging.Message;

/**
 * 离线消息推送服务
 *
 * @author DC
 */
public interface PushService {

    void pushToUser(PushMessage message);

    void sendMessages(List<Message> messages);

    void push(List<PushMessage> messages);
}
