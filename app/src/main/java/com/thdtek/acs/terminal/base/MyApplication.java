package com.thdtek.acs.terminal.base;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import com.tencent.bugly.crashreport.CrashReport;
import com.thdtek.acs.terminal.server.AppHttpServer;
import com.thdtek.acs.terminal.server.HeartbeatThreadForHttp;
import com.thdtek.acs.terminal.server.UploadRecordThreadForHttp;
import com.thdtek.acs.terminal.socket.core.PushMsgHelper;
import com.thdtek.acs.terminal.socket.core.SendMsgHelper;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DBUtil;
import com.thdtek.acs.terminal.util.DeviceSnUtil;
import com.thdtek.acs.terminal.util.FileUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SPUtils;
import com.thdtek.acs.terminal.yzface.YZFaceUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Time:2018/6/20
 * User:lizhen
 * Description:
 */

public class MyApplication extends MultiDexApplication {
    private static final String TAG = MyApplication.class.getSimpleName();
    private static Context mContext;
    private AppHttpServer httpServer;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Thread.setDefaultUncaughtExceptionHandler(restartHandler);
        //文件路径s
        initFile();
        //初始化日志
        LogUtils.init(Const.DIR_LOG, getLogFilterList());
        LogUtils.delLog(Const.DIR_LOG);
        Const.SDK = (String) SPUtils.get(this, Const.SDK_FACE, Const.SDK_YUN_TIAN_LI_FEI);
        LogUtils.i(TAG, "========= Const.SDK ========== " + Const.SDK);


        //bugly
        CrashReport.initCrashReport(getApplicationContext(), "8f100e8af1", false);

        //初始化sn码
        DeviceSnUtil.createDeviceSn();

        //数据库
        DBUtil.init(mContext, "Thdtek.db");

        //记录一次启动
        startRecord();

        if (Const.IS_OPEN_HTTP_MODE) {
            //启用http
            httpServer = new AppHttpServer();
            httpServer.start();
            YZFaceUtil.getInstance().init();

            //检测过闸流水上传
            UploadRecordThreadForHttp threadForHttp = new UploadRecordThreadForHttp();
            threadForHttp.start();
        }


    }

    private void startRecord() {
        Long count = (Long) SPUtils.get(getContext(), Const.START_COUNT, 0L);
        SPUtils.put(getContext(), Const.START_COUNT, (count + 1), true);

    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (httpServer != null) {
            httpServer.stop();

        }
        HeartbeatThreadForHttp.interrupted();
        YZFaceUtil.getInstance().close();
    }

    public void initFile() {
        //存放通过抓拍图片
        FileUtil.createDir(Const.DIR_IMAGE_RECORD);
        //存放员工正面照图片
        FileUtil.createDir(Const.DIR_IMAGE_EMPLOYEE);
        //存放log文件
        FileUtil.createDir(Const.DIR_LOG);
        //测试用存放临时文件
        FileUtil.createDir(Const.DIR_IMAGE_TEMP);
        //apk文件夹
        FileUtil.createDir(Const.DIR_APK);
        //存放身份证图片的文件夹
        FileUtil.createDir(Const.DIR_ID_IMAGE);
        //存放视频文件
        FileUtil.createDir(Const.DIR_VIDEO);
        //服务器存储photo的临时路径
        FileUtil.createDir(Const.DIR_TEMP_SERVER_PHOTO);
        //创建license文件

        FileUtil.copyLicense(mContext);
    }

    public List<String> getLogFilterList() {
        ArrayList<String> list = new ArrayList<>();
        //添加过滤的tag
//        list.add(CaptureService.class.getSimpleName());
//        list.add(ConnectHandler.class.getSimpleName());
//        list.add(ConnectCore.class.getSimpleName());
        list.add(SendMsgHelper.class.getSimpleName());
        list.add(PushMsgHelper.class.getSimpleName());
        return list;
    }

    // 创建服务用于捕获崩溃异常
    private Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            restartApp();//发生崩溃异常时,重启应用
        }
    };

    public void restartApp() {
        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
        Intent intent = new Intent();
        intent.setAction("intent_restart_app");
        sendBroadcast(intent);
    }
}
