package com.thdtek.acs.terminal.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageManager;
import android.icu.text.UFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.hwit.HwitManager;
import com.thdtek.acs.terminal.base.MyApplication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Time:2018/5/14
 * User:lizhen
 * Description:
 */

public class DownloadApk {

    private final String TAG = DownloadApk.class.getSimpleName();

    private Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            LogUtils.e(TAG,"============== 打开 acswatch ");
            HWUtil.launchApp(MyApplication.getContext(),"com.example.acswatch","com.example.acswatch.MainActivity");
            System.exit(0);
            return false;
        }
    });
    private static DownloadApk mDownloadApkBiz2;
    private Call mCallGetContentLength;
    private Call mCallGetApkFile;
    public static long mDownLoadApkSize = 0L;
    private static String APK_URL = "";

    private DownloadApk() {
    }

    public static DownloadApk getInstance() {
        if (mDownloadApkBiz2 == null) {
            synchronized (DownloadApk.class) {
                if (mDownloadApkBiz2 == null) {
                    mDownloadApkBiz2 = new DownloadApk();
                }
            }
        }
        return mDownloadApkBiz2;
    }

    public void downLoad(Context context) {
        String url = (String) SPUtils.get(context, Const.DOWN_LOAD_APK_URL, "");
        if (TextUtils.isEmpty(url)) {
            LogUtils.d(TAG, " ==== 没有需要下载的apk url,return");
            return;
        }
        int versionCode = (int) SPUtils.get(context, Const.DOWN_LOAD_APK_FILE_VERSION_CODE, 0);
        if (versionCode == 0) {
            LogUtils.d(TAG, " ==== 没有需要下载的 versionCode ,return");
            return;
        }


        boolean updateNow = (boolean) SPUtils.get(context, Const.DOWN_LOAD_APK_UPDATE_NOW, true);
        downLoad(context, url, versionCode, false, updateNow);
    }

    public void downLoad(Context context, String url, int versionCode, boolean checkNewDownload, boolean updateNow) {
        //关闭正在下载的所有请求
        closeAll();
        if (checkNewDownload) {
            //有新版本的apk下载
            LogUtils.d(TAG, " ==== 新的apk下载");
            clearAll();
        }
        //获取

        File newFile = createNewFile(context, versionCode + "");
        SPUtils.put(context, Const.DOWN_LOAD_APK_URL, url);
        SPUtils.put(context, Const.DOWN_LOAD_APK_FILE_VERSION_CODE, versionCode);
        SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_APK_UPDATE_NOW, true);
        long downLoadEndByte = (long) SPUtils.get(context, Const.DOWN_LOAD_APK_FILE_END_BYTE, 0L);
        LogUtils.d(TAG, " ==== 总的文件的长度 -> " + downLoadEndByte);
        if (downLoadEndByte == 0) {
            LogUtils.d(TAG, " ==== 下载新的apk文件 ");
            getContentLengthAndDownLoadApk(context, url, updateNow, newFile);
        } else {
            long downLoadStartByte = (long) SPUtils.get(context, Const.DOWN_LOAD_APK_FILE_START_BYTE, 0L);
            mDownLoadApkSize = downLoadStartByte;
            LogUtils.d(TAG, " ==== 继续下载apk文件 , 已经下载的长度 = " + downLoadStartByte);
            downLoadApk(context, url, updateNow, newFile, downLoadStartByte, downLoadEndByte);
        }
    }

    private File createNewFile(Context context, String versionCode) {
        String path = Const.DIR_APK;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(path, "new_" + versionCode + "_temp.apk");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.e(TAG, " ==== 新apk文件创建失败 -> " + e.getMessage());
            }
        }
        SPUtils.put(context, Const.DOWN_LOAD_APK_TEMP_PATH, file.getAbsolutePath());
        LogUtils.d(TAG, " ==== apk 文件路径 -> " + path);
        return file;
    }

    /**
     * 获取要下载的apk文件大小并且开始下载apk
     *
     * @param url
     * @return
     */
    public void getContentLengthAndDownLoadApk(final Context context, final String url, final boolean updateNow, final File file) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        APK_URL = url;
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, " ==== 获取apk的大小失败 - > " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mCallGetContentLength = call;
                Long downLoadEndByte = response.body().contentLength();
                Log.d(TAG, " ==== 获取apk的大小成功 , apk 文件大小 -> " + downLoadEndByte);
                SPUtils.put(context, Const.DOWN_LOAD_APK_FILE_END_BYTE, downLoadEndByte);
                downLoadApk(context, url, updateNow, file, 0, downLoadEndByte);
            }
        });
    }

    public void downLoadApk(final Context context, String url, final boolean updateNow, final File file, final long downLoadStartByte, final long downLoadEndByte) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .addHeader("range", "bytes=" + downLoadStartByte + "-" + downLoadEndByte)
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, " ==== apk下载失败 = " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(context, updateNow, call, response, file, downLoadStartByte, downLoadEndByte);
            }
        });
    }

    private void handleResponse(Context context, boolean updateNow, Call call, Response response, File file, long downLoadStartByte, long downLoadEndByte) {
        InputStream inputStream = null;
        mCallGetApkFile = call;
        try {
//            inputStream = response.body().byteStream();
//            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
//            FileChannel channel = randomAccessFile.getChannel();
//            int size = (int) (downLoadEndByte - downLoadStartByte);
//            MappedByteBuffer mappedBuffer = channel.map(FileChannel.MapMode.READ_WRITE,
//                    downLoadStartByte, size);
//            int len;
//            byte[] bytes = new byte[1024 * 8];
//            int count = 0;
//            while ((len = inputStream.read(bytes)) != -1 && !call.isCanceled()) {
//                mappedBuffer.put(bytes, 0, len);
//                downLoadStartByte += len;
//                if (count % 50 == 0) {
//                    System.out.println(" ===== len = " + len);
//                    SPUtils.put(MyApplication.getContext(),Const.DOWN_LOAD_APK_FILE_START_BYTE, downLoadStartByte);
//                    DeviceUtil.mDownLoadApkSize = downLoadStartByte;
//                }
//                count++;
//            }
            Bundle bundleStart = new Bundle();
            bundleStart.putString(Const.DOWN_LOAD_CURRENT_STATUS, Const.DOWN_LOAD_START);
            bundleStart.putLong(Const.DOWN_LOAD_FILE_LENGTH, downLoadEndByte);
            bundleStart.putLong(Const.DOWN_LOAD_CURRENT_LENGTH, mDownLoadApkSize);
            Intent intent = new Intent(Const.DOWN_RECEIVE);
            intent.putExtras(bundleStart);
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);

            inputStream = response.body().byteStream();
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(downLoadStartByte);
            int len;
            byte[] bytes = new byte[1024 * 8];
            int count = 0;
            while ((len = inputStream.read(bytes)) != -1 && !call.isCanceled()) {
                randomAccessFile.write(bytes, 0, len);
                downLoadStartByte += len;
                if (count % 100 == 0) {
                    SPUtils.put(context, Const.DOWN_LOAD_APK_FILE_START_BYTE, downLoadStartByte);
                    mDownLoadApkSize = downLoadStartByte;
                    Bundle bundle = new Bundle();
                    bundle.putString(Const.DOWN_LOAD_CURRENT_STATUS, Const.DOWN_LOAD_ING);
                    bundle.putLong(Const.DOWN_LOAD_FILE_LENGTH, downLoadEndByte);
                    bundle.putLong(Const.DOWN_LOAD_CURRENT_LENGTH, mDownLoadApkSize);
                    intent = new Intent(Const.DOWN_RECEIVE);
                    intent.putExtras(bundle);
                    LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);
                }
                count++;
            }
            LogUtils.d(TAG, " ==== 文件下载成功 ====");
            Bundle bundle = new Bundle();
            bundle.putString(Const.DOWN_LOAD_CURRENT_STATUS, Const.DOWN_LOAD_END);
            intent = new Intent(Const.DOWN_RECEIVE);
            intent.putExtras(bundle);
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);
            downLoadSuccess(context, file, updateNow);
        } catch (Exception e) {
            Log.d(TAG, " ==== apk下载发生异常 -> " + e.getMessage());
            SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_APK_FILE_START_BYTE, downLoadStartByte);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void downLoadSuccess(Context context, File file, boolean updateNow) {
        LogUtils.d(TAG, " ==== 升级包下载结束 ====");
        int versionCode = (int) SPUtils.get(MyApplication.getContext(), Const.DOWN_LOAD_APK_FILE_VERSION_CODE, 0);
        String path = Const.DIR_APK + File.separator + "new_" + versionCode + ".apk";
        File newFile = new File(path);
        boolean b = file.renameTo(newFile);
        LogUtils.d(TAG, " ==== apk 重命名 -> " + b + " path -> " + path);
        SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_APK_PATH, path);
        SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_APK_URL, "");
        SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_APK_FILE_END_BYTE, 0L);
        SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_APK_FILE_START_BYTE, 0L);

        mDownLoadApkSize = 0L;
        if (updateNow) {
            updateApp(context);
        }
    }


    public void clearAll() {
        LogUtils.d(TAG, " ==== 清空所有的临时数据并删除临时文件");
        SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_APK_FILE_END_BYTE, 0L);
        SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_APK_FILE_START_BYTE, 0L);
        SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_APK_FILE_VERSION_CODE, 0);
        SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_APK_URL, "");
        SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_APK_PATH, "");
        SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_APK_TEMP_PATH, "");

        clearApk(true);
    }

    public void clearApk(boolean deleteTemp) {
        File file = new File(Const.DIR_APK);

        try {
            if (null != file && file.exists() && file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (deleteTemp) {
                        LogUtils.d(TAG, " ==== 删除apk,apk name = " + files[i].getName());
                        files[i].delete();
                    } else {
                        if (files[i].getName().contains("temp")) {
                            LogUtils.d(TAG, " ==== 不删除 apk name = " + files[i].getName());
                        } else {
                            LogUtils.d(TAG, " ==== 删除apk,apk name = " + files[i].getName());
                            files[i].delete();
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.e(TAG, " ==== apk clearAll 删除发生异常 -> " + e.getMessage());
        }
    }

    public void closeAll() {
        LogUtils.d(TAG, " ==== 取消所有的okHttp请求 ====");
        closeCallGetContentLength();
        closeCallGetApkFile();
    }

    public void closeCallGetContentLength() {
        LogUtils.d(TAG, " ==== 取消获取文件大小的okHttp请求 ==== ");
        if (mCallGetContentLength != null) {
            mCallGetContentLength.cancel();
        }
    }

    public void closeCallGetApkFile() {
        LogUtils.d(TAG, " ==== 取消下载文件的okHttp请求 ====");
        if (mCallGetApkFile != null) {
            mCallGetApkFile.cancel();
        }
    }

    public void updateApp(Context context) {

        String path = (String) SPUtils.get(context, Const.DOWN_LOAD_APK_PATH);
        int appVersionCode = AppUtil.getAppVersionCode(context);
        int versionCode = (int) SPUtils.get(context, Const.DOWN_LOAD_APK_FILE_VERSION_CODE, 0);

        if (TextUtils.isEmpty(path)) {
            LogUtils.d(TAG, " ==== APP Apk 路径不存在,return");
            return;
        }
        if (versionCode != 0 && versionCode > appVersionCode) {
            LogUtils.d(TAG, " ==== APP 开始升级 path = " + path);
            SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_APK_FILE_VERSION_CODE, 0);
            SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_APK_PATH, "");
            if (APK_URL.contains("ACSWatch")) {
                HWUtil.installApk(context, path, null, null);
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(1,Const.HANDLER_DELAY_TIME_10000);
                }
            } else {
                HWUtil.installApk(context, path, "com.thdtek.acs.terminal",
                        "com.thdtek.acs.terminal.ui.welcome.WelcomeActivity");
            }
        } else {
            LogUtils.d(TAG, " ==== APP 下载版本小于或等于当前版本,不升级");
        }
    }
}
