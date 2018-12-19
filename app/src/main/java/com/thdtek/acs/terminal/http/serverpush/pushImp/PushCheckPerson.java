package com.thdtek.acs.terminal.http.serverpush.pushImp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.protobuf.ByteString;
import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.http.serverpush.PushBaseImp;
import com.thdtek.acs.terminal.util.BitmapUtil;

/**
 * Time:2018/12/13
 * User:lizhen
 * Description:
 */

public class PushCheckPerson extends PushBaseImp {
    @Override
    public Msg.Message onResponse(Msg.Message message, int seq) {


        Msg.Message.RsyncCheckPersonReq rsyncCheckPersonReq = message.getRsyncCheckPersonReq();
        long personId = rsyncCheckPersonReq.getPersonId();
        PersonBean personBean = PersonDao.query2PersonId(personId);
        Msg.Message.RsyncCheckPersonRsp rsyncCheckPersonRsp;
        if (personBean == null) {
            rsyncCheckPersonRsp = Msg.Message.RsyncCheckPersonRsp.newBuilder()
                    .build();
        } else {

            Bitmap bitmap = BitmapFactory.decodeFile(personBean.getFacePic());
            ByteString imageData = null;
            if (bitmap != null) {
                imageData = ByteString.copyFrom(BitmapUtil.bitmap2Byte(bitmap));
            }
            //人员信息
            Msg.Message.RsyncPersonRsp rsyncPersonRsp = null;
            Msg.Message.RsyncPersonRsp.Builder builder = Msg.Message.RsyncPersonRsp.newBuilder()
                    .setPersonId(personBean.getPerson_id())
                    .setEmployeeCardId(personBean.getEmployee_card_id())
                    .setPersonTs(personBean.getPerson_ts())
                    .setName(personBean.getName())
                    .setIDNo(personBean.getID_no());
            if (imageData == null) {
                rsyncPersonRsp = builder.build();
            } else {
                rsyncPersonRsp = builder.setFacePic(imageData)
                        .build();
            }
            Msg.Message.RsyncAuthRsp rsyncAuthRsp = Msg.Message.RsyncAuthRsp.newBuilder()
                    .setAuthId(personBean.getAuth_id())
                    .setAuthTs(personBean.getAuth_ts())
                    .setCount(personBean.getCount())
                    .setStartTs(personBean.getStart_ts())
                    .setEndTs(personBean.getEnd_ts())
                    .setWeekly(personBean.getWeekly())
                    .build();

            rsyncCheckPersonRsp = Msg.Message.RsyncCheckPersonRsp.newBuilder()
                    .setPerson(rsyncPersonRsp)
                    .setAuthority(rsyncAuthRsp)
                    .build();
        }

        return Msg.Message.newBuilder()
                .setRsyncCheckPersonRsp(rsyncCheckPersonRsp)
                .build();
    }
}
