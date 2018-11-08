package com.thdtek.acs.terminal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.text.TextUtils;
import android.util.Log;

import com.hwit.HwitManager;
import com.thdtek.acs.terminal.base.ThreadPool;
import com.thdtek.acs.terminal.ui.login.LoginActivity;
import com.thdtek.acs.terminal.ui.manage.ManagerActivity;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.UsbUtil;

/**
 * Time:2018/6/21
 * User:lizhen
 * Description:
 */

public class UsbReceiver extends BroadcastReceiver {
    private final String TAG = UsbReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        //暂时先这样用
        if (intent == null || intent.getAction() == null) {
            LogUtils.d(TAG, "UsbReceiver 收到 usb 插入广播,intent = null ");
            return;
        }
        HWUtil.openBackLight();
        if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)
                || intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
            LogUtils.d(TAG, "收到 usb 插入广播");
            String path = intent.getDataString();
            if (TextUtils.isEmpty(path)) {
                Log.e(TAG, " ==== USB path null,return ==== ");
                return;
            }
            LogUtils.d(TAG, " ================== ACTION_MEDIA_MOUNTED = 有设备挂载 path -> " + path);
            final String newPath = path.replaceAll("file://", "");
            if (UsbUtil.checkKeyFile(newPath)) {
                startActivity(context);
            }
        } else if (intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED)
                || (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED))) {
            LogUtils.d(TAG, "UsbReceiver 收到 usb 插入广播,ACTION_MEDIA_UNMOUNTED USB 移除 ");
        }
    }

    public void startActivity(Context context) {
        Intent intent = new Intent(context, ManagerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
