package com.thdtek.acs.terminal.thread;

import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.thdtek.acs.terminal.bean.FacePairEvent;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.face.FacePairStatus;
import com.thdtek.acs.terminal.face.FaceTempData;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.camera.CameraUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Time:2018/10/31
 * User:lizhen
 * Description:
 */

public class FacePairFinishThread extends Thread {

    private static final String TAG = FacePairFinishThread.class.getSimpleName();
    private static final int MAX_EVENT_SIZE = 10;
    private static final int HANDLER_MESSAGE = 1;
    private boolean mLoop = true;

    //所有的事件集合
    private List<FacePairEvent> mAllFacePairList = new ArrayList<>();
    //事件集合中所有的人员权限id集合
    private List<String> mAuthorityNumberList = new ArrayList<>();


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            LogUtils.d(TAG, "====== 时间超时,清空存在的权限 =====");
            mAllFacePairList.clear();
            mAuthorityNumberList.clear();
            CameraUtil.resetCameraVariable(true);
            return false;
        }
    });

    public void close() {
        mLoop = false;
    }

    @Override
    public void run() {
        super.run();
        while (mLoop) {
            try {
                LogUtils.e(TAG, "====== FacePairFinishThread 收到事件 ====== ");
                handlePair(mAllFacePairList, mAuthorityNumberList);
            } catch (InterruptedException e) {
                e.printStackTrace();
                LogUtils.e(TAG, "====== FacePairFinishThread error = " + e.getMessage());
            }
        }
        LogUtils.e(TAG, "====== " + TAG + " 完结撒花,特征值比对线程关闭 ====== ");
    }

    private void handlePair(List<FacePairEvent> allFacePairList, List<String> authorityNumberList) throws InterruptedException {
        FacePairEvent take = FaceTempData.getInstance().mQueue.take();
        if (Const.PAIR_TYPE_FACE.equals(take.getType())) {
            LogUtils.d(TAG, "====== 当前事件是 : 人脸 识别成功");
        } else if (Const.PAIR_TYPE_IC.equals(take.getType())) {
            LogUtils.d(TAG, "====== 当前事件是 : IC卡 识别成功");
        } else if (Const.PAIR_TYPE_ID.equals(take.getType())) {
            LogUtils.d(TAG, "====== 当前事件是 : ID卡 识别成功");
        }

        if (allFacePairList.size() >= MAX_EVENT_SIZE) {
            LogUtils.e(TAG, "====== 事件列表中超过了最大的事件长度要求 = " + MAX_EVENT_SIZE);
            authorityNumberList.clear();
            allFacePairList.clear();
        }

        if (authorityNumberList.size() != 0 && !authorityNumberList.contains(take.getPersonBean().getAuth_id() + "")) {
            LogUtils.e(TAG, "====== 已经存在以前的数据,同时当前的人和以前的人不一样,删除以前的数据,用新的人");
            authorityNumberList.clear();
            allFacePairList.clear();
        }

        //添加权限到列表中
        allFacePairList.add(take);

        ArrayList<String> authorityList = new ArrayList<>();
        for (int i = 0; i < allFacePairList.size(); i++) {
            authorityList.add(allFacePairList.get(i).getType());
            authorityNumberList.add(allFacePairList.get(i).getPersonBean().getAuth_id() + "");
        }
        LogUtils.d(TAG, "====== 所有事件集合 = " + allFacePairList);
        LogUtils.d(TAG, "====== 所有事件集合存在的类型 = " + authorityList);
        LogUtils.d(TAG, "====== 所有事件集合中的权限id = " + mAuthorityNumberList);

        PersonBean personBean = take.getPersonBean();
        String authority = personBean.getPersonalizedPermissions();

        if (TextUtils.isEmpty(authority)) {
            LogUtils.d(TAG, "====== 没有个性化权限,使用全局权限");
            if (AppSettingUtil.getConfig().getOpenDoorType() == Const.OPEN_DOOR_TYPE_FACE) {
                authority = Const.PAIR_TYPE_FACE;
                LogUtils.d(TAG, "====== 全局开门方式是 人脸 authority = " + authority);
            } else if (AppSettingUtil.getConfig().getOpenDoorType() == Const.OPEN_DOOR_TYPE_I_C) {
                authority = Const.PAIR_TYPE_FACE;
                LogUtils.d(TAG, "====== 全局开门方式是 人脸 || IC 卡 authority = " + authority);
            } else if (AppSettingUtil.getConfig().getOpenDoorType() == Const.OPEN_DOOR_TYPE_FACE_IC) {
                authority = Const.PAIR_TYPE_FACE + "+" + Const.PAIR_TYPE_IC;
                LogUtils.d(TAG, "====== 全局开门方式是 人脸 + IC 卡 authority = " + authority);
            } else if (AppSettingUtil.getConfig().getOpenDoorType() == Const.OPEN_DOOR_TYPE_FACE_ID) {
                authority = Const.PAIR_TYPE_FACE + "+" + Const.PAIR_TYPE_ID;
                LogUtils.d(TAG, "====== 全局开门方式是 人脸 + ID 卡 authority = " + authority);
            }
            if (personBean.getPerson_id() >= Const.PERSON_TYPE_GUEST_DEFAULT_AUTHORITY_ID) {
                LogUtils.d(TAG, "====== 当前人员是访客 = " + personBean.getName() + " personId = " + personBean.getPerson_id());
            } else {
                LogUtils.d(TAG, "====== 当前人员是员工 = " + personBean.getName() + " personId = " + personBean.getPerson_id());
            }
        }

        LogUtils.e(TAG, "====== 开始分割Person权限 分隔符 - , 权限 = " + authority + " personBean = " + personBean.toString());
        String[] split = authority.split("-");
        List<List<String>> personAuthorityList = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            List<String> subList = new ArrayList<>();
            String subAuthority = split[i];
            if (subAuthority.contains("+")) {
                LogUtils.d(TAG, "====== 这个权限有子权限,需要继续分割 i = " + i);
                String[] subsubAuthority = subAuthority.split("\\+");
                subList.addAll(Arrays.asList(subsubAuthority));
            } else {
                LogUtils.d(TAG, "====== 这个权限没有子权限,不需要继续分割 i = " + i);
                subList.add(split[i]);
            }
            LogUtils.d(TAG, "====== i = " + i + " 权限列表 = " + subList);
            personAuthorityList.add(subList);
        }
        LogUtils.e(TAG, "====== 所有权限分割后 = " + personAuthorityList);
        boolean allSuccess = false;
        for (int i = 0; i < personAuthorityList.size(); i++) {

            LogUtils.d(TAG, "====== 匹配权限 或 关系(最外层)");
            List<String> list = personAuthorityList.get(i);
            LogUtils.d(TAG, "====== 或(外) i = " + i + " list = " + list);
            boolean subSuccess = false;

            for (int k = 0; k < list.size(); k++) {
                String type = list.get(k);
                LogUtils.d(TAG, "====== 与(内) k = " + k + " type = " + type);
                if (authorityList.contains(type)) {
                    subSuccess = true;
                    LogUtils.d(TAG, "====== 包含  对应的权限 = " + type);
                } else {
                    subSuccess = false;
                    LogUtils.d(TAG, "====== 不包含 对应的权限,失败 = " + type);
                    break;
                }
            }
            if (subSuccess) {
                allSuccess = true;
                LogUtils.i(TAG, "====== 一个大条件 已经 符合 权限,识别成功");
                break;
            } else {
                allSuccess = false;
                LogUtils.i(TAG, "====== 一个大条件 不 符合权限,识别失败,识别下一个大条件");
            }
        }
        if (!allSuccess) {
            LogUtils.e(TAG, "====== 不具备识别成功的权限,继续等待," + Const.HANDLER_DELAY_TIME_5000 + "毫秒");
            mHandler.removeMessages(HANDLER_MESSAGE);
            mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE, Const.HANDLER_DELAY_TIME_3000);
            return;
        }

        FacePairEvent facePairEvent = null;
        for (int i = 0; i < allFacePairList.size(); i++) {
            if (allFacePairList.get(i).getType().equals(Const.PAIR_TYPE_FACE)) {
                facePairEvent = allFacePairList.get(i);
                break;
            }
        }
        if (facePairEvent == null) {
            LogUtils.e(TAG, "====== 具备识别成功的权限,识别成功,但是没有人脸的数据,return");
            return;
        }
        mHandler.removeMessages(HANDLER_MESSAGE);
        FacePairStatus.getInstance().pairSuccess(personBean,
                facePairEvent.getImage(),
                facePairEvent.getRate(),
                facePairEvent.getFaceRect(),
                facePairEvent.getSamePerson(),
                facePairEvent.isCameraData());
        LogUtils.e(TAG, "====== 具备识别成功的权限,识别成功,放行");
        allFacePairList.clear();
        authorityNumberList.clear();
    }
}
