package com.thdtek.acs.terminal.http.serverpush.pushImp;

import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.http.serverpush.PushBaseImp;
import com.thdtek.acs.terminal.socket.core.SendMsgHelper;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;

/**
 * Time:2018/7/4
 * User:ygb
 * Description:
 */

public class PushQueryPerson extends PushBaseImp {
    private static final String TAG = PushQueryPerson.class.getSimpleName();

    @Override
    public Msg.Message onResponse(Msg.Message message, int seq) {

        PersonBean personBean = null;
        if(message.hasRsyncPersonReq()){
            long personId = message.getRsyncPersonReq().getPersonId();
            personBean = PersonDao.query2PersonId(personId);
        }



        Msg.Message.RsyncPersonRsp.Builder builder = Msg.Message.RsyncPersonRsp.newBuilder();
        Msg.Message.RsyncPersonRsp rsyncPersonRsp = null;
        if(personBean == null){
            LogUtils.d(TAG, "personBean="+null);
            rsyncPersonRsp = builder.build();
        }else{
            LogUtils.d(TAG, "personBean="+personBean.toString());
            rsyncPersonRsp = builder
                    .setPersonId(personBean.getPerson_id())
                    .setPersonTs(personBean.getPerson_ts())
                    .build();
        }


        Msg.Message message1 = Msg.Message.newBuilder()
                .setRsyncPersonRsp(rsyncPersonRsp)
                .build();

        return message1;
    }
}
