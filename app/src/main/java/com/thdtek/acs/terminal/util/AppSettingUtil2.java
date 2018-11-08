package com.thdtek.acs.terminal.util;

import com.thdtek.acs.terminal.base.MyApplication;

//用于获取ConfigBean中随时变化的数据
public class AppSettingUtil2 {

    public static String getDeviceAppVersion(){
        return AppUtil.getAppVersionName(MyApplication.getContext());
    }

    public static String getDeviceRomSize(){
        String[] romSize = AppSettingUtil.getRomSize();
        return romSize[1] + " MB";
    }

    public static String getDeviceRomAvailableSize(){
        String[] romSize = AppSettingUtil.getRomSize();
        return romSize[0] + " MB";
    }

    public static String getDeviceRamMaxSize(){
        return null;
    }

    public static String getDeviceRamTotalSize(){
        return null;
    }

    public static String getDeviceRamUseSize(){
        return null;
    }

    public static String getDviceServiceTime(){
        return null;
    }



}
