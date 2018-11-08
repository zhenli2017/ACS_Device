package com.thdtek.acs.terminal.http.serverpush.pushImp;

import android.util.Log;

import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.base.ThreadPool;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.http.clientpull.PullPerson;
import com.thdtek.acs.terminal.http.clientpull.PullServerDatabase;
import com.thdtek.acs.terminal.http.serverpush.PushBaseImp;
import com.thdtek.acs.terminal.imp.person.persondownload.PersonDownLoadImp;
import com.thdtek.acs.terminal.socket.core.SendMsgHelper;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;

/**
 * Time:2018/7/4
 * User:lizhen
 * Description:
 */

public class PushRsynNotify extends PushBaseImp {

    private final String TAG = PullServerDatabase.class.getSimpleName();

    @Override
    public Msg.Message onResponse(Msg.Message message, int seq) {
        String[] success = success(message);

        Msg.Message.RsyncActionRsp rsyncActionRsp = Msg.Message.RsyncActionRsp.newBuilder()
                .setErrorMsg(success[1])
                .setStatus(Boolean.parseBoolean(success[0]) ? 0 : 1)
                .build();
        Msg.Message message1 = Msg.Message.newBuilder()
                .setRsyncActionRsp(rsyncActionRsp)
                .build();

        return message1;
    }

    public String[] success(Msg.Message message) {
        String[] data = new String[]{"true", ""};
        Msg.Message.RsyncActionReq rsyncActionReq = message.getRsyncActionReq();
        //删除权限id
        if (rsyncActionReq.hasDeleteAuth()) {
            LogUtils.d(TAG, "==== 删除权限");
            PersonDownLoadImp.getInstance().personDownLoadStart(MyApplication.getContext().getString(R.string.delete_person), Const.HANDLER_DELAY_TIME_3000);
            boolean b = PersonDao.deleteAuthority(rsyncActionReq.getDeleteAuth().getAuthId());
            data = parseMessage(b, "删除数据失败 = " + rsyncActionReq.getDeleteAuth().getAuthId());
        }
        //删除人id
        if (rsyncActionReq.hasDeletePerson()) {
            LogUtils.d(TAG, "==== 删除人");
            PersonDownLoadImp.getInstance().personDownLoadStart(MyApplication.getContext().getString(R.string.delete_person), Const.HANDLER_DELAY_TIME_3000);
            boolean b = PersonDao.deletePerson(rsyncActionReq.getDeletePerson().getPersonId());
            data = parseMessage(b, "删除数据失败 = " + rsyncActionReq.getDeletePerson().getPersonId());
        }
        if (rsyncActionReq.hasUpdateAuth()) {
            insertOrUpdateAuthority(rsyncActionReq.getUpdateAuth());
        }
        if (rsyncActionReq.hasUpdatePerson()) {
            data = insertOrUpdatePerson(rsyncActionReq.getUpdatePerson());
        }
        return data;
    }

    public void insertOrUpdateAuthority(Msg.Message.RsyncAuthRsp updateAuth) {
        long authId = updateAuth.getAuthId();
        PersonBean personBean = PersonDao.query2AuthorityId(authId);
        if (personBean == null) {
            //没有这个数据,添加
            insertAuthority(updateAuth);
        } else {
            //有这个数据,更新
            updateAuthority(updateAuth);
        }
    }

    public String[] insertOrUpdatePerson(Msg.Message.RsyncPersonRsp updatePerson) {
        long personId = updatePerson.getPersonId();
        PersonBean personBean = PersonDao.query2PersonId(personId);
        if (personBean == null) {
            //没有这个数据,添加
            return PersonDao.insertPerson(updatePerson);
        } else {
            //有这个数据,更新
            return PersonDao.updatePerson(updatePerson);
        }
    }


    private void insertAuthority(Msg.Message.RsyncAuthRsp rsyncAuthRsp) {
        LogUtils.d(TAG, "insertAuthority = " + rsyncAuthRsp.toString());
        PersonBean personBean = new PersonBean();
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
        new PullPerson().setData(rsyncAuthRsp.getPersonId(), Const.TYPE_INSERT);
    }

    private void updateAuthority(Msg.Message.RsyncAuthRsp rsyncAuthRsp) {
        PersonBean personBean = PersonDao.query2AuthorityId(rsyncAuthRsp.getAuthId());
        if (personBean == null) {
            LogUtils.e(TAG, "数据库中不存在这个id,无法更新,return");
            return;
        }
        LogUtils.d(TAG, "updateAuthority = " + rsyncAuthRsp.toString());
        if (rsyncAuthRsp.hasAuthId()) {
            personBean.setId(rsyncAuthRsp.getAuthId());
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
            //版本1.1.109 后增加的字段,需要兼容以前版本的后台
            personBean.setWeekly(rsyncAuthRsp.getWeekly());
        }
        PersonDao.update(personBean);
    }

    private String[] parseMessage(boolean b, String message) {
        String[] data = new String[]{"true", ""};
        if (b) {
            data[0] = "true";
        } else {
            data[0] = String.valueOf(false);
            data[1] = message;
        }
        return data;
    }


}
