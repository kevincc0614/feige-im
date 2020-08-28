package com.ds.feige.im.gateway.domain;

import com.ds.feige.im.constants.CacheKeys;
import com.ds.feige.im.gateway.socket.connection.ConnectionMeta;
import org.redisson.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 在线用户对象封装,充血模型
 * @author DC
 */
public class SessionUser {
    private Long userId;
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionUser.class);
    private RedissonClient redissonClient;

    public SessionUser(long userId, RedissonClient redissonClient) {
        this.userId = userId;
        this.redissonClient = redissonClient;
    }

    public SessionUser(Long userId, RedissonClient redissonClient, ConnectionMeta meta) {
        this(userId, redissonClient);
        this.addConnectionMeta(meta);
    }

    public void addConnectionMeta(ConnectionMeta connectionMeta) {
        TransactionOptions transactionOptions = TransactionOptions.defaults();
        RTransaction transaction = redissonClient.createTransaction(transactionOptions);
        //获取用户链接列表<sessionId>
        RSet<String> connectionKeys = transaction.getSet(CacheKeys.SESSION_USER_CONNECTIONS + this.userId);
        connectionKeys.add(connectionMeta.getDeviceId());
        //单个Connection的缓存
        RBucket<ConnectionMeta> metaRBucket = transaction.getBucket(CacheKeys.SESSION_USER_CONNECTION + connectionMeta.getDeviceId());
        metaRBucket.set(connectionMeta,60,TimeUnit.MINUTES);
        transaction.commit();
        LOGGER.info("Add connection meta:userId={},deviceId={},sessionId={}",userId,connectionMeta.getDeviceId(),connectionMeta.getSessionId());
    }

    public Map<String, ConnectionMeta> getConnectionMetas() {
        RSet<String> deviceIds = redissonClient.getSet(CacheKeys.SESSION_USER_CONNECTIONS + this.userId);
        String[] deviceIdArray = new String[deviceIds.size()];
        int i = 0;
        for (String deviceId : deviceIds) {
            deviceIdArray[i] = CacheKeys.SESSION_USER_CONNECTION + deviceId;
            i++;
        }
        Map<String, ConnectionMeta> metaMap = redissonClient.getBuckets().get(deviceIdArray);

        return metaMap;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ConnectionMeta getConnectionMeta(int deviceType) {
        for (Map.Entry<String, ConnectionMeta> entry : getConnectionMetas().entrySet()) {
            if (entry.getValue().getDeviceType() == deviceType) {
                return entry.getValue();
            }
        }
        return null;
    }

    public ConnectionMeta getMetaByDeviceId(String deviceId) {
        String key = CacheKeys.SESSION_USER_CONNECTION + deviceId;
        RBucket<ConnectionMeta> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    public boolean removeConnectionMeta(String deviceId) {
        //删除ConnectionMeta缓存
        String key = CacheKeys.SESSION_USER_CONNECTION + deviceId;
        RTransaction transaction = redissonClient.createTransaction(TransactionOptions.defaults());
        RBucket<ConnectionMeta> bucket = transaction.getBucket(key);
        boolean metaDeleted = bucket.delete();
        //删除集合中的key
        RSet<String> sessionIdSet = transaction.getSet(CacheKeys.SESSION_USER_CONNECTIONS + this.userId);
        boolean setDeleted = sessionIdSet.remove(deviceId);
        transaction.commit();
        LOGGER.info("Remove connection meta:userId={},deviceId={}",userId,deviceId);
        return metaDeleted && setDeleted;
    }

    public RLock getLock() {
        //获取分布式锁
        RLock sessionLock = this.redissonClient.getLock(CacheKeys.SESSION_USER_LOCK_PREFIX + userId);
        return sessionLock;
    }

    public boolean isOnline() {
        Map<String, ConnectionMeta> map = getConnectionMetas();
        return map != null && !map.isEmpty();
    }
}

