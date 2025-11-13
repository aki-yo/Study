package com.oh.tp.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class md5_java {

    /**
     * 生成MD5加密字符串（与前端hex_md5()函数效果相同）
     * @param input 输入字符串
     * @return 32位小写MD5加密结果
     */
    public static String md5(String input) {
        if (input == null) {
            input = "";
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes("UTF-8"));
            BigInteger no = new BigInteger(1, messageDigest);

            // 转换为16进制字符串并补齐前导零到32位
            StringBuilder hashtext = new StringBuilder(no.toString(16));
            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }

            return hashtext.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
