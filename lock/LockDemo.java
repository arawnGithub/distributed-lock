package com.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LockDemo {

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private ZookeeperLock zookeeperLock;

    private static final int TIMEOUT = 10 * 1000; // 超时时间10秒

    public void mockRedisLock(String productId) {
        // 加锁
        long time = System.currentTimeMillis() + TIMEOUT;
        if (!redisLock.tryLock(productId, String.valueOf(time))) {
            throw new RuntimeException("哎哟喂，人也太多了，换个姿势再试试~~");
        }

        // do something

        // 解锁
        redisLock.unlock(productId, String.valueOf(time));
    }

    public void mockZookeeperLock(String productId) {
        // 加锁
        long time = System.currentTimeMillis() + TIMEOUT;
        if (!zookeeperLock.tryLock(productId, String.valueOf(time))) {
            throw new RuntimeException("哎哟喂，人也太多了，换个姿势再试试~~");
        }

        // do something

        // 解锁
        zookeeperLock.unlock(productId, String.valueOf(time));
    }

}
