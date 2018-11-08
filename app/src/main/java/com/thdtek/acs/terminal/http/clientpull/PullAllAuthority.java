package com.thdtek.acs.terminal.http.clientpull;

import android.util.LongSparseArray;

import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.dao.FaceFeatureDao;
import com.thdtek.acs.terminal.dao.NowPicFeatureDao;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.imp.person.persondownload.PersonDownLoadImp;
import com.thdtek.acs.terminal.socket.core.RequestCallback;
import com.thdtek.acs.terminal.socket.core.SendMsgHelper;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Time:2018/7/6
 * User:lizhen
 * Description:
 * 全量拉取权限表
 */

public class PullAllAuthority {
    private final String TAG = PullAllAuthority.class.getSimpleName();

    private static int count = 0;
    private LongSparseArray<Long> mLongSparseArray;

    public void getServerData() {
        final Msg.Message.RsyncDataListReq rsyncAuthListReq = Msg.Message.RsyncDataListReq.newBuilder()
                .setDataType(0)//拉取权限表
                .build();

        Msg.Message message = Msg.Message.newBuilder()
                .setRsyncDataListReq(rsyncAuthListReq)
                .build();
        new SendMsgHelper().request_custom_timeout(message, Const.SOCKET_WAIT_TIME, new RequestCallback() {
            @Override
            public void onResponse(Msg.Message message) {
                success(message);
            }

            @Override
            public void onTimeout() {
                LogUtils.e(TAG, "更新 权限, onTimeout ,return");
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
        List<PersonBean> list = new ArrayList<>();
        int size = listList.size();
        for (int i = 0; i < size; i++) {
            Msg.Message.IdTsPair idTsPair = listList.get(i);
            PersonBean personBean = new PersonBean();
            personBean.setAuth_id(idTsPair.getId());
            personBean.setAuth_ts(idTsPair.getTs());
            list.add(personBean);
        }
        query(list);
    }

    private void query(List<PersonBean> list) {
        mLongSparseArray = new LongSparseArray<>();
        List<Long> httpList = new ArrayList<>();
        int size = list.size();
        if (size == 0) {
            LogUtils.e(TAG, "更新权限,list 没有数据,return");
            PersonDao.getDao().deleteAll();
            NowPicFeatureDao.getDao().deleteAll();
            FaceFeatureDao.getDao().deleteAll();
            AppSettingUtil.deleteImageDir(new File(Const.DIR_IMAGE_EMPLOYEE));
            return;
        }
        List<PersonBean> personBeanList = PersonDao.queryAll();
        LogUtils.d(TAG, " ===== personBeanList.size = " + personBeanList.size());
        HashMap<Long, PersonBean> hashMap = new HashMap<>();
        int personSize = personBeanList.size();
        for (int i = 0; i < personSize; i++) {
            PersonBean personBean = personBeanList.get(i);
            hashMap.put(personBean.getAuth_id(), personBean);
        }
        int countAdd = 0;
        int countChange = 0;
        for (int i = 0; i < size; i++) {
            PersonBean personBean = list.get(i);
            PersonBean personMapBean = hashMap.remove(personBean.getAuth_id());
            if (personMapBean == null) {
                //数据库中没有这个数据,添加
                mLongSparseArray.put(personBean.getAuth_id(), Const.TYPE_INSERT);
                httpList.add(personBean.getAuth_id());
            } else {
                //有个数据,比对ts值是否相同
                double mapTs = personMapBean.getAuth_ts();
                double ts = personBean.getAuth_ts();
                if (mapTs == ts) {
                    //相同,数据没有改变,不拉取数据
                } else {
                    mLongSparseArray.put(personBean.getAuth_id(), Const.TYPE_UPDATE);
                    httpList.add(personBean.getAuth_id());
                }
            }
        }
        List<Long> deleteList = new ArrayList<>(hashMap.keySet());
        try {
            LogUtils.d(TAG, "deleteList size = " + deleteList.size());
            LogUtils.d(TAG, "deleteList = " + deleteList);

            for (int i = 0; i < deleteList.size(); i++) {
                PersonDao.deleteAuthority(deleteList.get(i));
            }
            LogUtils.d(TAG, "添加 size = " + countAdd);
            LogUtils.d(TAG, "添加 size = " + countChange);
            LogUtils.d(TAG, "httpList size = " + httpList.size() + " http = " + httpList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getAuthorityData(httpList);

    }

    private void getAuthorityData(final List<Long> list) {
        if (list.size() == 0) {
            PersonDao.reloadPersonList();
            LogUtils.e(TAG, "本次 权限 全量拉取结束,开始全量拉取Person");
            new PullAllPerson().getServerData();
            return;
        }
        final Long aLong = list.remove(0);
        PersonDownLoadImp.getInstance().personDownLoadStart(MyApplication.getContext().getString(R.string.down_load_authority) + aLong, Const.HANDLER_DELAY_TIME_10000);
        final Msg.Message.RsyncAuthReq rsyncAuthReq = Msg.Message.RsyncAuthReq.newBuilder()
                .setAuthId(aLong)
                .build();
        Msg.Message message = Msg.Message.newBuilder()
                .setRsyncAuthReq(rsyncAuthReq)
                .build();
        new SendMsgHelper().request_custom_timeout(message, Const.SOCKET_WAIT_TIME, new RequestCallback() {
            @Override
            public void onResponse(Msg.Message message) {
                Long type = mLongSparseArray.get(aLong);
                if (type == Const.TYPE_INSERT) {
                    insert(message);
                } else if (type == Const.TYPE_UPDATE) {
                    update(message);
                }

                getAuthorityData(list);
            }

            @Override
            public void onTimeout() {
                LogUtils.d(TAG, "authority id onTimeout = " + count);
                if (count <= 10) {
                    getAuthorityData(list);
                    count++;
                } else {
                    count = 0;
                    return;
                }
            }
        });
    }

    private void insert(Msg.Message message) {
        LogUtils.d(TAG, "收到权限 insert message = " + message.toString());
        PersonBean personBean = new PersonBean();
        Msg.Message.RsyncAuthRsp rsyncAuthRsp = message.getRsyncAuthRsp();

        personBean.setId(rsyncAuthRsp.getAuthId());
        personBean.setAuth_id(rsyncAuthRsp.getAuthId());
        personBean.setAuth_ts(rsyncAuthRsp.getAuthTs());
        personBean.setPerson_id(rsyncAuthRsp.getPersonId());
        personBean.setStart_ts(rsyncAuthRsp.getStartTs());
        personBean.setEnd_ts(rsyncAuthRsp.getEndTs());
        personBean.setCount(rsyncAuthRsp.getCount());
        if (rsyncAuthRsp.hasWeekly()) {
            //版本1.1.109 后增加的字段,需要兼容以前版本的后台
            personBean.setWeekly(rsyncAuthRsp.getWeekly());
        }
        PersonDao.insert(personBean);
    }

    private void update(Msg.Message message) {
        LogUtils.d(TAG, "收到权限 update message = " + message.toString());
        Msg.Message.RsyncAuthRsp rsyncAuthRsp = message.getRsyncAuthRsp();
        PersonBean personBean = PersonDao.query2AuthorityId(rsyncAuthRsp.getAuthId());
        if (personBean == null) {
            LogUtils.e(TAG, "数据库中不存在这个id,无法更新,return");
            return;
        }
        if (rsyncAuthRsp.hasAuthId()) {
            personBean.setId(rsyncAuthRsp.getAuthId());
            personBean.setAuth_ts(rsyncAuthRsp.getAuthId());
        }
        if (rsyncAuthRsp.hasAuthTs()) {
            personBean.setAuth_ts(rsyncAuthRsp.getAuthTs());
        }
        if (rsyncAuthRsp.hasPersonId()) {
            personBean.setPerson_id(rsyncAuthRsp.getPersonId());
        }
        if (rsyncAuthRsp.hasStartTs()) {
            personBean.setStart_ts(rsyncAuthRsp.getStartTs());

        }
        if (rsyncAuthRsp.hasEndTs()) {
            personBean.setEnd_ts(rsyncAuthRsp.getEndTs());

        }
        if (rsyncAuthRsp.hasCount()) {
            personBean.setCount(rsyncAuthRsp.getCount());
        }
        if (rsyncAuthRsp.hasWeekly()) {
            personBean.setWeekly(rsyncAuthRsp.getWeekly());
        }
        PersonDao.update(personBean);
    }
}
