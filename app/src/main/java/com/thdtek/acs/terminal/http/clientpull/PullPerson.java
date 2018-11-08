package com.thdtek.acs.terminal.http.clientpull;

import android.util.LongSparseArray;

import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.imp.person.persondownload.PersonDownLoadImp;
import com.thdtek.acs.terminal.socket.core.RequestCallback;
import com.thdtek.acs.terminal.socket.core.SendMsgHelper;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Time:2018/7/9
 * User:lizhen
 * Description:
 */

public class PullPerson {
    private final String TAG = PullPerson.class.getSimpleName();
    private LongSparseArray<Long> mLongSparseArray;
    private static int count = 0;
    private int mCount = 0;
    public void setData(final List<Long> list, LongSparseArray<Long> longSparseArray) {
        mLongSparseArray = longSparseArray;
        LogUtils.d(TAG, "========== list = " + list.size());
        mCount = list.size();
        getPersonData(list);
    }

    public void setData(final long personId, Long type) {

        List<Long> list = new ArrayList<>();
        list.add(personId);
        mLongSparseArray = new LongSparseArray<>();
        mLongSparseArray.put(personId, type);
        mCount = list.size();
        getPersonData(list);
    }



    private void getPersonData(final List<Long> list) {
        if (list.size() == 0) {
            PersonDao.reloadPersonList();
            LogUtils.e(TAG, "本次Person拉取结束,return");

            return;
        }
        LogUtils.d(TAG, "========== getPersonData list = " + list.size());

        final Long aLong = list.remove(0);
        final Msg.Message.RsyncPersonReq rsyncPersonReq = Msg.Message.RsyncPersonReq.newBuilder()
                .setPersonId(aLong)
                .build();
        Msg.Message message = Msg.Message.newBuilder()
                .setRsyncPersonReq(rsyncPersonReq)
                .build();
        new SendMsgHelper().request_custom_timeout(message, Const.SOCKET_WAIT_TIME, new RequestCallback() {
            @Override
            public void onResponse(Msg.Message message) {
                Msg.Message.RsyncPersonRsp rsyncPersonRsp = message.getRsyncPersonRsp();
                Long type = mLongSparseArray.get(aLong);
                if (type == Const.TYPE_INSERT) {
                    LogUtils.d(TAG, "========== person insert");
                    PersonDao.insertPerson(rsyncPersonRsp);
                } else if (type == Const.TYPE_UPDATE) {
                    LogUtils.d(TAG, "========== person update");
                    PersonDao.updatePerson(rsyncPersonRsp);
                }
                getPersonData(list);
            }

            @Override
            public void onTimeout() {
                LogUtils.d(TAG, "time out ");
                if (count <= 10) {
                    getPersonData(list);
                    count++;
                } else {
                    count = 0;
                    return;
                }
            }
        });
    }
}
