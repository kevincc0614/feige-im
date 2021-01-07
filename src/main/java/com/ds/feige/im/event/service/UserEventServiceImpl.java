package com.ds.feige.im.event.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ds.feige.im.chat.mapper.UserEventMapper;
import com.ds.feige.im.common.util.BeansConverter;
import com.ds.feige.im.common.util.JsonUtils;
import com.ds.feige.im.constants.CacheKeys;
import com.ds.feige.im.constants.SocketPaths;
import com.ds.feige.im.event.constants.Topics;
import com.ds.feige.im.event.dto.UserEventData;
import com.ds.feige.im.event.dto.UserEventInfo;
import com.ds.feige.im.event.dto.UserEventQuery;
import com.ds.feige.im.event.entity.UserEvent;
import com.ds.feige.im.gateway.mapper.UserDeviceMapper;
import com.ds.feige.im.gateway.service.SessionUserService;
import com.ds.feige.im.push.service.PushService;
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

    @Autowired
    PushService pushService;

    void handlePush(UserEventData eventData) {
        String topic = eventData.getTopic();
        switch (topic) {
            case Topics.CHAT_CONVERSATION_MESSAGE:

                break;
        }
    }
    @Override
    public void publishEvent(UserEventData data, Set<String> excludeConnections) {
        long userId = data.getUserId();
        // 不存在从数据库取最大
        long userSeqId = nextUserSeqId(userId);
        UserEvent entity = null;
        try {
            entity = convertToEntity(data, userSeqId);
        } catch (Exception exception) {
            log.error("Event data to entity error:data={}", data, exception);
            return;
        }
        save(entity);
        Map<String, Object> payload = buildEventPayload(entity);
        sessionUserService.sendToUser(userId, SocketPaths.SC_PUSH_EVENT, payload, excludeConnections);
    }

    @Override
    public void publishEvents(Collection<UserEventData> events, Set<String> excludeConnectionIds) {
        // Batch 操作其实并无法保证原子性
        Map<String, UserEventData> eventMap =
            events.stream().collect(Collectors.toMap(UserEventData::getKey, Function.identity()));
        List<UserEvent> entries = Lists.newArrayList();
        // TODO 如果redis更新成功,但是数据库保存失败,取消息时从数据库取,可能造成消息丢失。
        // TODO 可参考方案,用户每次拉取消息,服务器检测消息是否连续,如果不连续则进行自动补偿机制
        events.forEach(data -> {
            long userId = data.getUserId();
            try {
                long userSeqId = nextUserSeqId(userId);
                UserEvent entity = convertToEntity(data, userSeqId);
                entries.add(entity);
            } catch (Throwable e) {
                log.error("Build user event entity error:data={}", data, e);
                // TODO 用户空间自增序列号失败,该如何处理?
                // 先删除,不入库,那么可能这条消息就丢了,无法到达用户收件箱,客户端如何补偿？
            }
        });
        super.saveBatch(entries);
        entries.forEach(entry -> {
            Map<String, Object> payload = buildEventPayload(entry);
            sessionUserService.sendToUser(entry.getUserId(), SocketPaths.SC_PUSH_EVENT, payload, excludeConnectionIds);
        });
        log.info("Publish events complete:data={},excludeConnections={}", events, excludeConnectionIds);
    }

    private Map<String, Object> buildEventPayload(UserEvent event) {
        Map<String, Object> payload = Maps.newHashMap();
        payload.put("seqId", event.getSeqId());
        payload.put("topic", event.getTopic());
        return payload;
    }

    private UserEvent convertToEntity(UserEventData data, long userSeqId) throws Exception {
        UserEvent entity = new UserEvent();
        entity.setSourceId(data.getSourceId());
        entity.setSeqId(userSeqId);
        entity.setUserId(data.getUserId());
        entity.setContent(JsonUtils.toJson(data.getContent()));
        entity.setTopic(data.getTopic());
        return entity;
    }

    @Override
    public List<UserEventInfo> getUserEvents(UserEventQuery query) {
        if (query.getStartSeqId() == 0 || query.getStartSeqId() == null) {
            Long maxDeviceSeqId = userDeviceMapper.getMaxDeviceCheckpoint(query.getUserId());
            if (maxDeviceSeqId == null) {
                query.setStartSeqId(0L);
            } else {
                query.setStartSeqId(maxDeviceSeqId);
            }
        }
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
            long newestSeqId = userSeqId == null ? 0 : userSeqId;
            userSeq.compareAndSet(0, newestSeqId);
        }
        return userSeq.get();
    }

    public long nextUserSeqId(long userId) {
        // 先从缓存中取
        RAtomicLong userSeq = redissonClient.getAtomicLong(CacheKeys.USER_EVENT_SEQ + userId);
        if (!userSeq.isExists()) {
            Long dbMaxId = baseMapper.getMaxUserSeqId(userId);
            long newestSeqId = dbMaxId == null ? 0 : dbMaxId;
            userSeq.compareAndSet(0, newestSeqId);
        }
        long result = userSeq.incrementAndGet();
        log.info("Get user next seq id:userId={},seqId={}", userId, result);
        return result;
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
