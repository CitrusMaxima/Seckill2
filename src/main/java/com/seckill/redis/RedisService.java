package com.seckill.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    public <T> T get(String key, Class<T> tClass) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = jedis.get(key);
            T t = stringToBean(str, tClass);
            return t;
        } finally {
            returnToPool(jedis);
        }
    }

    public <T> boolean set(String key, T value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            if (str == null || str.length() <= 0) {
                return false;
            }
            jedis.set(key, str);
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    private <T> String beanToString(T value) {

        if (value == null) {
            return null;
        }
        Class<?> tClass = value.getClass();
        if (tClass == int.class || tClass == Integer.class) {
            return "" + value;
        } else if (tClass == String.class) {
            return (String)value;
        } else if (tClass == long.class || tClass == Long.class) {
            return "" + value;
        } else {
            return JSON.toJSONString(value);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T stringToBean(String str, Class<T> tClass) {
        if (str == null || str.length() <= 0 || tClass == null) {
            return null;
        }

        if (tClass == int.class || tClass == Integer.class) {
            return (T)Integer.valueOf(str);
        } else if (tClass == String.class) {
            return (T)str;
        } else if (tClass == long.class || tClass == Long.class) {
            return (T)Long.valueOf(str);
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), tClass);
        }
    }

    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

}
