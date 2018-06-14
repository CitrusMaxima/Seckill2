package com.seckill.redis;

/**
 * Created by CitrusMaxima on 2018/6/14.
 */
public interface KeyPrefix {

    int expireSeconds();

    String getPrefix();
}
