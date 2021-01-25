package com.ds.feige.im.push.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ds.feige.im.constants.DeviceType;
import com.ds.feige.im.gateway.entity.UserDevice;
import com.ds.feige.im.gateway.service.UserDeviceService;
import com.ds.feige.im.push.configure.PushConfigProperties;
import com.ds.feige.im.push.dto.PushMessage;
import com.google.firebase.messaging.*;

import lombok.extern.slf4j.Slf4j;

/**
 * IOS推送
 *
 * @author DC
 */
@Service
@Slf4j
public class PushServiceImpl implements PushService {
    @Autowired
    FirebaseMessaging firebaseMessaging;
    @Autowired
    UserDeviceService userDeviceService;
    @Autowired
    PushConfigProperties pushConfigProperties;

    @Override
    public void pushToUser(PushMessage request) {

        List<UserDevice> devices = userDeviceService.getPushableDevices(request.getUserId());
        pushToDevices(devices, request);
    }

    private void pushToDevices(List<UserDevice> devices, PushMessage request) {
        if (devices != null && !devices.isEmpty()) {
            List<Message> messages = new ArrayList<>();
            devices.forEach(d -> {
                try {
                    Message message = buildFirebaseMessage(request, d);
                    messages.add(message);
                } catch (Exception e) {
                    log.error("Build firebase message error:device={},request={}", d, request, e);
                }
            });
            if (!messages.isEmpty()) {
                sendMessages(messages);
            } else {
                log.error("Firebase messages is empty");
            }

        } else {
            log.warn("User has no pushable device:userId={}", request.getUserId());
        }
    }

    @Override
    public void sendMessages(List<Message> messages) {
        try {
            BatchResponse response = firebaseMessaging.sendAll(messages);
            if (response.getFailureCount() > 0) {
                response.getResponses().forEach(sendResponse -> {
                    if (!sendResponse.isSuccessful()) {
                        log.error("Push message fail:messageId={}", sendResponse.getMessageId(),
                            sendResponse.getException());
                    }
                });
                log.error("Push message has failed:successCount={},failureCount={}", response.getSuccessCount(),
                    response.getFailureCount());
            } else {
                log.info("Push message all success:successCount={}", response.getSuccessCount());
            }

        } catch (FirebaseMessagingException e) {
            log.error("Push message error", e);
        }
    }

    @Override
    public void push(List<PushMessage> messages) {
        log.info("Ready to push messages:{}", messages);
        messages.forEach(message -> pushToUser(message));
    }

    Message buildFirebaseMessage(PushMessage pushRequest, UserDevice device) {
        Notification notification =
            Notification.builder().setTitle(pushRequest.getTitle()).setBody(pushRequest.getBody()).build();
        DeviceType deviceType = DeviceType.valueOf(device.getDeviceType());
        Message.Builder message = Message.builder();
        if (deviceType == DeviceType.IOS) {
            Aps aps = Aps.builder().setBadge(pushRequest.getBadgeNumber()).putAllCustomData(pushRequest.getProperties())
                .build();
            ApnsConfig apnsConfig = ApnsConfig.builder().setAps(aps).build();
            message.setApnsConfig(apnsConfig);
        } else if (deviceType == DeviceType.ANDROID) {
            AndroidConfig androidConfig = AndroidConfig.builder().putAllData(pushRequest.getAllStringProperties())
                .setPriority(AndroidConfig.Priority.HIGH).build();
            message.setAndroidConfig(androidConfig);
        }
        message.setNotification(notification);
        message.setToken(device.getDeviceToken());
        log.info("Build firebase message:userId={},deviceId={},request={}", device.getUserId(), device.getDeviceId(),
            pushRequest);
        return message.build();
    }
}
