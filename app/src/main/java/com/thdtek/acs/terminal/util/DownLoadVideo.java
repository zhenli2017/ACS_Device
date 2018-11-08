package com.thdtek.acs.terminal.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.tts.tools.MD5;
import com.thdtek.acs.terminal.base.MyApplication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Time:2018/10/12
 * User:lizhen
 * Description:
 */

public class DownLoadVideo {

    private static final String TAG = DownLoadVideo.class.getSimpleName();

    private static DownLoadVideo mDownLoadVideo = new DownLoadVideo();

    private Call mCallGetContentLength;
    private Call mCallGetApkFile;

    private int mStartByte;
    private int mEndByte;
    private long mDownLoadApkSize = 0L;

    private int mRepeatCount = 0;

    public static DownLoadVideo getInstance() {
        return mDownLoadVideo;
    }

    /**
     * 关闭所有当前下载
     */
    public void closeAll() {
        LogUtils.d(TAG, "========== closeAll 关闭当前video下载连接,清空所有的_temp文件 ==========");
        if (mCallGetContentLength != null) {
            mCallGetContentLength.cancel();
        }
        if (mCallGetApkFile != null) {
            mCallGetApkFile.cancel();
        }
    }

    public void clearMinSizeFile() {
        LogUtils.d(TAG, "========== clearMinSizeFile 开始清除video 小文件 ==========");
        File dir = new File(Const.DIR_VIDEO);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.length() <= Const.VIDEO_MIN_SIZE) {
                    LogUtils.d(TAG, "========== clearMinSizeFile 删除的文件名 = " + file.getName());
                    file.delete();
                }
            }
        }
    }

    public void clearAllFile() {
        File dir = new File(Const.DIR_VIDEO);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                LogUtils.d(TAG, "========== clearAllFile 删除的文件名 = " + file.getName());
                file.delete();
            }
        }
    }

    public void clearSDData(Context context) {
        //设置最新的视频保存路径为空
        SPUtils.put(context, Const.DOWN_LOAD_NEW_VIDEO_PATH, "");
        //设置上一次视频下载的号码_开始位置_结束位置 = 空
        SPUtils.put(context, Const.DOWN_LOAD_NUMBER_START_END_URL_FILE, "");
        //video 的url
        SPUtils.put(context, Const.DOWN_LOAD_VIDEO_URLS, "");
    }

    /**
     * 文件名 : 视频播放顺序号_MD5_temp
     *
     * @param fileName
     * @return
     */
    private File createNewFile(String fileName, int number) {
        String path = Const.DIR_VIDEO;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(path, fileName + "_temp");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.e(TAG, "========== createNewFile 新video文件创建失败 -> " + e.getMessage());
                return null;
            }
        }
        LogUtils.d(TAG, "========== createNewFile video 文件路径 -> " + path + " -> " + file.getAbsolutePath());
        return file;
    }

    public boolean checkUrlAndFileExist(String url, int index) {
        File dir = new File(Const.DIR_VIDEO);
        if (!dir.exists() || !dir.isDirectory()) {
            LogUtils.d(TAG, "========== checkUrlAndFileExist Const.DIR_VIDEO 不存在或不是一个文件夹 ==========");
            return false;
        }
        File[] files = dir.listFiles();
        String fileName = Md5.md5(url);
        for (int i = 0; i < files.length; i++) {
            LogUtils.d(TAG, "========== checkUrlAndFileExist video 文件 = files[i].getName()-> " + files[i].getName() + " fileName - > " + fileName + " " + (TextUtils.equals(fileName, files[i].getName())));
            if (TextUtils.equals(fileName, files[i].getName())) {
                LogUtils.d(TAG, "========== checkUrlAndFileExist video 文件存在 = " + files[i].getName());
                return true;
            }
        }
        return false;
    }

    public void downLoadVideo(Context context) {
        LogUtils.d(TAG, "========== downLoadVideo 开始下载video,从本地获取url ==========");
        String urls = (String) SPUtils.get(context, Const.DOWN_LOAD_VIDEO_URLS);
        if (TextUtils.isEmpty(urls)) {
            LogUtils.d(TAG, "========== downLoadVideo urls 为空,return ==========");
            return;
        }
        String number_start_end_url_file = (String) SPUtils.get(context, Const.DOWN_LOAD_NUMBER_START_END_URL_FILE);
        if (TextUtils.isEmpty(number_start_end_url_file)) {
            LogUtils.d(TAG, "========== downLoadVideo 上一次 没有 未完成的video下载 ==========");
            downLoad(context, urls);
        } else {
            LogUtils.d(TAG, "========== downLoadVideo 上一次 有 未完成的video下载 ==========");
            String[] split = number_start_end_url_file.split("\\|");
            for (int i = 0; i < split.length; i++) {
                LogUtils.d(TAG, "========== downLoadVideo = " + split[i]);
            }
            String replaceAll = Md5.md5(split[3].replaceAll("http://" + AppSettingUtil.getConfig().getServerIp() + ":", ""));
            LogUtils.d(TAG, "========== downLoadVideo = " + Const.DIR_VIDEO + "/" + replaceAll + "_temp");
            File file = new File(Const.DIR_VIDEO + "/" + replaceAll + "_temp");
            if (!file.exists()) {
                LogUtils.d(TAG, "========== downLoadVideo 文件不存在 = " + Const.DIR_VIDEO + "/" + replaceAll + "_temp");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            downLoadCurrentVideo(context, split[3], file, Long.parseLong(split[1]), Long.parseLong(split[2]), Integer.parseInt(split[0]));
        }
    }


    public void downLoadVideo(Context context, String existFileName, String deleteFileName, String urls) {
        closeAll();
        LogUtils.d(TAG, "========== downLoadVideo existFileName 已经存在的文件 -> " + existFileName);
        LogUtils.d(TAG, "========== downLoadVideo deleteFileName 需要删除的文件 -> " + deleteFileName);

        //删除以前所有的未完成的文件
        File dir = new File(Const.DIR_VIDEO);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.getName().contains("_temp")) {
                    LogUtils.d(TAG, "========== closeAll 删除的文件名 = " + file.getName());
                    file.delete();
                }
            }
        }

        //删除新的url不存在的文件
        String[] deleteFileNameList = deleteFileName.split(";");
        for (int i = 0; i < deleteFileNameList.length; i++) {
            if (!TextUtils.isEmpty(deleteFileNameList[i])) {
                File file = new File(deleteFileNameList[i]);
                if (file.exists()) {
                    file.delete();
                    LogUtils.d(TAG, "========== downLoadVideo 删除文件 -> " + file.getName());
                }
            }
        }

        //设置最新的视频保存路径为空
        SPUtils.put(context, Const.DOWN_LOAD_NEW_VIDEO_PATH, existFileName);
        //设置上一次视频下载的号码_开始位置_结束位置 = 空
        SPUtils.put(context, Const.DOWN_LOAD_NUMBER_START_END_URL_FILE, "");
        //video 的url
        SPUtils.put(context, Const.DOWN_LOAD_VIDEO_URLS, urls);
        LogUtils.d(TAG, "========== downLoadVideo 开始下载video,推送url ========== \n" + urls);
        if (TextUtils.isEmpty(urls)) {
            LogUtils.d(TAG, "========== downLoadVideo url == 空,不下载文件 ========== ");
            return;
        }
        downLoad(context, urls);

    }

    private void downLoad(Context context, String urls) {

        String[] split = urls.split(";");
        if (split.length == 0) {
            LogUtils.d(TAG, "========== downLoad video url 分割失败,return ========== url = " + urls);
            return;
        }

        LogUtils.d(TAG, "========== downLoad 检查url所对应的文件是否已经存在");
        int index = -1;
        for (int i = 0; i < split.length; i++) {
            if (TextUtils.isEmpty(split[i])) {
                continue;
            }
            boolean fileExist = checkUrlAndFileExist(split[i], i);
            LogUtils.d(TAG, "========== downLoad fileExist = " + fileExist);
            if (!fileExist) {
                index = i;
                LogUtils.d(TAG, "========== downLoad 文件 不存在,角标 -> " + index + " file = " + split[i]);
                break;
            } else {
                LogUtils.d(TAG, "========== downLoad 文件 存在,角标 -> " + i);
                String filePath = (String) SPUtils.get(context, Const.DOWN_LOAD_NEW_VIDEO_PATH);
                if (TextUtils.isEmpty(filePath)) {
                    SPUtils.put(context, Const.DOWN_LOAD_NEW_VIDEO_PATH, Const.DIR_VIDEO + "/" + Md5.md5(split[i]));
                } else {
                    String msg = Const.DIR_VIDEO + "/" + Md5.md5(split[i]);
                    if (!filePath.contains(msg)) {
                        SPUtils.put(context, Const.DOWN_LOAD_NEW_VIDEO_PATH, filePath + ";" + Const.DIR_VIDEO + "/" + Md5.md5(split[i]));
                    }
                }
            }
        }
        if (index == -1) {
            LogUtils.d(TAG, "========== downLoad video 对应的文件都存在,return ========== ");
            Intent intent = new Intent(Const.DOWN_LOAD_VIDEO_RECEIVER);
            Bundle bundle = new Bundle();
            bundle.putString(Const.DOWN_LOAD_VIDEO_STATUE, Const.DOWN_LOAD_VIDEO_FINISH);
            intent.putExtras(bundle);
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);
            return;
        }

        //获取video的文件大小并开始下载
        getVideoFileLengthAndDownLoadFile(context, split[index], index);

    }

    private void getVideoFileLengthAndDownLoadFile(final Context context, final String url, final int index) {
        final String finalUrl = "http://" + AppSettingUtil.getConfig().getServerIp() + ":" + url;
        LogUtils.d(TAG, "========== getVideoFileLengthAndDownLoadFile 获取video的文件大小并准备开始下载 ========== " + finalUrl);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                //url需要拼接
                .url(finalUrl)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.d(TAG, "========== getVideoFileLengthAndDownLoadFile 获取Video的大小失败 - > " + e.getMessage() + " index = " + index);
                if (mRepeatCount <= 5) {
                    mRepeatCount++;
                    downLoadVideo(context);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body == null) {
                    LogUtils.e(TAG, "========== getVideoFileLengthAndDownLoadFile body == null ==========");
                    return;
                }
                mCallGetContentLength = call;
                Long downLoadEndByte = body.contentLength();
                String string = body.string();
                LogUtils.d(TAG, "========== getVideoFileLengthAndDownLoadFile 获取video的大小成功 , video 文件大小 -> " + downLoadEndByte);
                if (downLoadEndByte <= Const.VIDEO_MIN_SIZE) {
                    LogUtils.d(TAG, "========== getVideoFileLengthAndDownLoadFile 获取video 太小,跳过文件 -> " + finalUrl);
                    downLoadVideo(context);
                    return;
                }

                //播放循序号码_MD5_temp
                File newFile = createNewFile(Md5.md5(url), index);
                if (newFile == null) {
                    LogUtils.d(TAG, "========== getVideoFileLengthAndDownLoadFile video 文件创建失败,return ========== ");
                    return;
                }
                SPUtils.put(context, Const.DOWN_LOAD_NUMBER_START_END_URL_FILE, index + "|" + 0 + "|" + downLoadEndByte + "|" + finalUrl + "|" + newFile.getAbsolutePath());
                LogUtils.d(TAG, "========== getVideoFileLengthAndDownLoadFile video 文件正式下载 -> " + newFile.getAbsolutePath());
                downLoadCurrentVideo(context, finalUrl, newFile, 0, downLoadEndByte, index);
            }
        });
    }

    private void downLoadCurrentVideo(final Context context, final String url, final File file, final long downLoadStartByte, final long downLoadEndByte, final int index) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .addHeader("range", "bytes=" + downLoadStartByte + "-" + downLoadEndByte)
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.d(TAG, "========== downLoadCurrentVideo video 下载失败 = " + e.getMessage() + " index = " + index + " file = " + file.getName());
                if (mRepeatCount <= 5) {
                    mRepeatCount++;
                    downLoadVideo(context);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(context, call, response, url, file, index, downLoadStartByte, downLoadEndByte);
            }
        });
    }

    private void handleResponse(Context context, Call call, Response response, String url, File file, int index, long downLoadStartByte, long downLoadEndByte) {
        InputStream inputStream = null;
        mCallGetApkFile = call;
        try {
            Bundle bundleStart = new Bundle();
            bundleStart.putString(Const.DOWN_LOAD_VIDEO_STATUE, Const.DOWN_LOAD_START);
            Intent intent = new Intent(Const.DOWN_LOAD_VIDEO_RECEIVER);
            intent.putExtras(bundleStart);
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);

            inputStream = response.body().byteStream();
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(downLoadStartByte);
            int len;
            byte[] bytes = new byte[1024 * 4];
            int count = 0;
            while ((len = inputStream.read(bytes)) != -1 && !call.isCanceled()) {
                randomAccessFile.write(bytes, 0, len);
                downLoadStartByte += len;
                if (count % 100 == 0) {
                    SPUtils.put(context, Const.DOWN_LOAD_NUMBER_START_END_URL_FILE, index + "|" + downLoadStartByte + "|" + downLoadEndByte + "|" + url + "|" + file.getAbsolutePath());
                    mDownLoadApkSize = downLoadStartByte;
                }
                count++;
            }

            String newPath = file.getAbsolutePath().replaceAll("_temp", "");
            File newFile = new File(newPath);
            boolean b = file.renameTo(newFile);

            LogUtils.d(TAG, "========== handleResponse video 文件下载成功 ========= 重命名 name = " + newPath + " path = " + newPath);
            SPUtils.put(context, Const.DOWN_LOAD_NUMBER_START_END_URL_FILE, "");
            //添加新的路径
            String filePath = (String) SPUtils.get(context, Const.DOWN_LOAD_NEW_VIDEO_PATH);
            LogUtils.d(TAG, "========== handleResponse video new path -> " + filePath + " -> " + newPath);
            if (TextUtils.isEmpty(filePath)) {
                SPUtils.put(context, Const.DOWN_LOAD_NEW_VIDEO_PATH, newPath);
            } else {
                if (!filePath.contains(newPath)) {
                    SPUtils.put(context, Const.DOWN_LOAD_NEW_VIDEO_PATH, filePath + ";" + newPath);
                    LogUtils.d(TAG, "========== handleResponse video new path 2 " + (String) SPUtils.get(context, Const.DOWN_LOAD_NEW_VIDEO_PATH));
                }
            }
            LogUtils.d(TAG, "========== handleResponse video 文件下载成功 ========= name = " + newPath + " path = " + newPath);
            Thread.sleep(1000);
            downLoadVideo(context);
            Bundle bundle = new Bundle();
            bundle.putString(Const.DOWN_LOAD_VIDEO_STATUE, Const.DOWN_LOAD_VIDEO_END);
            intent = new Intent(Const.DOWN_LOAD_VIDEO_RECEIVER);
            intent.putExtras(bundle);
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);

        } catch (Exception e) {
            Log.d(TAG, "========== handleResponse video下载发生异常 -> " + e.getMessage());
            SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_NUMBER_START_END_URL_FILE, index + "|" + downLoadStartByte + "|" + downLoadEndByte + "|" + url + "|" + file.getAbsolutePath());
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
}
