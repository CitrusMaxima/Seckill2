package com.seckill.util;

import com.alibaba.fastjson.JSON;

public class CommonUtil {

    public static <T> String beanToString(T value) {

        if (value == null) {
            return null;
        }
        Class<?> tClass = value.getClass();
        if (tClass == int.class || tClass == Integer.class) {
            return "" + value;
        } else if (tClass == String.class) {
            return (String) value;
        } else if (tClass == long.class || tClass == Long.class) {
            return "" + value;
        } else {
            return JSON.toJSONString(value);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T stringToBean(String str, Class<T> tClass) {
        if (str == null || str.length() <= 0 || tClass == null) {
            return null;
        }

        if (tClass == int.class || tClass == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (tClass == String.class) {
            return (T) str;
        } else if (tClass == long.class || tClass == Long.class) {
            return (T) Long.valueOf(str);
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), tClass);
        }
    }
}
