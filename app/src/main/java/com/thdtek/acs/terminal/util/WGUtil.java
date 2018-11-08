package com.thdtek.acs.terminal.util;

import java.util.Locale;

/**
 * Time:2018/9/19
 * User:lizhen
 * Description:
 */

public class WGUtil {
    private static final String TAG = WGUtil.class.getSimpleName();

    /**
     * 读取的16进制数输出到 串口 ,需要把数据转成int
     * 转换方法 :
     * 例子: 101F3372
     *
     * @param number
     * @return
     */
    public static int parseWG26(String number) {
        if (number.length() < 8) {
            LogUtils.e(TAG, "parseWG26 IC格式不符合");
            return 0;
        }
        //截取前6位,最后两位抛弃
        String substring = number.substring(0, 6);
        String one = substring.substring(0, 2);
        String two = substring.substring(2, 4);
        String three = substring.substring(4, 6);
        String all = three + two + one;
        int parseInt = Integer.parseInt(all, 16);
        LogUtils.d(TAG, "parseWG26 = " + all + " int = " + parseInt);
        return parseInt;
    }

    /**
     * 读取的16进制数输出到 串口 ,需要把数据转成int
     * 例子: 101F3372
     * 1.数据翻转 72 33 1F 10
     * 2.直接转成 10 进制数据,输出到数据
     *
     * @param number
     * @return
     */
    public static int parseWG34(String number) {
        if (number.length() < 8) {
            LogUtils.e(TAG, "parseWG34 IC格式不符合");
            return 0;
        }
        number = parseIcNumber(number).toLowerCase();
        //截取前6位,最后两位抛弃
        int i = Integer.parseInt(number, 16);
        LogUtils.d(TAG, "parseWG34 = " + number + " int = " + i);
        return i;
    }

    public static String parseIcNumber(String number) {

        if (number.length() < 8) {
            LogUtils.e(TAG, "parseIcNumber IC格式不符合");
            return "";
        }
        String one = number.substring(0, 2);
        String two = number.substring(2, 4);
        String three = number.substring(4, 6);
        String four = number.substring(6, 8);
        LogUtils.d(TAG, "four+three+two+one = " + four + three + two + one);
        return four + three + two + one;
    }
}
