package com.thdtek.acs.terminal.haogonge;

import java.security.MessageDigest;

/**
 * MD5 算法
 */
public class MD5 {

    /**
     * md5 加密
     *
     * @param s
     * @return
     */
    public String encode(String s) {
        try {
            return toHex(MessageDigest.getInstance("MD5").digest(s.getBytes("UTF-8"))).toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("md5 加密", e);
        }
    }

    /**
     * 十六进制字符
     */
    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    /**
     * 转换为十六进制字符串
     *
     * @param bytes
     * @return
     */
    private static String toHex(byte[] bytes) {
        StringBuilder str = new StringBuilder(bytes.length * 2);
        final int fifteen = 0x0f;//十六进制中的 15
        for (byte b : bytes) {//byte 为 32 位
            str.append(HEX_CHARS[(b >> 4) & fifteen]);//获取第 25 位到第 28 位的二进制数
            str.append(HEX_CHARS[b & fifteen]);//获取第 29 位到第 32 位的二进制数
        }
        return str.toString();
    }
}