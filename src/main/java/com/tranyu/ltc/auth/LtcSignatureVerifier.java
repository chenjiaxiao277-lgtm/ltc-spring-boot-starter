package com.tranyu.ltc.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class LtcSignatureVerifier {
    /**
     * 验证 LTC 签名。
     * LTC 前端算法：key = random(5chars)，token = md5(key + vt) + key
     * key 嵌在 token 末尾 5 位，无需服务端静态 signKey。
     */
    public boolean verify(String vt, String tokenHeaderValue) {
        if (vt == null || vt.isBlank()) return false;
        if (tokenHeaderValue == null || tokenHeaderValue.length() < 5) return false;
        String key = tokenHeaderValue.substring(tokenHeaderValue.length() - 5);
        String expected = md5Hex(key + vt) + key;
        return expected.equalsIgnoreCase(tokenHeaderValue.trim());
    }

    private static String md5Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] b = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(b.length * 2);
            for (byte x : b) {
                sb.append(Character.forDigit((x >> 4) & 0xF, 16));
                sb.append(Character.forDigit(x & 0xF, 16));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("MD5 不可用", e);
        }
    }
}
