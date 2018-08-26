package com.lock;

/**
 * Created by ArawN on 2018/8/26.
 */
public interface DistributedLock {

    /**
     * 获取锁
     * @param key
     * @param value
     * @return
     */
    boolean tryLock(String key, String value);

    /**
     * 释放锁
     * @param key
     * @param value
     */
    void unlock(String key, String value);
}
