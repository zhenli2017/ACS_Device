package com.thdtek.acs.terminal.haogonge;

import android.os.SystemClock;

import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.base.ThreadPool;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SPUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VerifyByServer {

    private final static String TAG = VerifyByServer.class.getSimpleName();
    public static OkHttpClient mOkHttpClient;

    static {
        //okHttp
        mOkHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    public void verifyByServer(final String pin, final long passTime) {
        ThreadPool.getThread().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtils.i(TAG, "verifyByServer(attLog) ...");

                    String timeFormat = new TimeFormat().format(passTime);
                    String bodyForSign = pin + " " + timeFormat;

                    long time = new Date().getTime();
                    String sign = new Sign().getSignString(HaogongeThread.sn, time, bodyForSign, HaogongeThread.encryptKey);

                    String url = HaogongeThread.url_root;
                    url += "/verify";
                    url += "?sn=" + HaogongeThread.sn;
                    url += "&v=" + HaogongeThread.v;
                    url += "&time=" + time;
                    url += "&sign=" + sign;
                    LogUtils.d(TAG, "url=" + url);

                    FormBody formBody = new FormBody.Builder().build();

                    final Request request = new Request.Builder()
                            .url(url)
                            .post(formBody)
                            .build();
                    final Call call = mOkHttpClient.newCall(request);
                    Response response = call.execute();

                    if (response.isSuccessful()) {
                        String respStr = response.body().string();

                        LogUtils.i(TAG, "verifyByServer(attLog):" + respStr);

                        if ("open".equalsIgnoreCase(respStr)) {
                            openDoor(pin);
                        }
                    } else {
                        LogUtils.e(TAG, "服务器验证失败");
                        if (verifyByLocal()) {
                            LogUtils.d(TAG, "本地开门...");
                            openDoor(pin);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e(TAG, "服务器验证失败");
                    if (verifyByLocal()) {
                        LogUtils.d(TAG, "本地开门...");
                        openDoor(pin);
                    }
                }
            }
        });

    }

    private boolean verifyByLocal() {
        String offlineAction = (String) SPUtils.get(MyApplication.getContext(), Const.haogonge_offlineAction, "");
        if ("open".equals(offlineAction)) {
            LogUtils.d(TAG, "终端判断-允许开门");
            return true;
        }
        LogUtils.d(TAG, "终端判断-禁止开门");
        return false;
    }

    private void openDoor(String number) {
        HWUtil.openDoor(number);
        //延时关门
        if (AppSettingUtil.getConfig().getDoorType() == Const.WG_0
                || AppSettingUtil.getConfig().getDoorType() == Const.WG_26_0
                || AppSettingUtil.getConfig().getDoorType() == Const.WG_34_0
                || AppSettingUtil.getConfig().getDoorType() == Const.WG_66_0) {

            SystemClock.sleep(AppSettingUtil.getConfig().getOpenDoorContinueTime());
            HWUtil.closeDoor();
        }
    }
}
