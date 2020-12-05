package com.ds.feige.im.enterprise.cache;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DC
 */
@Component
@Slf4j
public class DepartmentCacheProvider {
    @Autowired
    RedissonClient redissonClient;
}
