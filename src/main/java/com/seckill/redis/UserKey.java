package com.seckill.redis;

/**
 * Created by CitrusMaxima on 2018/6/14.
 */
public class UserKey extends BasePrefix{

    private UserKey(String prefix) {
        super(prefix);
    }

    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");
}
