package com.thdtek.acs.terminal.server;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.AppUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DeviceSnUtil;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SPUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;


public class HeartbeatThreadForHttp extends Thread {

    private static final String TAG = HeartbeatThreadForHttp.class.getSimpleName();

    public static boolean mLoop = true;

    @Override
    public void run() {
        super.run();
        LogUtils.d(TAG, "http心跳线程开启");
        while (mLoop) {

            sendHeartbeat();

            int period = (int) SPUtils.get(MyApplication.getContext(),
                    Const.PERIOD_FOR_HTTP_HEARTBEAT, 15);
            try {
                Thread.sleep(period * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        LogUtils.d(TAG, "http心跳线程结束");
    }

    private void sendHeartbeat() {
        String url = (String) SPUtils.get(MyApplication.getContext(),
                Const.URL_FOR_HTTP_HEARTBEAT);
        if (TextUtils.isEmpty(url)) {
            return;
        }

        FormBody build = null;
        try {
            build = new FormBody.Builder()
                    .addEncoded("deviceElapsedRealtime", URLEncoder.encode((SystemClock.elapsedRealtime() / 1000) + "", "utf-8"))
                    .addEncoded("deviceSystemVersion", URLEncoder.encode(android.os.Build.VERSION.RELEASE, "utf-8"))
                    .addEncoded("deviceSn", URLEncoder.encode(DeviceSnUtil.getDeviceSn(), "utf-8"))
                    .addEncoded("deviceAppVersion", URLEncoder.encode(AppUtil.getAppVersionName(MyApplication.getContext()), "utf-8"))
                    .addEncoded("deviceRegisterTime", URLEncoder.encode(AppSettingUtil.getConfig().getDeviceRegisterTime() + "", "utf-8"))
                    .addEncoded("deviceRomAvailableSize", URLEncoder.encode(AppSettingUtil.getRomSize()[0] + "MB", "utf-8"))
                    .addEncoded("deviceRomSize", URLEncoder.encode(AppSettingUtil.getRomSize()[1] + "MB", "utf-8"))
                    .addEncoded("deviceIpAddress", URLEncoder.encode(HWUtil.getIPAddress(), "utf-8"))
                    .build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            build = new FormBody.Builder()
                    .add("deviceElapsedRealtime", (SystemClock.elapsedRealtime() / 1000) + "")
                    .add("deviceSystemVersion", android.os.Build.VERSION.RELEASE)
                    .add("deviceSn", DeviceSnUtil.getDeviceSn())
                    .add("deviceAppVersion", AppUtil.getAppVersionName(MyApplication.getContext()))
                    .add("deviceRegisterTime", AppSettingUtil.getConfig().getDeviceRegisterTime() + "")
                    .add("deviceRomAvailableSize", AppSettingUtil.getRomSize()[0] + "MB")
                    .add("deviceRomSize", AppSettingUtil.getRomSize()[1] + "MB")
                    .add("deviceIpAddress", HWUtil.getIPAddress())
                    .build();
        }
        LogUtils.d(TAG, "发送http心跳 ... " + url);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .header("Content-Length", build.contentLength() + "")
                .post(build)//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.d(TAG, "http心跳 onFailure: ");
                sendBroadCast("", "", Const.VIEW_STATUS_OFF_LINE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtils.d(TAG, "http心跳 onResponse: " + response.body().string());
                sendBroadCast("", "", Const.VIEW_STATUS_ON_LINE);
            }
        });

    }


    //首页ui调整
    private void sendBroadCast(String weatherType, String weatherCode, int status) {
        Intent intent = new Intent();
        intent.setAction(Const.WEATHER);
        intent.putExtra(Const.WEATHER_TYPE, weatherType);
        intent.putExtra(Const.WEATHER_CODE, weatherCode);
        intent.putExtra(Const.DEVICE_ON_LINE, status);
        LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);

    }

    public void close() {
        mLoop = false;
    }
}
