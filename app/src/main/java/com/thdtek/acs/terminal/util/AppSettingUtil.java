package com.thdtek.acs.terminal.util;

import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.text.TextUtils;

import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.bean.ConfigBean;
import com.thdtek.acs.terminal.socket.core.AES;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import greendao.ConfigBeanDao;

/**
 * Time:2018/6/28
 * User:lizhen
 * Description:
 * 本地的各项配置
 */

public class AppSettingUtil {
    private static final String TAG = AppSettingUtil.class.getSimpleName();

    private static ConfigBean mConfig;

    /**
     * 获取加密的key
     *
     * @return
     */
    public static String getDeviceAesKey() {
        String key = (String) SPUtils.get(MyApplication.getContext(), Const.DEVICE_AES_KEY, "");
        if (TextUtils.isEmpty(key)) {
            return "";
        } else {
            byte[] bytes = ByteFormatTransferUtils.hexStringToBytes(key);
            return new String(AESUtils.dec(bytes, AES.AES_KEY.getBytes()));
        }

    }

    /**
     * 保存加密的key
     */
    public static void setDeviceAesKey(String aesKey) {
        if (TextUtils.isEmpty(aesKey)) {
            SPUtils.put(MyApplication.getContext(), Const.DEVICE_AES_KEY, "");
        } else {
            SPUtils.put(MyApplication.getContext(), Const.DEVICE_AES_KEY,
                    ByteFormatTransferUtils.bytesToHexStringNoSpace(AESUtils.enc(aesKey.getBytes(), AES.AES_KEY.getBytes())));
        }

    }


    public static boolean checkDeviceKeyIsEmpty() {
        return TextUtils.isEmpty(getDeviceAesKey());
    }


    public static void saveConfig(ConfigBean config) {
        DBUtil.getDaoSession().getConfigBeanDao().insertOrReplace(config);
    }

    public static ConfigBean getConfig() {
        return getConfig(false);
    }

    public static ConfigBean getConfig(boolean reload) {
        if (mConfig == null || reload) {
            List<ConfigBean> list = DBUtil.getDaoSession().getConfigBeanDao().queryBuilder().list();
            if (list == null || list.size() == 0) {
                mConfig = setDefaultConfig();
            } else {
                mConfig = list.get(0);
            }
        }
        return mConfig;
    }

    public static ConfigBean setDefaultConfig() {
        ConfigBean configBean = new ConfigBean();
        configBean.setAppWelcomeMsg("识别成功");
        configBean.setAppWelcomeMusic("");
        configBean.setCameraDetectType(0);
        configBean.setDeviceAppVersion(AppUtil.getAppVersionName(MyApplication.getContext()));
        configBean.setDeviceCameraSdkVersion("");
        configBean.setDeviceCpuTemperature(0);
        configBean.setDeviceDefendTime("00:00");
        configBean.setDeviceElapsedRealtime(0L);
        configBean.setDeviceHardwareSdkVersion("");
        configBean.setDeviceIntoOrOut(0);
        configBean.setDeviceIpAddress("");
        configBean.setDeviceMusicSize(0);
        configBean.setDeviceName("");
        configBean.setDeviceNetworkIpType(0);
        configBean.setDeviceNetworkType(0);
        configBean.setDeviceRamMaxSize("");
        configBean.setDeviceRamTotalSize("");
        configBean.setDeviceRamUseSize("");
        configBean.setDeviceRegisterTime(0L);
        configBean.setDeviceRomAvailableSize("");
        configBean.setDeviceRomSize("");
        configBean.setDeviceSerialNumber("");
        configBean.setDeviceServiceTime(0);
        configBean.setDeviceSn(DeviceSnUtil.getDeviceSn());
        configBean.setDeviceSystemVersion("");
        configBean.setDeviceTemperature(0);
        configBean.setDoorType(26);
        configBean.setFaceFeaturePairNumber(Const.SDK.equals(Const.SDK_YUN_TIAN_LI_FEI) ? 0.92f : 0.6f);
        configBean.setFaceFeaturePairSuccessOrFailWaitTime(2000);
        configBean.setOpenDoorContinueTime(1000);
        configBean.setOpenDoorType(0);
        configBean.setServerIp("");
        configBean.setServerPort(0);
        configBean.setAppFailMsg("识别失败");
        configBean.setIdFeaturePairNumber(Const.SDK.equals(Const.SDK_YUN_TIAN_LI_FEI) ? 0.80f : 0.5f);
        configBean.setGuestOpenDoorType(0);
        configBean.setGuestOpenDoorNumber("12345678");
        DBUtil.getDaoSession().getConfigBeanDao().insertOrReplace(configBean);
        return configBean;
    }

