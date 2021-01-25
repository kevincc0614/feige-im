package com.ds.feige.im.account.cache;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.redisson.api.RBucket;
import org.redisson.api.RBuckets;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ds.feige.im.account.entity.User;
import com.ds.feige.im.constants.CacheKeys;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author DC
 */
@Component
@Slf4j
public class UserCacheProvider {
    @Autowired
    RedissonClient redissonClient;

    public User getUser(long userId) {
        RBucket<User> bucket = redissonClient.getBucket(getUserKey(userId));
        User result = bucket.get();
        log.debug("Get user from cache:userId={},result={}", userId, result);
        return result;
    }

    public void putUser(User user) {
        RBucket<User> bucket = redissonClient.getBucket(getUserKey(user.getId()));
        bucket.set(user);
        log.debug("Put user to cache:user={}", user);
    }

    public Collection<User> getUsers(Collection<Long> userIds) {
        String[] keys = getUserKeys(userIds);
        RBuckets buckets = redissonClient.getBuckets();
        Map<String, User> userMap = buckets.get(keys);
        return userMap.values();
    }

    public void deleteUser(long userId) {
        RBucket<User> bucket = redissonClient.getBucket(getUserKey(userId));
        bucket.delete();
        log.info("Delete user in cache:userId={}", userId);
    }

    private static String getUserKey(Long userId) {
        return CacheKeys.USER_ENTITY + userId;
    }

    private static String[] getUserKeys(Collection<Long> userIds) {
        String[] userKeys = new String[userIds.size()];
        int i = 0;
        for (Long userId : userIds) {
            userKeys[i] = CacheKeys.USER_ENTITY + userId;
            i++;
        }
        return userKeys;
    }

    public String createUserToken(long userId, String password) {
        String token = IdUtil.simpleUUID();
        RMap map = redissonClient.getMap(CacheKeys.USER_TOKEN + token);
        LocalDateTime expireAt = LocalDateTime.now().plusDays(90);
        Date expireDate = Date.from(expireAt.atZone(ZoneId.systemDefault()).toInstant());
        map.put("userId", userId);
        map.put("expireAt", expireDate.getTime());
        map.put("password", password);
        map.expireAt(expireDate);
        return token;
    }

    public Map<String, Object> getUserIdByToken(String token) {
        RMap<String, Object> map = redissonClient.getMap(CacheKeys.USER_TOKEN + token);
        return map;
    }

    public Long deleteToken(String token) {
        RMap map = redissonClient.getMap(CacheKeys.USER_TOKEN + token);
        Long result = null;
        if (!map.isEmpty()) {
            result = (Long)map.get("userId");
            map.clear();
        }
        return result;
    }
}
