package com.lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by ArawN on 2018/8/26.
 */
@Component
public class ZookeeperLock implements DistributedLock {

    private Logger logger = LoggerFactory.getLogger(ZookeeperLock.class);

    private RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    /**
     * zk客户端
     */
    private CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", retryPolicy);

    private ThreadLocal<InterProcessMutex> mutexThreadLocal = new ThreadLocal<>();

    @Override
    public boolean tryLock(String key, String value) {
        client.start();
        // zk共享锁实现
        InterProcessMutex mutex = new InterProcessMutex(client, "/locks/" + key);
        mutexThreadLocal.set(mutex);
        try {
            return mutex.acquire(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unlock(String key, String value) {
        try {
            InterProcessMutex mutex = mutexThreadLocal.get();
            mutex.release();
            client.close();
        } catch (Exception e) {
            logger.error("【zookeeper分布式锁】解锁异常，{}", e);
        }
    }

}
