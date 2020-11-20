package com.ds.feige.im.gateway.domain;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.redisson.api.*;

import com.ds.feige.im.constants.CacheKeys;
import com.ds.feige.im.gateway.socket.connection.ConnectionMeta;

import lombok.extern.slf4j.Slf4j;

/**
 * 在线用户对象封装,充血模型
 * @author DC
 */
@Slf4j
public class SessionUser {
    private Long userId;
    private RedissonClient redissonClient;
    public SessionUser(long userId, RedissonClient redissonClient) {
        this.userId = userId;
        this.redissonClient = redissonClient;
    }

    public void connectionEstablished(ConnectionMeta connectionMeta) {
        TransactionOptions transactionOptions = TransactionOptions.defaults();
        RTransaction transaction = redissonClient.createTransaction(transactionOptions);
        String deviceConnectionKey = getDeviceConnectionKey(connectionMeta.getDeviceId());
        //获取用户链接列表<sessionId>
        RSet<String> connectionKeys = transaction.getSet(CacheKeys.SESSION_USER_CONNECTIONS + this.userId);
        connectionKeys.add(deviceConnectionKey);
        //单个Connection的缓存
        RBucket<ConnectionMeta> metaRBucket = transaction.getBucket(deviceConnectionKey);
        metaRBucket.set(connectionMeta, 60, TimeUnit.MINUTES);
        transaction.commit();
        //更新state
        RBucket<UserState> stateRBucket = redissonClient.getBucket(CacheKeys.SESSION_USER_STATE + userId);
        UserState state = stateRBucket.get();
        if (state == null) {
            state = new UserState();
        }
        state.setOnline(true);
        stateRBucket.set(state);
        log.info("Add connection meta:userId={},connectionKey={},sessionId={}", userId, deviceConnectionKey,
            connectionMeta.getSessionId());
    }

    public Map<String, ConnectionMeta> getConnectionMetas() {
        RSet<String> deviceIds = redissonClient.getSet(CacheKeys.SESSION_USER_CONNECTIONS + this.userId);
        String[] deviceConnectionKeys = new String[deviceIds.size()];
        int i = 0;
        for (String key : deviceIds) {
            deviceConnectionKeys[i] = key;
            i++;
        }
        Map<String, ConnectionMeta> metaMap = redissonClient.getBuckets().get(deviceConnectionKeys);
        return metaMap;
    }

    public boolean keepAlive(String deviceId) {
        String deviceConnectionKey = getDeviceConnectionKey(deviceId);
        RBucket<ConnectionMeta> bucket = redissonClient.getBucket(deviceConnectionKey);
        ConnectionMeta meta = bucket.get();
        if (meta == null) {
            return false;
        }
        meta.setLastActiveTime(new Date());
        bucket.set(meta, 6, TimeUnit.MINUTES);
        return true;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ConnectionMeta getConnectionMetaByType(int deviceType) {
        for (Map.Entry<String, ConnectionMeta> entry : getConnectionMetas().entrySet()) {
            if (entry.getValue().getDeviceType().type == deviceType) {
                return entry.getValue();
            }
        }
        return null;
    }

    public ConnectionMeta getConnectionMetaByDeviceId(String deviceId) {
        String key = getDeviceConnectionKey(deviceId);
        RBucket<ConnectionMeta> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    public boolean disconnectConnection(String deviceId) {
        //删除ConnectionMeta缓存
        String key = getDeviceConnectionKey(deviceId);
        RTransaction transaction = redissonClient.createTransaction(TransactionOptions.defaults());
        RBucket<ConnectionMeta> bucket = transaction.getBucket(key);
        boolean metaDeleted = bucket.delete();
        //删除集合中的key
        RSet<String> sessionIdSet = transaction.getSet(CacheKeys.SESSION_USER_CONNECTIONS + this.userId);
        boolean setDeleted = sessionIdSet.remove(key);
        transaction.commit();
        //更新state
        UserState userState = new UserState();
        userState.setOnline(isOnline());
        userState.setOfflineTime(System.currentTimeMillis());
        RBucket<UserState> stateRBucket = redissonClient.getBucket(CacheKeys.SESSION_USER_STATE + userId);
        stateRBucket.set(userState);
        log.info("Remove connection meta:userId={},connectionKey={}", userId, key);
        return metaDeleted && setDeleted;
    }

    public RLock getLock() {
        //获取分布式锁
        RLock sessionLock = this.redissonClient.getLock(CacheKeys.SESSION_USER_LOCK_PREFIX + userId);
        return sessionLock;
    }

    public String getDeviceConnectionKey(String deviceId) {
        return CacheKeys.SESSION_USER_CONNECTION + this.userId + "." + deviceId;
    }

    public boolean isOnline() {
        Map<String, ConnectionMeta> map = getConnectionMetas();
        return map != null && !map.isEmpty();
    }

    public UserState getState() {
        RBucket<UserState> bucket = redissonClient.getBucket(CacheKeys.SESSION_USER_STATE + userId);
        UserState state = bucket.get();
        return state == null ? new UserState() : state;
    }
}

