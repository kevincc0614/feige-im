package com.ds.feige.im.push.service;

import com.ds.feige.im.push.dto.PushMessage;

/**
 * 离线消息推送服务
 *
 * @author DC
 */
public interface PushService {

    void push(PushMessage message);
}
