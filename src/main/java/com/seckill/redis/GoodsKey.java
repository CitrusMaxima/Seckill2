package com.seckill.redis;

/**
 * Created by CitrusMaxima on 2018/6/14.
 */
public class GoodsKey extends BasePrefix {

    private GoodsKey(int expireSeconds, String prefix) {
        super(prefix);
    }

    public static GoodsKey getGoodsList = new GoodsKey(60, "gl");
}