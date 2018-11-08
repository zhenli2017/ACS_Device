package com.thdtek.acs.terminal.http.serverpush.pushImp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.http.serverpush.PushBaseImp;
import com.thdtek.acs.terminal.socket.core.SendMsgHelper;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SPUtils;

import java.io.IOException;
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

public class PushAd extends PushBaseImp {
    private static final String TAG = PushAd.class.getSimpleName();


    @Override
    public Msg.Message onResponse(Msg.Message message, int seq) {

        Msg.Message.NotifyADUpdateReq notifyAdUpdateReq = message.getNotifyAdUpdateReq();
        String url = notifyAdUpdateReq.getUrl();
        LogUtils.d(TAG, "收到广告推送 -> " + url);

        int widthPixels = MyApplication.getContext().getResources().getDisplayMetrics().widthPixels;
        if (widthPixels == Const.WINDOW_SIZE_WIDTH_1920) {
            LogUtils.d(TAG, "当前屏幕是13.3寸,处理广告");
            getAd(url);
        } else {
            LogUtils.d(TAG, "当前屏幕不是13.3寸,不处理广告");
        }

        Msg.Message.NotifyADUpdateRsp updateAPKRsp = Msg.Message.NotifyADUpdateRsp.newBuilder()
                .setStatus(0)
                .setErrorMsg("")
                .build();
        return Msg.Message.newBuilder()
                .setNotifyAdUpdateRsp(updateAPKRsp)
                .build();
    }


    public void getAd(String url) {
        String finalUrl = "http://" + AppSettingUtil.getConfig().getServerIp() + ":" + url;
        LogUtils.e(TAG, "========== 开始获取广告数据 ========== " + finalUrl);
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
                LogUtils.e(TAG, "========== 获取广告数据失败 ==========");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body == null) {
                    return;
                }
                LogUtils.e(TAG, "========== 获取广告数据成功 ==========");
                String string = body.string();
                Intent intent = new Intent(Const.DOWN_LOAD_VIDEO_RECEIVER);
                Bundle bundleStart = new Bundle();
                bundleStart.putString(Const.DOWN_LOAD_VIDEO_STATUE, Const.DOWN_LOAD_AD_IMAGE);
                bundleStart.putString(Const.DOWN_LOAD_AD_MESSAGE, string);
                intent.putExtras(bundleStart);
                LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);

            }
        });

    }

}
