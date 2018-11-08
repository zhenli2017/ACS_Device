package com.thdtek.acs.terminal.util;

import android.support.annotation.IntDef;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.thdtek.acs.terminal.bean.WeeklyBean;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.Locale;

/**
 * Time:2018/7/13
 * User:lizhen
 * Description:
 */

public class AuthorityUtil {
    private static final String TAG = AuthorityUtil.class.getSimpleName();

    public static final int TIME_TYPE_YEAR = 0;
    public static final int TIME_TYPE_MONTH = 1;
    public static final int TIME_TYPE_DAY = 2;
    public static final int TIME_TYPE_HOUR = 3;
    public static final int TIME_TYPE_MINUTE = 4;
    public static final int TIME_TYPE_SECOND = 5;


    @IntDef(value = {
            TIME_TYPE_YEAR,
            TIME_TYPE_MONTH,
            TIME_TYPE_DAY,
            TIME_TYPE_HOUR,
            TIME_TYPE_MINUTE,
            TIME_TYPE_SECOND,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface TimeOptions {

    }

    private static final long NEVER_NUMBER = 10000L;
    private static final double NEVER_START_TIME = 111111111.0;
    private static final double NEVER_END_TIME = 99999999999.0;

    public static boolean checkCount(long count) {
        LogUtils.d(TAG, "count = " + count);
        LogUtils.d(TAG, "countPair = " + (count == NEVER_NUMBER));
        if (count == NEVER_NUMBER) {
            return true;
        }
        return count > 0;
    }


    /**
     * 比对时间是不是在指定的时间范围内,比对的是HH:mm:ss,不包含年,月,日
     *
     * @param startTime 开始时间,毫秒值
     * @param endTime   结束时间,毫秒值
     * @param nowTime   当前时间,毫秒值
     * @return
     */
    public static boolean checkTimeInTime(double startTime, double endTime, long nowTime, @TimeOptions int type) {


        if (startTime == NEVER_START_TIME && endTime == NEVER_END_TIME) {
            return true;
        }
        LogUtils.d(TAG, "startTime = " + startTime);
        LogUtils.d(TAG, "endTime = " + endTime);
        LogUtils.d(TAG, "startPair = " + (startTime == NEVER_START_TIME));
        LogUtils.d(TAG, "endTimePair = " + (endTime == NEVER_END_TIME));

        if (nowTime / 1000 >= startTime && nowTime / 1000 <= endTime) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkWeekly(String json) {

        try {
            if (TextUtils.isEmpty(json)) {
                LogUtils.d(TAG, "checkWeekly json = null,return true");
                return true;
            }
            WeeklyBean weeklyBean = new Gson().fromJson(json, WeeklyBean.class);
            System.out.println(weeklyBean.toString());
            Calendar instance = Calendar.getInstance();
            int dayForWeek = instance.get(Calendar.DAY_OF_WEEK);
            if (dayForWeek == 1) {
                LogUtils.d(TAG, "Sunday");
                return parseTime(weeklyBean.getSunday());
            } else if (dayForWeek == 2) {
                LogUtils.d(TAG, "Monday");
                return parseTime(weeklyBean.getMonday());
            } else if (dayForWeek == 3) {
                LogUtils.d(TAG, "Tuesday");
                return parseTime(weeklyBean.getTuesday());
            } else if (dayForWeek == 4) {
                LogUtils.d(TAG, "Wednesday");
                return parseTime(weeklyBean.getWednesday());
            } else if (dayForWeek == 5) {
                LogUtils.d(TAG, "Thursday");
                return parseTime(weeklyBean.getThursday());
            } else if (dayForWeek == 6) {
                LogUtils.d(TAG, "Friday");
                return parseTime(weeklyBean.getFriday());
            } else if (dayForWeek == 7) {
                LogUtils.d(TAG, "Saturday");
                return parseTime(weeklyBean.getSaturday());
            }
            return true;
        } catch (Exception e) {
            LogUtils.e(TAG, "checkWeekly = " + e.getMessage());
            return true;
        }
    }

    public static boolean parseTime(String message) {
        String[] split = message.split(";");
        String format = String.format(Locale.getDefault(), "%tR", System.currentTimeMillis());
        System.out.println(format);
        String[] currentHHMM = format.split(":");

        for (int i = 0; i < split.length; i++) {
            //split[i] = 00:00-24:00
            String time = split[i];
//            LogUtils.d(TAG,"time = " + time);
            //hhMM = [00:00,00:00]
            String[] hhMM = time.split("-");
//            LogUtils.d(TAG,"hhMM0 = " + hhMM[0] + " hhMM1 = " + hhMM[1]);
            //00:00
            String[] hhMMOne = hhMM[0].split(":");
//            LogUtils.d(TAG,"hhMMOne0 = " + hhMMOne[0] + " hhMMOne1 = " + hhMMOne[1]);
            //00:00
            String[] hhMMTwo = hhMM[1].split(":");
//            LogUtils.d(TAG,"hhMMTwo0 = " + hhMMTwo[0] + " hhMMTwo1 = " + hhMMTwo[1]);

            boolean hour = false;
            boolean min = false;
            if (Long.parseLong(hhMMOne[0]) == 0 && Long.parseLong(hhMMTwo[0]) == 24 &&
                    Long.parseLong(hhMMOne[1]) == 0 && Long.parseLong(hhMMTwo[1]) == 0) {
                LogUtils.d(TAG,"====== 全时间通过");
                //当前是全时间,通过
                return true;
            } else {
                if (Long.parseLong(currentHHMM[0]) == Long.parseLong(hhMMOne[0]) && Long.parseLong(currentHHMM[0]) == Long.parseLong(hhMMTwo[0])) {
                    //例子 15:00 - 15:00,需要判断分钟
                    min = Long.parseLong(currentHHMM[1]) >= Long.parseLong(hhMMOne[1]) && Long.parseLong(currentHHMM[1]) <= Long.parseLong(hhMMTwo[1]);
                    LogUtils.d(TAG,"====== 开始时间和结束时间相同");
                    if (min) {
                        return true;
                    }
                } else if (Long.parseLong(currentHHMM[0]) == Long.parseLong(hhMMOne[0])&&Long.parseLong(currentHHMM[0])<Long.parseLong(hhMMTwo[0])){
                    //等于开始时间,小于结束时间
                    LogUtils.d(TAG,"====== 等于开始时间,小于结束时间");
                    min = Long.parseLong(currentHHMM[1]) >= Long.parseLong(hhMMOne[1]);
                    if (min) {
                        return true;
                    }
                } else if (Long.parseLong(currentHHMM[0]) > Long.parseLong(hhMMOne[0]) && Long.parseLong(currentHHMM[0]) == Long.parseLong(hhMMTwo[0])) {
                    //大于开始时间,等于结束时间
                    LogUtils.d(TAG,"====== 大于开始时间,等于结束时间");
                    min = Long.parseLong(currentHHMM[1]) <= Long.parseLong(hhMMTwo[1]);
                    if (min) {
                        return true;
                    }
                } else if (Long.parseLong(currentHHMM[0]) > Long.parseLong(hhMMOne[0]) && Long.parseLong(currentHHMM[0]) < Long.parseLong(hhMMTwo[0])) {
                    LogUtils.d(TAG,"====== 大于开始时间,小于结束时间");
                    return true;
                }
            }
        }
        return false;
    }

}
