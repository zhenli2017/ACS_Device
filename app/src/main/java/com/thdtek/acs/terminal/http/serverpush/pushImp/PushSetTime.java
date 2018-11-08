package com.thdtek.acs.terminal.http.serverpush.pushImp;

import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.http.serverpush.PushBaseImp;
import com.thdtek.acs.terminal.socket.core.SendMsgHelper;
import com.thdtek.acs.terminal.util.HWUtil;

/**
 * Time:2018/7/4
 * User:lizhen
 * Description:
 */

public class PushSetTime extends PushBaseImp {
    @Override
    public Msg.Message onResponse(Msg.Message message, int seq) {
        boolean success = HWUtil.setClientSystemTime(message.getSetTimeReq().getTs());

        Msg.Message.SetTimeRsp setTimeRsp = Msg.Message.SetTimeRsp.newBuilder()
                .setStatus(success ? 0 : 1)
                .setErrorMsg("时间格式化错误 = " + message.getSetTimeReq().getTs())
                .build();
        Msg.Message message1 = Msg.Message.newBuilder()
                .setSetTimeRsp(setTimeRsp)
                .build();

        return message1;
    }
}
