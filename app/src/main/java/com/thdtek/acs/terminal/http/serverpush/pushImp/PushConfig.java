package com.thdtek.acs.terminal.http.serverpush.pushImp;

import android.util.Log;

import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.http.serverpush.PushBaseImp;
import com.thdtek.acs.terminal.socket.core.SendMsgHelper;
import com.thdtek.acs.terminal.util.AppSettingUtil;

/**
 * Time:2018/7/2
 * User:lizhen
 * Description:
 */

public class PushConfig extends PushBaseImp {

    private final String TAG = PushConfig.class.getSimpleName();

    public PushConfig() {
    }

    @Override
    public Msg.Message onResponse(Msg.Message message, int seq) {

        Log.e(TAG, message.toString());
        String errorMsg = "";
        AppSettingUtil.updateConfig(message);

        Msg.Message.SetConfigRsp setConfigRsp = Msg.Message.SetConfigRsp.newBuilder()
                .setStatus(0)
                .setErrorMsg(errorMsg)
                .build();
        Msg.Message msg = Msg.Message.newBuilder()
                .setSetConfigRsp(setConfigRsp)
                .build();
//        new SendMsgHelper().response(msg, seq);
        return msg;
    }
}
