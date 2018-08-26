package com.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

@Component
public class RedisLock implements DistributedLock {

    private Logger logger = LoggerFactory.getLogger(RedisLock.class);

    private Jedis jedis = new Jedis();

    /**
     * 加锁
     * @param key key
     * @param value 当前时间+超时时间
     * @return
     */
    public boolean tryLock(String key, String value) {
        if (jedis.setnx(key, value) > 0) {
            return true;
        }
        String currentValue = jedis.get(key);
        // 如果锁过期
        if (!StringUtils.isEmpty(currentValue)
                && Long.parseLong(currentValue) < System.currentTimeMillis()) {
            // 获取上一个锁的时间
            String oldValue = jedis.getSet(key, value);
            if (!StringUtils.isEmpty(oldValue) && oldValue.equals(currentValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解锁
     * @param key key
     * @param value 当前时间+超时时间
     */
    public void unlock(String key, String value) {
        try {
            String currentValue = jedis.get(key);
            if (!StringUtils.isEmpty(currentValue) && currentValue.equals(value)) {
                jedis.del(key);
            }
        } catch (Exception e) {
            logger.error("【redis分布式锁】解锁异常，{}", e);
        }
    }

}