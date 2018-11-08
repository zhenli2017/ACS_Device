package com.thdtek.acs.terminal.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {

    private static byte[] encrypt(byte[] content, byte[] password, byte[] random)  {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(password, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(random);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] data = cipher.doFinal(content);
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    private static byte[] decrypt(byte[] content, byte[] password, byte[] random) {
        try {
            SecretKeySpec key = new SecretKeySpec(password, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.getBlockSize();
            IvParameterSpec iv = new IvParameterSpec(random);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] getRandom(){
        SecureRandom random = new SecureRandom();
        return random.generateSeed(16);
    }

    //本项目 random+body  (random是16字节， body使用random加密)
    public static byte[] enc(byte[] content, byte[] password){
        if(Const.IS_OPEN_DYNAMIC_AESKEY){
            byte[] random = getRandom();
            byte[] enc_re = encrypt(content, password, random);

            byte[] data3 = new byte[random.length+enc_re.length];
            System.arraycopy(random,0, data3,0, random.length);
            System.arraycopy(enc_re,0, data3, random.length, enc_re.length);
            return data3;
        }else{
            return content;
        }

    }


    //本项目 前16字节为随机向量  使用随机向量解密后面的内容
    public static byte[] dec(byte[] content, byte[] password){
        if(Const.IS_OPEN_DYNAMIC_AESKEY){
            byte[] random = new byte[16];
            byte[] body = new byte[content.length - 16];
            System.arraycopy(content, 0, random, 0, 16);
            System.arraycopy(content, 16, body, 0, content.length - 16);
            byte[] dec_re = decrypt(body, password, random);
            return dec_re;
        }else{
            return content;
        }

    }
}
