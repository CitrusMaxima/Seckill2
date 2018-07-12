package com.seckill.redis;

public class OrderKey extends BasePrefix {

    public OrderKey(String prefix) {
        super(0, prefix);
    }

    public static OrderKey getSeckillOrderByUidGid = new OrderKey("soug");
}