    public static boolean checkServerIpAndPortIsEmpty() {
        ConfigBean configBean = getConfig(true);
        String serverIp = configBean.getServerIp();
        int port = configBean.getServerPort();
        if (TextUtils.isEmpty(serverIp) || port == 0) {
            return true;
        }
        return false;
    }


    //删除图片的时间间隔
    private static final int DELETE_IMAGE_TIME = 86400000;
    //    private static final int DELETE_IMAGE_TIME = 5000;
    private static final int DELETE_IMAGE_INDEX = 5000;
    private static long DELETE_IMAGE_LAST_TIME = 0;


    /**
     * 当log的截图超过1000张的时候,删除以前的图片
     * 每24个小时检查一次
     * 每次开机检查一次
     *
     * @param dir 图片保存的文件夹路径
     */
    public static void deleteImageOver1000(File dir, int number) {
        System.out.println("============== deleteImageOver1000");
        //获取开机到现在的时间
        long startTime = SystemClock.elapsedRealtime();
        if (DELETE_IMAGE_LAST_TIME != 0) {
            //上次删除图片的时间不是0,表示已经删除过一次,此时需要计算是否需要删除
            if (startTime - DELETE_IMAGE_LAST_TIME >= 1000) {
                DELETE_IMAGE_LAST_TIME = startTime;
                LogUtils.d(TAG, "时间间隔 长,删除图片");
            } else {
                LogUtils.d(TAG, "时间间隔 短,不删除图片");
                return;
            }
        } else {
            LogUtils.d(TAG, "开机第一次检查是否需要删除图片");
            DELETE_IMAGE_LAST_TIME = SystemClock.elapsedRealtime();
        }
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            LogUtils.d(TAG, "deleteImageOver1000  == null || 文件不存在 || 文件不是文件夹");
            return;
        }
        File[] files = dir.listFiles();
        if (files.length <= number) {
            LogUtils.d(TAG, "当前图片数量是 = " + files.length);
            return;
        } else {
            LogUtils.d(TAG, "当前图片数量是 ===== " + files.length);
            for (int i = files.length - 1; i >= number; i--) {
                LogUtils.d(TAG, "deleteImageOver1000 file delete = " + files[i].getName());
                files[i].delete();
            }
        }
    }

    /**
     * 当log的截图超过1000张的时候,删除以前的图片
     * 每24个小时检查一次
     * 每次开机检查一次
     *
     * @param dir 图片保存的文件夹路径
     */
    public static void deleteImageOver1000(File dir) {
        //获取开机到现在的时间
        long startTime = SystemClock.elapsedRealtime();
        if (DELETE_IMAGE_LAST_TIME != 0) {
            //上次删除图片的时间不是0,表示已经删除过一次,此时需要计算是否需要删除
            if (startTime - DELETE_IMAGE_LAST_TIME >= DELETE_IMAGE_TIME) {
                DELETE_IMAGE_LAST_TIME = startTime;
                LogUtils.d(TAG, "开机 不是第一次 检查是否需要删除图片");
            } else {
                return;
            }
        } else {
            LogUtils.d(TAG, "开机第一次检查是否需要删除图片");
            DELETE_IMAGE_LAST_TIME = SystemClock.elapsedRealtime();
        }
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            LogUtils.d(TAG, "deleteImageOver1000  == null || 文件不存在 || 文件不是文件夹");
            return;
        }
        File[] files = dir.listFiles();
        if (files.length <= DELETE_IMAGE_INDEX) {
            LogUtils.d(TAG, "当前图片数量是 = " + files.length);
            return;
        } else {
            LogUtils.d(TAG, "当前图片数量是 = " + files.length);

            for (int i = 0; i < files.length - DELETE_IMAGE_INDEX; i++) {
                System.out.println("file delete = " + files[i].getName());
                files[i].delete();
            }
        }
    }

    /**
     * 删除所有的截图
     *
     * @param file 截图所在的文件夹
     */
    public static void deleteImageDir(File file) {
        if (file != null && file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
                LogUtils.d(TAG, "当前截图文件夹内没有图片");
            } else {
                for (File allFile : files) {
                    allFile.delete();
                }
            }
        }
    }

    /**
     * 重置所有的配置
     *
     * @param dir
     */
    public static void resetAll(File dir) {
        deleteImageDir(dir);
    }

    public static void updateConfig(Msg.Message message) {
        Msg.Message.SetConfigReq setConfigReq = message.getSetConfigReq();

        Msg.Message.Config config = setConfigReq.getConfig();


        ConfigBeanDao configBeanDao = DBUtil.getDaoSession().getConfigBeanDao();
        ConfigBean configBean = AppSettingUtil.getConfig(true);
        if (config.hasAppWelcomeMsg()) {
            configBean.setAppWelcomeMsg(config.getAppWelcomeMsg());
        }
        if (config.hasAppWelcomeMusic()) {
            configBean.setAppWelcomeMusic(config.getAppWelcomeMusic());
        }
        if (config.hasCameraDetectType()) {
            configBean.setCameraDetectType(config.getCameraDetectType());
        }
        if (config.hasDeviceDefendTime()) {
            configBean.setDeviceDefendTime(config.getDeviceDefendTime());
            SPUtils.put(MyApplication.getContext(), Const.TODAY_OF_YEAR, 0, true);
        }
        if (config.hasDeviceIntoOrOut()) {
            configBean.setDeviceIntoOrOut(config.getDeviceIntoOrOut());
        }
        if (config.hasDeviceMusicSize()) {
            configBean.setDeviceMusicSize(config.getDeviceMusicSize());
            SoundUtil.setVolume(config.getDeviceMusicSize());
        }
        if (config.hasDeviceName()) {
            configBean.setDeviceName(config.getDeviceName());
        }
        if (config.hasDeviceSerialNumber()) {
            configBean.setDeviceSerialNumber(config.getDeviceSerialNumber());
        }
        if (config.hasDoorType()) {
            configBean.setDoorType(config.getDoorType());
        }
        if (config.hasFaceFeaturePairNumber()) {
            configBean.setFaceFeaturePairNumber(config.getFaceFeaturePairNumber());
        }
        if (config.hasFaceFeaturePairSuccessOrFailWaitTime()) {
            configBean.setFaceFeaturePairSuccessOrFailWaitTime(config.getFaceFeaturePairSuccessOrFailWaitTime());
        }
        if (config.hasOpenDoorContinueTime()) {
            configBean.setOpenDoorContinueTime(config.getOpenDoorContinueTime());
        }
        if (config.hasOpenDoorType()) {
            configBean.setOpenDoorType(config.getOpenDoorType());
        }
        if (config.hasAppFailMsg()) {
            configBean.setAppFailMsg(config.getAppFailMsg());
        }
        if (config.hasVisitorCardNo()) {
            try {
                if (AppSettingUtil.getConfig().getDoorType() == Const.WG_34) {
                    configBean.setGuestOpenDoorNumber(String.format(Locale.getDefault(), "%010d", Long.parseLong(config.getVisitorCardNo())));
                } else {
                    configBean.setGuestOpenDoorNumber(String.format(Locale.getDefault(), "%08d", Long.parseLong(config.getVisitorCardNo())));
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e(TAG, "添加的人的 IC 卡 Integer 转换失败 = " + config.hasVisitorCardNo());
                configBean.setGuestOpenDoorNumber(Const.ERROR_EMPLOYEE_CARD_NUMBER);
            }

        }
        if (config.hasVisitorOpenDoorType()) {
            configBean.setGuestOpenDoorType(config.getVisitorOpenDoorType());
        }
        if (config.hasIdCardFaceFeaturePairNumber()) {

            configBean.setIdFeaturePairNumber(config.getIdCardFaceFeaturePairNumber());
        }


        configBeanDao.update(configBean);
        AppSettingUtil.getConfig(true);
        LogUtils.d(TAG, configBean.toString());
    }

    public static String[] getRomSize() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());//调用该类来获取磁盘信息（而getDataDirectory就是内部存储）
        long tcounts = statFs.getBlockCountLong();//总共的block数
        long counts = statFs.getAvailableBlocksLong(); //获取可用的block数
        long size = statFs.getBlockSizeLong(); //每格所占的大小，一般是4KB==
        double availROMSize = 1.0 * counts * size / (1024 * 1024);//可用内部存储大小
        double totalROMSize = 1.0 * tcounts * size / (1024 * 1024); //内部存储总大小
        return new String[]{availROMSize + "", totalROMSize + ""};
    }
}
