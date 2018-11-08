package com.thdtek.acs.terminal.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Time:2018/7/16
 * User:lizhen
 * Description:
 */

public class DateUtil {
    public static final String TAG = DateUtil.class.getSimpleName();

    public static int getCurrentYear() {
        Calendar instance = Calendar.getInstance();
        return instance.get(Calendar.YEAR);
    }

    public static int getCurrentMonth() {
        Calendar instance = Calendar.getInstance();
        return instance.get(Calendar.MONTH);
    }

    public static int getCurrentDayOfMonth() {
        Calendar instance = Calendar.getInstance();
        return instance.get(Calendar.DAY_OF_MONTH);
    }

    public static int getCurrentDayOfYear() {
        Calendar instance = Calendar.getInstance();
        return instance.get(Calendar.DAY_OF_YEAR);
    }

    public static int getCurrentHour() {
        Calendar instance = Calendar.getInstance();
        return instance.get(Calendar.HOUR_OF_DAY);
    }

    public static int getCurrentMintue() {
        Calendar instance = Calendar.getInstance();
        return instance.get(Calendar.MINUTE);
    }

    public static int getCurrentSecond() {
        Calendar instance = Calendar.getInstance();
        return instance.get(Calendar.SECOND);
    }

    public static int parseTimeHour(long time) {
        String[] startFormat = String.format(Locale.getDefault(), "%tF-%<tT", time).split(":|-");
        try {
            return Integer.parseInt(startFormat[3]);
        } catch (Exception e) {
            LogUtils.e(TAG, "parseTimeHour = " + e.getMessage());
            return 2;
        }
    }

    public static int parseTime(int startHour, int endHour, int nowHour) {

        System.out.println("start = " + startHour + " end = " + endHour + " now = " + nowHour);

        //0:可以通过,1,继续比对时间,2:不可通过
        if (nowHour == startHour || nowHour == endHour) {
            //需要继续比对
            return 1;
        } else if (nowHour > startHour && nowHour < endHour) {
            return 0;
        } else {
            return 2;
        }

    }
}
