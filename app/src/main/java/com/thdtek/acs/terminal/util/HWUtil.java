package com.thdtek.acs.terminal.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.hwit.ArmFreqUtils;
import com.hwit.HwitManager;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.base.ThreadPool;
import com.thdtek.acs.terminal.face.FaceTempData;
import com.thdtek.acs.terminal.thread.SerialThread;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;

/**
 * Time:2018/7/4
 * User:lizhen
 * Description:
 */

public class HWUtil {


    private static final String TAG = HWUtil.class.getSimpleName();
    //正式
    private static final boolean DEBUG = false;
    //debug
//    private static final boolean DEBUG = true;


    /**
     * 打开补光灯
     */
    public static void openFillLight() {

        if (!DEBUG) {
            try {
                int k = HwitManager.HwitGetIOValue(3);
                if (k == 0) {
                    //07:00-09:00;10:00-12:00
                    String fillLightTimes = AppSettingUtil.getConfig().getFillLightTimes();
                    if (TextUtils.isEmpty(fillLightTimes)) {
                        return;
                    }
                    String[] split = fillLightTimes.split(";");
                    for (int i = 0; i < split.length; i++) {

                        String subTime = split[i];
                        if (TextUtils.isEmpty(subTime)) {
                            continue;
                        }
                        String[] subSubTimeSplit = subTime.split("-");
                        if (subSubTimeSplit.length != 2) {
                            continue;
                        }
                        String startTime = subSubTimeSplit[0];
                        String endTime = subSubTimeSplit[1];

                        String[] startTimeSplit = startTime.split(":");
                        String[] endTimeSplit = endTime.split(":");

                        if (startTimeSplit.length != 2 || endTimeSplit.length != 2) {
                            continue;
                        }

                        String format = String.format(Locale.getDefault(), "%tR", System.currentTimeMillis());
                        String[] currentHHMM = format.split(":");

                        if (Long.parseLong(currentHHMM[0]) == Long.parseLong(startTimeSplit[0]) && Long.parseLong(currentHHMM[0]) == Long.parseLong(endTimeSplit[0])) {
                            //例子 15:00 - 15:00,需要判断分钟
                            if (Long.parseLong(currentHHMM[1]) >= Long.parseLong(startTimeSplit[1]) && Long.parseLong(currentHHMM[1]) <= Long.parseLong(endTimeSplit[1])) {
                                LogUtils.d(TAG, "========== 打开补光灯 ==========");
                                HwitManager.HwitSetIOValue(3, 1);
                                break;
                            }
                        } else if (Long.parseLong(currentHHMM[0]) == Long.parseLong(startTimeSplit[0]) && Long.parseLong(currentHHMM[0]) < Long.parseLong(endTimeSplit[0])) {
                            //等于开始时间,小于结束时间
                            LogUtils.d(TAG, "====== 等于开始时间,小于结束时间,判断分钟");
                            if (Long.parseLong(currentHHMM[1]) >= Long.parseLong(startTimeSplit[1])) {
                                LogUtils.d(TAG, "========== 打开补光灯 ==========");
                                HwitManager.HwitSetIOValue(3, 1);
                                break;
                            }
                        } else if (Long.parseLong(currentHHMM[0]) > Long.parseLong(startTimeSplit[0]) && Long.parseLong(currentHHMM[0]) == Long.parseLong(endTimeSplit[0])) {
                            //大于开始时间,等于结束时间
                            LogUtils.d(TAG, "====== 大于开始时间,等于结束时间");
                            if (Long.parseLong(currentHHMM[1]) <= Long.parseLong(endTimeSplit[1])) {
                                LogUtils.d(TAG, "========== 打开补光灯 ==========");
                                HwitManager.HwitSetIOValue(3, 1);
                                break;
                            }

                        } else if (Long.parseLong(currentHHMM[0]) > Long.parseLong(startTimeSplit[0]) && Long.parseLong(currentHHMM[0]) < Long.parseLong(endTimeSplit[0])) {
                            LogUtils.d(TAG, "====== 大于开始时间,小于结束时间,打开补光灯");
                            HwitManager.HwitSetIOValue(3, 1);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                LogUtils.e(TAG, "openFillLight = " + e.getMessage());
            }
        }
    }

    /**
     * 关闭补光灯
     */
    public static void closeFillLight() {
        if (!DEBUG) {
            try {
                int i = HwitManager.HwitGetIOValue(3);
                if (i == 1) {
                    LogUtils.d(TAG, "========== 关闭补光灯 ==========");
                    HwitManager.HwitSetIOValue(3, 0);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, "closeFillLight = " + e.getMessage());
            }
        }
    }

    public static boolean BACK_LIGHT_HEIGHT = false;

    /**
     * 增加背光亮度
     */
    public static void openBackLight() {
        if (!DEBUG) {
            try {
                if (!BACK_LIGHT_HEIGHT) {
                    for (int i = 0; i < 15; i++) {
                        HwitManager.HwitAddBrightness();
                    }
                    BACK_LIGHT_HEIGHT = true;
                }
            } catch (Exception e) {
                LogUtils.e(TAG, "openBackLight = " + e.getMessage());
            }
        }
    }

    /**
     * 减少背光亮度
     */
    public static void closeBackLight() {
        if (!DEBUG) {
            try {
                if (BACK_LIGHT_HEIGHT) {
                    for (int i = 0; i < 15; i++) {
                        HwitManager.HwitSubBrightness();
                    }
                    BACK_LIGHT_HEIGHT = false;
                }
            } catch (Exception e) {
                LogUtils.e(TAG, "closeBackLight = " + e.getMessage());
            }
        }


    }

    /**
     * 打开继电器
     */
    public static void openDoorRelay() {
        if (!DEBUG) {
            LogUtils.d(TAG, "================= 打开继电器 ==================");
            try {
                SerialThread.getInstance().relayOpen();
            } catch (Exception e) {
                LogUtils.e(TAG, "openDoorRelay = " + e.getMessage());
            }
        }

    }

    /**
     * 关闭继电器
     */
    public static void closeDoorRelay() {
        if (!DEBUG) {
            LogUtils.d(TAG, "================= 关闭继电器 ==================");
            try {
                SerialThread.getInstance().relayClose();
            } catch (Exception e) {
                LogUtils.e(TAG, "closeDoorRelay = " + e.getMessage());
            }
        }
    }

    /**
     * 打开维根26
     */
    public static void openDoorWeigen26(String number) {
        if (!DEBUG) {
            LogUtils.d(TAG, "================= 打开维根26 ================== " + number);
            try {

                SerialThread.getInstance().onWG26(number);
            } catch (Exception e) {
                LogUtils.e(TAG, "openDoor = " + e.getMessage());
            }
        }
    }

    /**
     * 打开维根34
     */
    public static void openDoorWeigen34(String number) {
        if (!DEBUG) {
            LogUtils.d(TAG, "================= 打开维根34 ================== " + number);
            try {
                SerialThread.getInstance().onWG34(number);
            } catch (Exception e) {
                LogUtils.e(TAG, "openDoorWeigen = " + e.getMessage());
            }
        }
    }

    /**
     * 打开维根26
     */
    public static void openDoorWeigen66(String number) {
        if (!DEBUG) {
            LogUtils.d(TAG, "================= 打开维根66 ================== " + number);
            try {
                SerialThread.getInstance().onWG66(number);
            } catch (Exception e) {
                LogUtils.e(TAG, "openDoor = " + e.getMessage());
            }
        }
    }

    public static void openDoor(final String number) {
        if (AppSettingUtil.getConfig().getPairSuccessOpenDoor() == 1 ) {
            LogUtils.d(TAG, "====== openDoor 全部不开门 =====");
            return;
        }
        ThreadPool.getThread().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int doorType = AppSettingUtil.getConfig().getDoorType();
                    if (doorType == Const.WG_0
                            || doorType == Const.WG_26_0
                            || doorType == Const.WG_34_0
                            || doorType == Const.WG_66_0) {
                        HWUtil.openDoorRelay();
                    }

                    if (doorType == Const.WG_34
                            || doorType == Const.WG_34_0) {
                        openDoorWeigen34(number);
                    } else if (doorType == Const.WG_26
                            || doorType == Const.WG_26_0) {
                        openDoorWeigen26(number);
                    } else if (doorType == Const.WG_66 ||
                            doorType == Const.WG_66_0) {
                        openDoorWeigen66(number);
                    }
                } catch (Exception e) {
                    LogUtils.e(TAG, "openDoor = " + e.getMessage());
                }
            }
        });
    }

    public static void closeDoor() {
        if (AppSettingUtil.getConfig().getPairSuccessOpenDoor() == 1) {
            LogUtils.d(TAG, "====== closeDoor 全部不关门 =====");
            return;
        }
        HWUtil.closeDoorRelay();
    }

    /**
     * 设置终端的系统时间
     * 单位:秒
     *
     * @param time
     */
    public static boolean setClientSystemTime(long time) {
        if (!DEBUG) {
            boolean success = true;
            try {
                String format = String.format(Locale.getDefault(), "%tF-%<tT", time * 1000);
                String[] split = format.split("-|:");
                if (split.length == 6) {
                    HwitManager.HwitSetTime(MyApplication.getContext(),
                            Integer.parseInt(split[0]),
                            Integer.parseInt(split[1]),
                            Integer.parseInt(split[2]),
                            Integer.parseInt(split[3]),
                            Integer.parseInt(split[4]),
                            Integer.parseInt(split[5]));
                    success = true;
                    LogUtils.d(TAG, "时间设置成功 = " + time);
                } else {
                    LogUtils.e(TAG, "时间格式化不正确 = " + time + " format = " + format);
                }
            } catch (Exception e) {
                LogUtils.e(TAG, "setClientSystemTime = " + e.getMessage());
            }
            return success;
        } else {
            return false;
        }
    }

    /**
     * 重启机器
     */
    public static void reboot(String msg) {
        if (!DEBUG) {
            try {
                LogUtils.e(TAG, "========== 重启机器 , " + msg + " ========== ");
                HwitManager.HwitRebootSystem(MyApplication.getContext());

            } catch (Exception e) {
                LogUtils.e(TAG, "reboot = " + e.getMessage());
            }
        }

    }

    public static final int CPU_FEED_1608000 = 1600000;
    public static final int CPU_FEED_696000 = 800000;

    private static int CPU_CURRENT_FEED = 0;

    /**
     * 设置cpu的频率
     */
    public static void setCpuFeed(int value) {
        if (!DEBUG) {
            if (CPU_CURRENT_FEED == value) {
                return;
            } else {
                try {
                    ArmFreqUtils.setSpeedFreq(value);
                    CPU_CURRENT_FEED = value;
                } catch (IOException e) {
                    LogUtils.e(TAG, "setCpuFeed = " + e.getMessage());
                } finally {
                    CPU_CURRENT_FEED = value;
                }
            }
            LogUtils.d(TAG, "cpu feed = " + getCpuFeed());
        }

    }

    /**
     * 获取cpu的频率
     */
    public static int getCpuFeed() {
//        if (!DEBUG) {
//
//            return ArmFreqUtils.getCurFrequencies();
//        }
        return 0;
    }

    /**
     * 隐藏系统状态栏和导航栏
     */
    public static void hideStatusBarAndNaviBar(Context context) {
        if (!DEBUG) {
            try {
                HwitManager.HwitSetHideSystemBar(context);
            } catch (Exception e) {
                LogUtils.e(TAG, "hideStatusBarAndNaviBar = " + e.getMessage());
            }
        }

    }

    /**
     * 显示系统状态栏和导航栏
     */
    public static void showStatusBarAndNaviBar(Context context) {
        if (!DEBUG) {
            try {
                HwitManager.HwitSetShowSystemBar(context);
            } catch (Exception e) {
                LogUtils.e(TAG, "showStatusBarAndNaviBar = " + e.getMessage());
            }
        }
    }

    public static String getWifiMac() {
        if (!DEBUG) {
            return HwitManager.HwitGetWifiMac(MyApplication.getContext());

        }
        return "";
    }

    public static String getBluetoothMac() {
        if (!DEBUG) {

            return HwitManager.HwitGetBluetoothMac();
        }
        return "";
    }

    public static String getEthernetMac() {
        if (!DEBUG) {
            return HwitManager.HwitGetEthernetMac();
        }
        return "";
    }

    public static String getMcuUid() {
        if (!DEBUG) {
            try {
                return SerialThread.getInstance().getMCUID();
            } catch (Exception e) {
                LogUtils.d(TAG, "getMcuuid = " + e.getMessage());
                return "";
            }
        }
        return "";
    }

    public static String getImei() {
        if (!DEBUG) {
            try {
                return HwitManager.HwitGetImei(MyApplication.getContext());
            } catch (Exception e) {
                LogUtils.d(TAG, "getIMEI = " + e.getMessage());
                return "";
            }
        } else {
            return "1111111111111118";
        }
    }

    public static String getIPAddress() {
        if (!DEBUG) {
            String hostIp = "0.0.0.0";
            try {
                Enumeration nis = NetworkInterface.getNetworkInterfaces();
                InetAddress ia = null;
                while (nis.hasMoreElements()) {
                    NetworkInterface ni = (NetworkInterface) nis.nextElement();
                    Enumeration<InetAddress> ias = ni.getInetAddresses();
                    while (ias.hasMoreElements()) {
                        ia = ias.nextElement();
                        if (ia instanceof Inet6Address) {
                            continue;// skip ipv6
                        }
                        String ip = ia.getHostAddress();
                        if (!"127.0.0.1".equals(ip)) {
                            hostIp = ia.getHostAddress();
                            break;
                        }
                    }
                }
            } catch (SocketException e) {
                LogUtils.e(TAG, "getIPAddress = " + e.getMessage());
            }
            return hostIp;
        }
        return "0.0.0.0";
    }

    private static boolean FAN_OPEN = false;

    public static void handleFan() {
        if (!DEBUG) {
            LogUtils.d(TAG, "================= 判断是否打开或关闭风扇 ==================");
            try {
                int cpuTemp = HWUtil.getCpuTemp();
                LogUtils.d(TAG, "CPU温度 = " + cpuTemp + " open_fan = " + FAN_OPEN);
                if (cpuTemp >= 68 || (FAN_OPEN && cpuTemp >= 62)) {
                    //风扇已经打开,当前温度大于68度
                    if (!FAN_OPEN) {
                        HWUtil.openFan();
                    }
                } else {
                    if (FAN_OPEN) {
                        HWUtil.closeFan();
                        LogUtils.d(TAG, "关闭风扇 = " + cpuTemp + " open_fan = " + FAN_OPEN);
                    }
                }
            } catch (Exception e) {
                LogUtils.e(TAG, "closeFan = " + e.getMessage());
            }
        }
    }

    public static void closeFan() {
        if (!DEBUG) {
            LogUtils.d(TAG, "================= 关闭风扇 ==================");
            try {
                FAN_OPEN = false;
                HwitManager.HwitSetIOValue(4, 0);
            } catch (Exception e) {
                LogUtils.e(TAG, "closeFan = " + e.getMessage());
            }
        }
    }


    public static void openFan() {
        if (!DEBUG) {
            try {
                LogUtils.d(TAG, "================= 打开风扇 ==================");
                FAN_OPEN = true;
                HwitManager.HwitSetIOValue(4, 1);
            } catch (Exception e) {
                LogUtils.e(TAG, "openFan = " + e.getMessage());
            }
        }
    }

    public static void installApk(Context context, String path, String pk, String ac) {
        if (!DEBUG) {
            LogUtils.d(TAG, "================= 开始升级apk ================== path = " + path);
            try {
                HwitManager.HwitInstallApp(context,
                        path,
                        pk,
                        ac);
            } catch (Exception e) {
                LogUtils.e(TAG, "installApk = " + e.getMessage());
            }
        }
    }

    public static void launchApp(Context context, String packageName, String appName) {
        if (!DEBUG) {
            LogUtils.d(TAG, "================= 拉起APP ================== ");
            try {
                HwitManager.HwitStartActivity(context, packageName, appName);
            } catch (Exception e) {
                LogUtils.e(TAG, "launchApp = " + e.getMessage());
            }
        }
    }

    public static int getCpuTemp() {
        if (!DEBUG) {
            try {
                int cpuTemp = HwitManager.HwitGetCpuTemp();
                LogUtils.d(TAG, "================= 获取cpu温度 ================== " + cpuTemp);
                return cpuTemp;

            } catch (Exception e) {
                LogUtils.e(TAG, "getCpuTemp = " + e.getMessage());
            }
        }
        return 65;
    }
}
