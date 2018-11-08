package com.thdtek.acs.terminal.util;

import android.text.TextUtils;

import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.socket.core.SendMsgHelper;

/**
 * Time:2018/7/14
 * User:lizhen
 * Description:
 */

public class DeviceSnUtil {

    private static final String TAG = DeviceSnUtil.class.getSimpleName();
    private static String DEVICE_SN = "";

    /**
     * 蓝牙mac地址+以太网max地址+wifi mac 地址,md5加密,截取前16位
     */
    public static void createDeviceSn() {
        String mcuUid = HWUtil.getMcuUid();
        if (TextUtils.isEmpty(mcuUid)) {
            DEVICE_SN = Md5.getEncodeDeviceSn(HWUtil.getImei());
        } else {
            DEVICE_SN = Md5.getEncodeDeviceSn(mcuUid);
        }

//        DEVICE_SN = "1234567890123516";

        LogUtils.e(TAG, "========== sn = " + DEVICE_SN + " app version = " + AppUtil.getAppVersionCode(MyApplication.getContext()) + " app version name = " + AppUtil.getAppVersionName(MyApplication.getContext()));
    }

    public static String getDeviceSn() {
        if (TextUtils.isEmpty(DEVICE_SN)) {
            createDeviceSn();
        }
        return DEVICE_SN;
    }

}
