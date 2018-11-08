package com.thdtek.acs.terminal.http.serverpush.pushImp;

import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.http.serverpush.PushBaseImp;
import com.thdtek.acs.terminal.socket.core.SendMsgHelper;
import com.thdtek.acs.terminal.util.LogUtils;

/**
 * Time:2018/7/4
 * User:ygb
 * Description:
 */

public class PushQueryDataList extends PushBaseImp {
    private static final String TAG = PushQueryDataList.class.getSimpleName();

    @Override
    public Msg.Message onResponse(Msg.Message message, int seq) {

        LogUtils.d(TAG, "服务器向终端查询数据 RSYNC_DATA_LIST_REQ接口");
        Msg.Message.RsyncDataListReq req = message.getRsyncDataListReq();

        Msg.Message.RsyncDataListRsp.Builder rspBuilder = Msg.Message.RsyncDataListRsp.newBuilder();
        Msg.Message.RsyncDataListRsp rsp = null;
        if(req.hasDataType()){
            int dataType = req.getDataType();
            if(dataType == 0){// 0代表同步权限列表，1代表同步人员列表
                LogUtils.d(TAG, "服务器向终端请求并同步权限列表");
//                rspBuilder
//                        .setNeedUpdate()
//                        .set
            }else if(dataType == 1){// 0代表同步权限列表，1代表同步人员列表
                LogUtils.d(TAG, "服务器向终端请求并同步人员列表");
            }else{

            }
        }
        rsp = rspBuilder.build();

        Msg.Message message1 = Msg.Message.newBuilder()
                .setRsyncDataListRsp(rsp)
                .build();

//        new SendMsgHelper().response(message1, seq);
        return message1;
    }
}
