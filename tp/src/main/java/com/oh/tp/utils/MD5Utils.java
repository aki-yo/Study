package com.oh.tp.utils;


import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.util.DigestUtils;

public class MD5Utils {
    public static String md5(String input) {
        byte[] digest = DigestUtils.md5Digest(Utf8.encode(input));
        return new String(Hex.encode(digest));
    }
}