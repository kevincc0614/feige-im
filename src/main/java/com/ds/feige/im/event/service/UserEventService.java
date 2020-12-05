package com.ds.feige.im.event.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.ds.feige.im.event.dto.UserEventData;
import com.ds.feige.im.event.dto.UserEventInfo;
import com.ds.feige.im.event.dto.UserEventQuery;

/**
 * @author DC
 */
public interface UserEventService {

    void publishEvent(UserEventData request, Set<String> excludeConnections);

    void publishEvents(Collection<UserEventData> events, Set<String> excludeConnections);

    List<UserEventInfo> getUserEvents(UserEventQuery query);

    long getUserMaxSeqId(long userId);

    long getUserDeviceCheckpoint(long userId, String deviceId);

    boolean updateCheckpoint(long userId, String deviceId, long seqId);

}
