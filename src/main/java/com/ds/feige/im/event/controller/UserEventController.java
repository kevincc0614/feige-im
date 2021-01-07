package com.ds.feige.im.event.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.ds.feige.im.constants.SessionAttributeKeys;
import com.ds.feige.im.constants.SocketPaths;
import com.ds.feige.im.event.dto.UserEventInfo;
import com.ds.feige.im.event.dto.UserEventQuery;
import com.ds.feige.im.event.service.UserEventService;
import com.ds.feige.im.gateway.socket.annotation.SocketController;
import com.ds.feige.im.gateway.socket.annotation.SocketRequestMapping;
import com.ds.feige.im.gateway.socket.annotation.UserId;
import com.google.common.collect.Maps;

/**
 * @author DC
 */
@SocketController
public class UserEventController {
    @Autowired
    UserEventService userEventService;

    @SocketRequestMapping(SocketPaths.CS_PULL_EVENTS)
    public Map<String, Object> pullEvents(@RequestBody @Valid UserEventQuery query) {
        List<UserEventInfo> result = userEventService.getUserEvents(query);
        long maxSeqId = userEventService.getUserMaxSeqId(query.getUserId());
        Map<String, Object> payload = Maps.newHashMap();
        payload.put("events", result);
        payload.put("maxSeqId", maxSeqId);
        return payload;
    }

    @SocketRequestMapping(SocketPaths.CS_UPDATE_EVENT_CHECKPOINT)
    public boolean updateCheckpoint(@RequestParam("checkpoint") Long checkpoint,
        @RequestAttribute(SessionAttributeKeys.DEVICE_ID) String deviceId, @UserId Long userId) {
        return userEventService.updateCheckpoint(userId, deviceId, checkpoint);
    }
}
