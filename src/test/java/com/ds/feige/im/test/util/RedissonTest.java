package com.ds.feige.im.test.util;

import java.io.File;

import org.redisson.Redisson;
import org.redisson.api.BatchOptions;
import org.redisson.api.RBatch;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * @author DC
 */
public class RedissonTest {
    public static void main(String[] args) throws Exception {
        Config config =
            Config.fromYAML(new File("/Users/caedmon/Workspace/feige-im/src/main/resources/redisson-dev.yml"));
        RedissonClient client = Redisson.create(config);
        long start = System.currentTimeMillis();
        BatchOptions options = BatchOptions.defaults().executionMode(BatchOptions.ExecutionMode.REDIS_WRITE_ATOMIC);
        RBatch batch = client.createBatch(options);
        for (int i = 0; i < 50000; i++) {
            batch.getAtomicLong("test_incr" + i).incrementAndGetAsync();
        }

        batch.executeAsync().get();
        long end = System.currentTimeMillis();
        System.out.println("总耗时:" + (end - start) + "ms");
        // System.out.println(client.getBucket("feige-im.chat.message.read-receipts.406487154668002304").get());
        // 先操作redis,再保存数据库,如果保存数据库异常,失败,但是redis操作成功,redis的seq_id回滚。
        // 保证原子性,seq_id,redis,数据库 三者一定要保证原子性
    }
}
