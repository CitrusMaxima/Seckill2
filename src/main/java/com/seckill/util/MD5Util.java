package com.seckill.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    private static final String salt = "27dkl4564sjsOIneo#*%UNF军嫂法儿sajOioaffj0&*(sfe";

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    public static String inputPassToFormPass(String inputPass) {
        String str = inputPass + salt;
        return md5(str);
    }

    public static String formPassToDBPass(String formPass, String salt) {
        String str = formPass + salt;
        return md5(str);
    }

    public static String inputPassToDbPass(String input, String saltDB) {
        String formPass = inputPassToFormPass(input);
        String dbPass = formPassToDBPass(formPass, saltDB);
        return dbPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPassToFormPass("yzx126055"));
        //System.out.println(formPassToDBPass(inputPassToFormPass("yzx126055"), "27dkl4564sjsOIneo#*%UNF军嫂法儿sajOioaffj0&*(sfe"));
        //System.out.println(inputPassToDbPass("yzx126055", "shjfh98w3s"));
    }
}
