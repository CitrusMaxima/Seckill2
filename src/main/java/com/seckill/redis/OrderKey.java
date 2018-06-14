package com.seckill.redis;

/**
 * Created by CitrusMaxima on 2018/6/14.
 */
public class OrderKey extends BasePrefix{

    public OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
