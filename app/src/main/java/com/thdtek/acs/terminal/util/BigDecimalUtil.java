package com.thdtek.acs.terminal.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Time:2017/7/7
 * User:lizhen
 * Description:
 */

public class BigDecimalUtil {

    public static double getDouble(double number, int size) {
        BigDecimal b = new BigDecimal(number);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String format(double number, String format) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.applyPattern(format);
        return decimalFormat.format(number);   //12.34
    }
}
