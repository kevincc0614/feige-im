package com.ds.feige.im.test;

import com.ds.feige.im.constants.CacheKeys;
import org.junit.Test;
import org.redisson.api.BatchResult;
import org.redisson.api.RBatch;
import org.redisson.api.RMapAsync;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author DC
 */
public class RedissonTest extends BaseTest {
    @Autowired
    RedissonClient redissonClient;

    @Test
    public void testBatch() throws Exception {
        RBatch batch = redissonClient.createBatch();
        RMapAsync mapAsync = batch.getMap(CacheKeys.SESSION_USER_STATE + 377665490283353088L);
        batch.getList("feige-im.user.connections.377593020425071616");
        BatchResult result = batch.execute();
//        RFuture future = mapAsync.getAsync("disconnectTime");
//        System.out.println(future.get());
        List list = result.getResponses();
        System.out.println(list);
    }
}
