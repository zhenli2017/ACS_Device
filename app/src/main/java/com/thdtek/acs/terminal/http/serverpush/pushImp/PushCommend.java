package com.thdtek.acs.terminal.http.serverpush.pushImp;

import android.nfc.Tag;
import android.text.TextUtils;
import android.util.Log;

import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.http.serverpush.PushBaseImp;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;

/**
 * Time:2018/11/2
 * User:lizhen
 * Description:
 */

public class PushCommend extends PushBaseImp {
    public static final String TAG = PushCommend.class.getSimpleName();

    @Override
    public Msg.Message onResponse(Msg.Message message, int seq) {

        String cmd = message.getSendCmdReq().getCmd();
        if (TextUtils.isEmpty(cmd)) {
            LogUtils.d(TAG, "PushCommend cmd = null");
        } else if (cmd.startsWith("launchAPP")) {
            //格式 launchAPP:包名:activity名
            String[] split = cmd.split(":");
            if (split.length == 3) {
                HWUtil.launchApp(MyApplication.getContext(), split[1], split[2]);
            }
        }

        Msg.Message.SendCmdRsp sendCmdRsp = Msg.Message.SendCmdRsp.newBuilder()
                .setStatus(0)
                .setErrorMsg("")
                .build();
        return Msg.Message.newBuilder()
                .setSendCmdRsp(sendCmdRsp)
                .build();
    }
}
