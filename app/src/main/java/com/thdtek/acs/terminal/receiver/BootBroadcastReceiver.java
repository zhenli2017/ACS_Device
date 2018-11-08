package com.thdtek.acs.terminal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.thdtek.acs.terminal.util.AppUtil;
import com.thdtek.acs.terminal.util.LogUtils;


/**
 * 实现开机自启动
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = BootBroadcastReceiver.class.getSimpleName();

    private static boolean SHUTDOWN = false;//是否收到过关机广播   默认false


    public static boolean isSHUTDOWN() {
        return SHUTDOWN;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            LogUtils.d(TAG, "收到开机广播,拉起APP");
            AppUtil.launchApp(context, AppUtil.getPackageName());
        } else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
            LogUtils.d(TAG, "收到关机广播,return");
            SHUTDOWN = true;
        }
    }
}