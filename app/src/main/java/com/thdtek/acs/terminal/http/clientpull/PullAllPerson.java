package com.thdtek.acs.terminal.http.clientpull;

import android.util.LongSparseArray;

import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.socket.core.RequestCallback;
import com.thdtek.acs.terminal.socket.core.SendMsgHelper;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Time:2018/7/6
 * User:lizhen
 * Description:
 * 全量拉取person表
 */

public class PullAllPerson {
    private final String TAG = PullAllPerson.class.getSimpleName();

    public void getServerData() {
        Msg.Message.RsyncDataListReq rsyncDataListReq = Msg.Message.RsyncDataListReq.newBuilder()
                .setDataType(1)//拉取person表
                .build();
        Msg.Message message = Msg.Message.newBuilder()
                .setRsyncDataListReq(rsyncDataListReq)
                .build();

        new SendMsgHelper().request_custom_timeout(message, Const.SOCKET_WAIT_TIME, new RequestCallback() {
            @Override
            public void onResponse(Msg.Message message) {
                success(message);
            }

            @Override
            public void onTimeout() {
                LogUtils.e(TAG, "全量更新Person列表, onTimeout ,return");
            }
        });
    }

    private void success(Msg.Message message) {
        Msg.Message.RsyncDataListRsp rsyncAuthListRsp = message.getRsyncDataListRsp();
        if (rsyncAuthListRsp.hasNeedUpdate() && !rsyncAuthListRsp.getNeedUpdate()) {
            LogUtils.d(TAG, "rsyncAuthListRsp.getNeedUpdate() = false ,不更新数据");
            return;
        }
        List<Msg.Message.IdTsPair> listList = rsyncAuthListRsp.getListList();
        LogUtils.d(TAG, "PullAllPerson list size = " + listList.size());
        List<PersonBean> list = new ArrayList<>();
        int size = listList.size();
        for (int i = 0; i < size; i++) {
            Msg.Message.IdTsPair idTsPair = listList.get(i);
            PersonBean personBean = new PersonBean();
            personBean.setPerson_id(idTsPair.getId());
            personBean.setPerson_ts(idTsPair.getTs());
            list.add(personBean);
        }
        query(list);
    }

    private void query(List<PersonBean> list) {

        LongSparseArray<Long> longLongSparseArray = new LongSparseArray<>();
        List<Long> httpList = new ArrayList<>();
        List<Long> deleteList = new ArrayList<>();
        int size = list.size();
        if (size == 0) {
            LogUtils.e(TAG, "更新Person,list 没有数据,return");
        }
        int countAdd = 0;
        int countChange = 0;
        List<PersonBean> personBeanList = PersonDao.queryAll();
        HashMap<Long, PersonBean> hashMap = new HashMap<>();
        int personSize = personBeanList.size();
        for (int i = 0; i < personSize; i++) {
            PersonBean personBean = personBeanList.get(i);
            hashMap.put(personBean.getPerson_id(), personBean);
        }
        for (int i = 0; i < size; i++) {
            PersonBean personBean = list.get(i);
            PersonBean personMapBean = hashMap.remove(personBean.getPerson_id());

            if (personMapBean == null) {
                //数据库中没有这个数据,添加
                longLongSparseArray.put(personBean.getPerson_id(), Const.TYPE_INSERT);
                countAdd++;
                httpList.add(personBean.getPerson_id());
            } else {

                //有个数据,比对ts值是否相同
                double mapTs = personMapBean.getPerson_ts();
                double ts = personBean.getPerson_ts();
                if (mapTs == ts) {
                    //相同,数据没有改变,不拉取数据
                } else {
                    longLongSparseArray.put(personBean.getPerson_id(), Const.TYPE_UPDATE);
                    httpList.add(personBean.getPerson_id());
                    countChange++;
                }
            }
        }
        Iterator<Long> iterator = hashMap.keySet().iterator();
        while (iterator.hasNext()) {
            deleteList.add(hashMap.get(iterator.next()).getAuth_id());
        }
        try {
            LogUtils.d(TAG, "deleteList size = " + deleteList.size());
            LogUtils.d(TAG, "deleteList = " + deleteList);
            for (int i = 0; i < deleteList.size(); i++) {
                PersonDao.deletePerson(deleteList.get(i));
            }
            LogUtils.d(TAG, "添加 size = " + countAdd);
            LogUtils.d(TAG, "修改 size = " + countChange);
            LogUtils.d(TAG, "map = " + longLongSparseArray);
            LogUtils.d(TAG, "httpList = " + httpList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new PullPerson().setData(httpList, longLongSparseArray);
    }

}
