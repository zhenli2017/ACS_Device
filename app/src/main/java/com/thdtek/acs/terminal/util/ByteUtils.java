package com.thdtek.acs.terminal.util;/**
 * Created by ygb on 2016/11/2.
 */

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : guobin.yang
 * @Description : ...
 * @date : 2016/11/2 12:08
 * @email : ygbokay@163.com
 */
public class ByteUtils {
    private static final String TAG = ByteUtils.class.getCanonicalName();

    /**
     * 合并两个数组
     * @param data1
     * @param data2
     * @return
     */
    public static byte[] combineByteArr(byte[] data1,  byte[] data2){
        byte[] data3 = new byte[data1.length+data2.length];
        System.arraycopy(data1,0,data3,0,data1.length);
        System.arraycopy(data2,0,data3,data1.length,data2.length);
        return data3;
    }


    /**
     * 合并多个字节数组
     * @param bytes
     * @return
     */
    public static byte[] combineByteArrs(byte[]... bytes){
        byte[] result = new byte[0];
        for (int i = 0; i < bytes.length; i++) {
            result = combineByteArr(result, bytes[i]);
        }
        return result;
    }

    /**
     * 判断一个字节数组是否包含另一个字节数组
     * @param A
     * @param B 被包含字节数组
     * @return
     */
    public static boolean isAContainB(byte[] A, byte[] B){
        if(A == null || B == null)
            return false;
        if(A.length < B.length)
            return false;
        String AStr = ByteFormatTransferUtils.bytesToHexString(A);
        String BStr = ByteFormatTransferUtils.bytesToHexString(B);
        if(AStr.contains(BStr)){
            return true;
        }
        return false;
    }


    /**
     * hex字符串格式化为带空格的hex字符串， 例如:1b62ffFF ----> 1b 62 ff FF
     * @param hexStr
     * @return
     */
    public static String hexStrAddBlankChar(String hexStr){

        if(TextUtils.isEmpty(hexStr)){
            return hexStr;
        }

        String temp = hexStr.trim();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < temp.length(); i++) {
            sb.append(temp.charAt(i));
            if(i % 2 == 1 && i != temp.length()-1){
                sb.append(" ");
            }
        }
        return sb.toString();
    }


    /**
     * 移除hex字符串中的空白字符
     * @param hexStr
     * @return
     */
    public static String hexStrRemoveBlankChar(String hexStr){
        if(TextUtils.isEmpty(hexStr)){
            return hexStr;
        }

        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(hexStr);
        String re = m.replaceAll("");
        return re;
    }
}
