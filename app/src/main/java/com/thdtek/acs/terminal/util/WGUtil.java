package com.thdtek.acs.terminal.util;

import android.text.TextUtils;
import android.util.Log;

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
//    public static int parseWG26(String number) {
//        if (number.length() < 8) {
//            LogUtils.e(TAG, "parseWG26 IC格式不符合");
//            return 0;
//        }
//        //截取前6位,最后两位抛弃
//        String substring = number.substring(0, 6);
//        String one = substring.substring(0, 2);
//        String two = substring.substring(2, 4);
//        String three = substring.substring(4, 6);
//        String all = three + two + one;
//        int parseInt = Integer.parseInt(all, 16);
//        LogUtils.d(TAG, "parseWG26 = " + all + " int = " + parseInt);
//        return parseInt;
//    }
    public static int parseWG26(String number) {
        if (number.length() < 8) {
            LogUtils.e(TAG, "parseWG26 IC格式不符合");
            return 0;
        }
        //D0702672
        String one = number.substring(4, 6);
        String two = number.substring(2, 4);
        String three = number.substring(0, 2);

        System.out.println("one = " + one);
        System.out.println("two+three = " + (two + three));
        String parseOne = Integer.parseInt(one, 16) + "";
        String parseTwo = String.format(Locale.getDefault(), "%05d", Integer.parseInt(two + three, 16));

        System.out.println("parseOne = " + parseOne + " parseTwo = " + parseTwo);
        int finalInt = Integer.parseInt(parseOne + parseTwo);
        LogUtils.d(TAG, "parseWG26 = " + one + two + three + " int = " + finalInt);
        return finalInt;
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
        String parseIcNumber = parseIcNumber(number);
        String one = parseIcNumber.substring(0, 4);
        String two = parseIcNumber.substring(4, 8);
        int parseIntOne = Integer.parseInt(one, 16);
        int parseIntTwo = Integer.parseInt(two, 16);
        int finalInt = Integer.parseInt(String.valueOf(parseIntOne) + String.valueOf(parseIntTwo));

        LogUtils.d(TAG, "parseWG34 = " + parseIcNumber + " int = " + finalInt);
        return finalInt;
    }

    public static String parseWG66(String number) {
        if (TextUtils.isEmpty(number)) {
            return "0000000000000000";
        }
        if (number.length() == 16) {
            return number;
        }
        if (number.length() > 16) {
            return number.substring(0, 16);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 16 - number.length(); i++) {
            stringBuilder.append("0");
        }
        return stringBuilder.toString() + number;
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

    public static String parseWG16To10(String number) {
        try {
            long parseLong = Long.parseLong(number, 16);
            return parseLong + "";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static String parseWG10To16(String number, int wg) {

        String result = "0000000000000000";
        try {
            long tempNumber = Long.parseLong(number);
            String hexString = Long.toHexString(tempNumber);
            if (wg == Const.WG_26 || wg == Const.WG_26_0) {
                if (hexString.length() < 8) {
                    result = add0(hexString, 8 - hexString.length());
                } else {
                    result = hexString.substring(hexString.length() - 8, hexString.length());
                }

            } else if (wg == Const.WG_34 || wg == Const.WG_34_0) {
                if (hexString.length() < 8) {
                    result = add0(hexString, 8 - hexString.length());
                } else {
                    result = hexString.substring(hexString.length() - 8, hexString.length());
                }
            } else if (wg == Const.WG_66 || wg == Const.WG_66_0) {
                if (hexString.length() < 16) {
                    result = add0(hexString, 16 - hexString.length());
                } else {
                    result = hexString.substring(hexString.length() - 16, hexString.length());
                }
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "parseWG10To16 = " + e.getMessage());
        }
        LogUtils.d(TAG, "parseWG10To16 = " + result);
        return result;
    }

    public static String add0(String msg, int number) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < number; i++) {
            stringBuilder.append("0");
        }
        return stringBuilder.toString() + msg;

    }

}
