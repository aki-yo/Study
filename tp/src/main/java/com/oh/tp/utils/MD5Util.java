package com.oh.tp.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;

public class MD5Util {
    public static String md5(String input) {
        return DigestUtils.md5Hex(input.getBytes(StandardCharsets.UTF_8));
    }
}
