package com.seckill.redis;

import com.seckill.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    /**
     * 获取单个对象
     *
     * @param keyPrefix
     * @param key
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix keyPrefix, String key, Class<T> tClass) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            // 生成真正的key
            String realKey = keyPrefix.getPrefix() + key;
            String str = jedis.get(realKey);
            T t = CommonUtil.stringToBean(str, tClass);
            return t;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 设置对象
     *
     * @param keyPrefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> boolean set(KeyPrefix keyPrefix, String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = CommonUtil.beanToString(value);
            if (str == null || str.length() <= 0) {
                return false;
            }
            // 生成真正的key
            String realKey = keyPrefix.getPrefix() + key;
            int seconds = keyPrefix.expireSeconds();
            if (seconds <= 0) {
                jedis.set(realKey, str);
            } else {
                jedis.setex(realKey, seconds, str);
            }

            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 判断key是否存在
     *
     * @param keyPrefix
     * @param key
     * @return
     */
    public boolean exists(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            // 生成真正的key
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.exists(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 删除对象
     *
     * @param keyPrefix
     * @param key
     * @return
     */
    public boolean delete(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            // 生成真正的key
            String realKey = keyPrefix.getPrefix() + key;
            long ret = jedis.del(realKey);
            return ret > 0;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 增加值
     *
     * @param keyPrefix
     * @param key
     * @return
     */
    public Long incr(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            // 生成真正的key
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.incr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 减少值
     *
     * @param keyPrefix
     * @param key
     * @return
     */
    public Long decr(KeyPrefix keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            // 生成真正的key
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.decr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

}
