package com.seckill.redis;

public class SeckillKey extends BasePrefix {

    public SeckillKey(String prefix) {
        super(prefix);
    }

    public SeckillKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static SeckillKey isGoodsOver = new SeckillKey("go");
    public static SeckillKey getSeckillPath = new SeckillKey(60, "sp");
    public static SeckillKey getMiaoshaVerifyCode = new SeckillKey(300, "vc");
}
