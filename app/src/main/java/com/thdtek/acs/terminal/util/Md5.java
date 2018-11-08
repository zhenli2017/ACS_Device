package com.thdtek.acs.terminal.util;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * @author anylife.zlb@gmail.com
 * @author ygb
 */
public class Md5 {
    public static String GetCodeMd5(String code) {

        return md5(code).substring(0, 16);
    }

    public static String getMd5(String code) {
        return md5(code);
    }

    /**
     * @param string
     * @return
     */
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static String getEncodePwd(String pwd) {
        Log.e("TAG", " === pwd -> " + md5(pwd).substring(0, 16));
        return md5(pwd).substring(0, 16);
    }
    public static String getEncodeDeviceSn(String sn) {
        return md5(sn).substring(0, 16);
    }
}
