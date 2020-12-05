package com.ds.feige.im.event.service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ds.feige.im.chat.mapper.UserEventMapper;
import com.ds.feige.im.common.util.BeansConverter;
import com.ds.feige.im.common.util.JsonUtils;
import com.ds.feige.im.constants.CacheKeys;
import com.ds.feige.im.constants.SocketPaths;
import com.ds.feige.im.event.dto.UserEventData;
import com.ds.feige.im.event.dto.UserEventInfo;
import com.ds.feige.im.event.dto.UserEventQuery;
import com.ds.feige.im.event.entity.UserEvent;
import com.ds.feige.im.gateway.mapper.UserDeviceMapper;
import com.ds.feige.im.gateway.service.SessionUserService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DC
 */
@Service
@Slf4j
public class UserEventServiceImpl extends ServiceImpl<UserEventMapper, UserEvent> implements UserEventService {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    SessionUserService sessionUserService;

    @Autowired
    UserDeviceMapper userDeviceMapper;

    @Override
    public void publishEvent(UserEventData data, Set<String> excludeConnections) {
        long userId = data.getUserId();
        String sourceId = data.getSourceId();
        RAtomicLong userSeq = redissonClient.getAtomicLong(CacheKeys.USER_EVENT_SEQ + userId);
        Long userSeqId = userSeq.incrementAndGet();
        UserEvent entity = convertToEntity(data, userSeqId);
        save(entity);
        Map<String, Object> payload = buildEventPayload(entity);
        sessionUserService.sendToUser(userId, SocketPaths.SC_PUSH_EVENT, payload, excludeConnections);
    }

    @Override
    public void publishEvents(Collection<UserEventData> events, Set<String> excludeConnectionIds) {
        // Batch 操作其实并无法保证原子性
        BatchOptions batchOptions = BatchOptions.defaults().executionMode(BatchOptions.ExecutionMode.IN_MEMORY_ATOMIC)
            .retryAttempts(3).retryInterval(1, TimeUnit.SECONDS).responseTimeout(5, TimeUnit.SECONDS);
        RBatch batch = redissonClient.createBatch(batchOptions);
        Map<String, RFuture<Long>> futures = new HashMap<>();
        Map<String, UserEventData> eventMap =
            events.stream().collect(Collectors.toMap(UserEventData::getKey, Function.identity()));
        eventMap.forEach((eventKey, event) -> {
            long userId = event.getUserId();
            RAtomicLongAsync userSeqAtomic = batch.getAtomicLong(CacheKeys.USER_EVENT_SEQ + userId);
            futures.put(eventKey, userSeqAtomic.incrementAndGetAsync());
        });
        batch.execute();
        List<UserEvent> entries = Lists.newArrayList();
        // TODO 如果redis更新成功,但是数据库保存失败,取消息时从数据库取,可能造成消息丢失。
        // TODO 可参考方案,用户每次拉取消息,服务器检测消息是否连续,如果不连续则进行自动补偿机制
        futures.forEach((eventKey, incrementFuture) -> {
            UserEventData data = eventMap.get(eventKey);
            Long userSeqId = null;
            try {
                userSeqId = incrementFuture.get();
                UserEvent entity = convertToEntity(data, userSeqId);
                entries.add(entity);
            } catch (Throwable e) {
                log.error("Get user event seqId error:data={}", data, e);
                // TODO 用户空间自增序列号失败,该如何处理?
                // 先删除,不入库,那么可能这条消息就丢了,无法到达用户收件箱,客户端如何补偿？
                eventMap.remove(eventKey);
            }
        });
        super.saveBatch(entries);
        entries.forEach(entry -> {
            Map<String, Object> payload = buildEventPayload(entry);
            sessionUserService.sendToUser(entry.getUserId(), SocketPaths.SC_PUSH_EVENT, payload, excludeConnectionIds);
        });

    }

    private Map<String, Object> buildEventPayload(UserEvent event) {
        Map<String, Object> payload = Maps.newHashMap();
        payload.put("seqId", event.getSeqId());
        payload.put("topic", event.getTopic());
        return payload;
    }

    private UserEvent convertToEntity(UserEventData data, long userSeqId) {
        UserEvent entity = new UserEvent();
        entity.setSourceId(data.getSourceId());
        entity.setSeqId(userSeqId);
        entity.setUserId(data.getUserId());
        try {
            entity.setContent(JsonUtils.toJson(data.getContent()));
        } catch (IOException e) {
            log.error("User event json format error:{}", data.getContent());
            return null;
        }
        entity.setTopic(data.getTopic());
        return entity;
    }

    @Override
    public List<UserEventInfo> getUserEvents(UserEventQuery query) {
        List<UserEvent> events =
            baseMapper.findEventsByStartSeqId(query.getUserId(), query.getStartSeqId(), query.getSize());
        return BeansConverter.convertToUserEventInfos(events);
    }

    @Override
    public long getUserMaxSeqId(long userId) {
        // 先从缓存中取
        RAtomicLong userSeq = redissonClient.getAtomicLong(CacheKeys.USER_EVENT_SEQ + userId);
        Long userSeqId = userSeq.get();
        if (userSeqId == null || userSeqId <= 0) {
            userSeqId = baseMapper.getMaxUserSeqId(userId);
        }
        return userSeqId == null ? 0 : userSeqId;
    }

    @Override
    public long getUserDeviceCheckpoint(long userId, String deviceId) {
        Long checkpoint = userDeviceMapper.getDeviceCheckpoint(userId, deviceId);
        return checkpoint == null ? 0 : checkpoint;
    }

    @Override
    public boolean updateCheckpoint(long userId, String deviceId, long seqId) {
        int i = userDeviceMapper.updateCheckpoint(userId, deviceId, seqId);
        log.info("Update user device checkpoint:update={},userId={},deviceId={},seqId={}", i, userId, deviceId, seqId);
        return i > 0;
    }
}
