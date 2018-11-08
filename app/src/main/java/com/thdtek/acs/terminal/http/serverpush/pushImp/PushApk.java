package com.thdtek.acs.terminal.http.serverpush.pushImp;

import android.text.TextUtils;

import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.http.serverpush.PushBaseImp;
import com.thdtek.acs.terminal.socket.core.SendMsgHelper;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.AppUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DownloadApk;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SPUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Time:2018/8/30
 * User:lizhen
 * Description:
 */

public class PushApk extends PushBaseImp {
    private static final String TAG = PushApk.class.getSimpleName();

    //apk命名方式 terminal_versionCode_versionName.apk
    //terminal_20_1.1.20.apk
    @Override
    public Msg.Message onResponse(Msg.Message message, int seq) {

        LogUtils.d(TAG, "============= 收到APP升级推送 =============");
        Msg.Message.UpdateAPKReq updateApkReq = message.getUpdateApkReq();
        String apkUrl = updateApkReq.getApkUrl();
        Pattern pattern = Pattern.compile("terminal_.+");
        Matcher matcher = pattern.matcher(apkUrl);


        boolean b = matcher.find();
        if (!b) {
            LogUtils.d(TAG, "apk命名格式错误,terminal_versionCode_versionName.apk");
            return sendRsp(1, "apk命名格式错误,terminal_versionCode_versionName.apk", seq);
        }
        String group = matcher.group();
        if (TextUtils.isEmpty(group)) {
            LogUtils.d(TAG, "apk命名格式错误,terminal_versionCode_versionName.apk");
            return sendRsp(1, "apk命名格式错误,terminal_versionCode_versionName.apk", seq);
        }
        String[] terminals = apkUrl.split("_");
        if (terminals.length < 3) {
            LogUtils.d(TAG, "apk命名格式错误,terminal_versionCode_versionName.apk");
            return sendRsp(1, "apk命名格式错误,terminal_versionCode_versionName.apk", seq);
        }
        LogUtils.d(TAG, "====== apk开始准备下载 ======");
        try {
            int code = Integer.parseInt(terminals[1]);

            int appVersionCode = AppUtil.getAppVersionCode(MyApplication.getContext());
            if (code > appVersionCode) {
                apkUrl = "http://" + AppSettingUtil.getConfig().getServerIp() + ":" + apkUrl;
                LogUtils.d(TAG, "apkURL = " + apkUrl);
                SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_APK_URL, apkUrl);
                SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_APK_FILE_VERSION_CODE, code);
                Msg.Message.UpdateAPKReq.UpdateFlag flag = updateApkReq.getFlag();
                SPUtils.put(MyApplication.getContext(), Const.DOWN_LOAD_APK_UPDATE_NOW, flag.getNumber() == 0);
                DownloadApk.getInstance().downLoad(MyApplication.getContext(),
                        apkUrl, code, true, true);
                LogUtils.d(TAG, "推送成功,准备升级");
                return sendRsp(0, "推送成功,准备升级", seq);
            } else {
                LogUtils.d(TAG, "版本小于或等于当前版本,不升级");
                return sendRsp(1, "版本小于或等于当前版本,不升级", seq);
            }
        } catch (Exception e) {
            LogUtils.d(TAG, "apk升级发生异常 = " + e.getMessage());
            return sendRsp(1, "apk命名格式错误 " + e.getMessage(), seq);
        }

    }

    private Msg.Message sendRsp(int state, String msg, int seq) {
        Msg.Message.UpdateAPKRsp updateAPKRsp = Msg.Message.UpdateAPKRsp.newBuilder()
                .setStatus(state)
                .setErrorMsg(msg)
                .build();
        Msg.Message message1 = Msg.Message.newBuilder()
                .setUpdateApkRsp(updateAPKRsp)
                .build();

//        new SendMsgHelper().response(message1, seq);
        return message1;
    }
}
