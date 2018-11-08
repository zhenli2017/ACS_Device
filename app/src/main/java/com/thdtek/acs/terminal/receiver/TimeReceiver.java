package com.thdtek.acs.terminal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.TextView;

import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DateUtil;
import com.thdtek.acs.terminal.util.DownloadApk;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SPUtils;

import java.util.Locale;

/**
 * Time:2018/7/11
 * User:lizhen
 * Description:
 */

public class TimeReceiver extends BroadcastReceiver {

    private final String TAG = TimeReceiver.class.getSimpleName();
    private TextView mTvTime;
    private TextView mTvData;
    private TextView mTvWeek;


    public TimeReceiver(TextView tvTime, TextView tvData, TextView tvWeek) {
        mTvTime = tvTime;
        mTvData = tvData;
        mTvWeek = tvWeek;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d(TAG, "============ TimeReceiver ============");
        if (intent == null || intent.getAction() == null) {
            LogUtils.e(TAG, "intent intent.getAction() == null");
            return;
        }
        if (mTvTime != null) {
            mTvTime.setText(String.format(Locale.getDefault(), "%tT", System.currentTimeMillis()));
        }
        if (mTvData != null) {
            mTvData.setText(String.format(Locale.getDefault(), "%tm/%<td", System.currentTimeMillis()));
        }
        if (mTvWeek != null) {
            mTvWeek.setText(String.format(Locale.getDefault(), "   %tA", System.currentTimeMillis()));
        }

        int dayOfYear = (int) SPUtils.get(context, Const.TODAY_OF_YEAR, 0);
        if (dayOfYear == DateUtil.getCurrentDayOfYear()) {
            LogUtils.d(TAG, "day 时间相同,已经重启过了");
            return;
        }

        String path = (String) SPUtils.get(MyApplication.getContext(), Const.DOWN_LOAD_APK_PATH);
        if (!TextUtils.isEmpty(path)) {
            DownloadApk.getInstance().updateApp(MyApplication.getContext());
        } else {
            int currentHour = DateUtil.getCurrentHour();
            String deviceDefendTime = AppSettingUtil.getConfig().getDeviceDefendTime();
            if (TextUtils.isEmpty(deviceDefendTime)) {
                deviceDefendTime = "00:00";
            }
            String[] split = deviceDefendTime.split(":");
            if (split.length == 2) {
                try {
                    int hour = Integer.parseInt(split[0]);
                    int minute = Integer.parseInt(split[1]);
                    if (currentHour == hour && minute == DateUtil.getCurrentMintue()) {
                        SPUtils.put(context, Const.TODAY_OF_YEAR, DateUtil.getCurrentDayOfYear(), true);
                        HWUtil.reboot("每天定时任务重启机器");
                    }
                } catch (Exception e) {
                    LogUtils.e(TAG, "hour minute 时间格式化错误");
                }
            }
        }


    }
}
