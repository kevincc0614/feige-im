package com.ds.feige.im.event.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ds.feige.im.chat.entity.UserEventContent;
import com.ds.feige.im.chat.mapper.UserEventContentMapper;
import com.ds.feige.im.chat.mapper.UserEventMapper;
import com.ds.feige.im.common.util.JsonUtils;
import com.ds.feige.im.constants.CacheKeys;
import com.ds.feige.im.constants.SocketPaths;
import com.ds.feige.im.event.dto.UserEventData;
import com.ds.feige.im.event.dto.UserEventInfo;
import com.ds.feige.im.event.dto.UserEventQuery;
import com.ds.feige.im.event.entity.UserEvent;
import com.ds.feige.im.gateway.mapper.UserDeviceMapper;
import com.ds.feige.im.gateway.service.SessionUserService;
import com.ds.feige.im.push.service.PushService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author DC
 */
@Service
@Slf4j
@Transactional
public class UserEventServiceImpl extends ServiceImpl<UserEventMapper, UserEvent> implements UserEventService {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    SessionUserService sessionUserService;

    @Autowired
    UserDeviceMapper userDeviceMapper;

    @Autowired
    PushService pushService;

    @Autowired
    UserEventContentMapper contentMapper;

    @Override
    @SneakyThrows
    public void publishEvent(long userId, UserEventData data, Set<String> excludeConnections) {
        // 不存在从数据库取最大
        long userSeqId = nextUserSeqId(userId);
        long contentId = saveEventContent(data);
        UserEvent entity = null;
        try {
            entity = convertToEntity(userId, data.getTopic(), contentId, userSeqId);
        } catch (Exception exception) {
            log.error("Event data to entity error:data={}", data, exception);
            return;
        }
        save(entity);
        Map<String, Object> payload = buildEventPayload(entity);
        sessionUserService.sendToUser(userId, SocketPaths.SC_PUSH_EVENT, payload, excludeConnections);
    }

    @SneakyThrows
    private long saveEventContent(UserEventData data) {
        UserEventContent contentEntity = new UserEventContent();
        String contentValue = JsonUtils.toJson(data.getContent());
        contentEntity.setContent(contentValue);
        contentMapper.insert(contentEntity);
        RBucket<String> contentBucket = redissonClient.getBucket(CacheKeys.USER_EVENT_CONTENT + contentEntity.getId());
        contentBucket.set(contentValue, 30, TimeUnit.DAYS);
        log.info("Save event content success:{}", contentEntity);
        return contentEntity.getId();
    }

    @Override
    @SneakyThrows
    public void publishEvents(Collection<Long> userIds, UserEventData data, Set<String> excludeConnectionIds) {
        // Batch 操作其实并无法保证原子性
        List<UserEvent> entries = Lists.newArrayList();
        // TODO 如果redis更新成功,但是数据库保存失败,取消息时从数据库取,可能造成消息丢失。
        // TODO 可参考方案,用户每次拉取消息,服务器检测消息是否连续,如果不连续则进行自动补偿机制
        long contentId = saveEventContent(data);
        userIds.forEach(userId -> {
            try {
                long userSeqId = nextUserSeqId(userId);
                UserEvent entity = convertToEntity(userId, data.getTopic(), contentId, userSeqId);
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
        log.info("Publish events complete:eventContentId={},data={},excludeConnections={}", contentId, data,
            excludeConnectionIds);
    }

    private Map<String, Object> buildEventPayload(UserEvent event) {
        Map<String, Object> payload = Maps.newHashMap();
        payload.put("seqId", event.getSeqId());
        payload.put("topic", event.getTopic());
        return payload;
    }

    private UserEvent convertToEntity(long userId, String topic, long contentId, long userSeqId) throws Exception {
        UserEvent entity = new UserEvent();
        entity.setSeqId(userSeqId);
        entity.setUserId(userId);
        entity.setContentId(contentId);
        entity.setTopic(topic);
        return entity;
    }

    @Override
    public List<UserEventInfo> getUserEvents(UserEventQuery query) {
        log.info("Ready to get user events:{}", query);
        if (query.getStartSeqId() == 0 || query.getStartSeqId() == null) {
            Long maxDeviceSeqId = userDeviceMapper.getMaxDeviceCheckpoint(query.getUserId());
            if (maxDeviceSeqId == null) {
                query.setStartSeqId(0L);
            } else {
                query.setStartSeqId(maxDeviceSeqId);
            }
        }
        // 查询数据库
        List<UserEvent> events =
            baseMapper.findEventsByStartSeqId(query.getUserId(), query.getStartSeqId(), query.getSize());
        String[] keys = new String[events.size()];
        for (int i = 0; i < events.size(); i++) {
            keys[i] = CacheKeys.USER_EVENT_CONTENT + events.get(i).getContentId();
        }
        // 查询缓存
        Map<String, String> contents = redissonClient.getBuckets().get(keys);
        Set<String> allKeySet = Sets.newHashSet(keys);
        Set<String> notInCache = Sets.difference(allKeySet, contents.keySet());
        if (notInCache != null && !notInCache.isEmpty()) {
            Set<Long> dbIds = notInCache.stream()
                .map(key -> Long.valueOf(key.replace(CacheKeys.USER_EVENT_CONTENT, ""))).collect(Collectors.toSet());
            if (dbIds != null && !dbIds.isEmpty()) {
                List<UserEventContent> entities = contentMapper.findContentsByIds(dbIds);
                entities.forEach(entity -> {
                    contents.put(CacheKeys.USER_EVENT_CONTENT + entity.getId(), entity.getContent());
                });
            }
        }

        List<UserEventInfo> result = Lists.newArrayListWithCapacity(events.size());
        events.forEach((event) -> {
            UserEventInfo userEventInfo = new UserEventInfo();
            userEventInfo.setUserId(event.getUserId());
            userEventInfo.setSeqId(event.getSeqId());
            userEventInfo.setTopic(event.getTopic());
            userEventInfo.setCreateTime(event.getCreateTime());
            userEventInfo.setContent(contents.get(CacheKeys.USER_EVENT_CONTENT + event.getContentId()));
            result.add(userEventInfo);
        });
        return result;
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
