package com.thdtek.acs.terminal.http.serverpush.pushImp;

import android.os.SystemClock;

import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.base.ThreadPool;
import com.thdtek.acs.terminal.http.serverpush.PushBaseImp;
import com.thdtek.acs.terminal.socket.core.SendMsgHelper;
import com.thdtek.acs.terminal.util.HWUtil;

/**
 * Time:2018/7/12
 * User:lizhen
 * Description:
 * 推送重启机器
 */

public class PushRebootOrOpenAndCloseDoor extends PushBaseImp {

    @Override
    public Msg.Message onResponse(Msg.Message message, int seq) {
        Msg.Message.DeviceCtrlReq deviceCtrlReq = message.getDeviceCtrlReq();
        Msg.Message.DeviceCtrlReq.CtrlType ctrl = deviceCtrlReq.getCtrl();

        Msg.Message.DeviceCtrlRsp deviceCtrlRsp = Msg.Message.DeviceCtrlRsp.newBuilder()
                .setStatus(0)
                .setErrorMsg("")
                .build();
        Msg.Message message1 = Msg.Message.newBuilder()
                .setDeviceCtrlRsp(deviceCtrlRsp)
                .build();

        if (ctrl.getNumber() == 0) {
            rebootDelay();
        } else if (ctrl.getNumber() == 1) {
            HWUtil.openDoor("");
            SystemClock.sleep(1000);
            HWUtil.closeDoor();
        } else if (ctrl.getNumber() == 2) {
            HWUtil.closeDoor();
        }
        return message1;
    }

    //重启 机器
    private void rebootDelay(){
        ThreadPool.getThread().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                HWUtil.reboot("网络推送 或 http 模式下重启机器");
            }
        });
    }

}
