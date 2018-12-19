package com.thdtek.acs.terminal.face;

import android.graphics.Rect;

import com.thdtek.acs.terminal.base.ThreadPool;
import com.thdtek.acs.terminal.bean.AccessRecordBean;
import com.thdtek.acs.terminal.bean.FacePairBean;
import com.thdtek.acs.terminal.bean.PairSuccessOtherBean;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.dao.AccessRecordDao;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.imp.camera.CameraPreviewFindFaceInterface;
import com.thdtek.acs.terminal.thread.FacePairThread9;
import com.thdtek.acs.terminal.thread.ThreadManager;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.camera.CameraUtil;
import com.thdtek.acs.terminal.util.tts.TtsUtil;

/**
 * Time:2018/9/18
 * User:lizhen
 * Description:
 */

public class FacePairStatus {

    private static final String TAG = FacePairStatus.class.getSimpleName();

    private CameraPreviewFindFaceInterface mFindFaceInterface;
    /**
     * 重复失败的次数
     */
    private int mFacePairFailCurrentCount = 1;
    /**
     * 上一次识别成功人权限id
     */
    private long mLastFacePairSuccessAuthorityId = Const.DEFAULT_CONITUE_AUTHORITY_ID;

    private static FacePairStatus mFacePairStatus = new FacePairStatus();

    public static FacePairStatus getInstance() {
        return mFacePairStatus;
    }

    public void init(CameraPreviewFindFaceInterface findFaceInterface) {
        mFindFaceInterface = findFaceInterface;
    }


    /**
     * 正在匹配
     */
    public synchronized void pairIng() {
        if (mFindFaceInterface != null) {
            mFindFaceInterface.facePairing();
        }
    }

    /**
     * 匹配成功
     */
    public synchronized void pairSuccess(final PersonBean personBean,
                                         final PairSuccessOtherBean pairSuccessOtherBean,
                                         final byte[] image,
                                         final float rate,
                                         final Rect rect,
                                         int insert,
                                         final boolean cameraData) {
        FacePairBean facePairBean = new FacePairBean();
        long accessTime = System.currentTimeMillis();

        pairSuccessOtherBean.setAccessTime(accessTime);

        facePairBean.setAccessTime(accessTime);
        facePairBean.setPersonBean(personBean);
        if (mFindFaceInterface != null) {
            mFindFaceInterface.facePairSuccess(facePairBean, insert);
        }
        CameraUtil.PAIR_FACE_SUCCESS_COLOR = true;
        setLastFacePairSuccessAuthorityId(personBean.getAuth_id());

        if (insert == Const.FACE_PAIR_NOT_SAME_PEOPLE) {
            ThreadPool.getThread().execute(new Runnable() {
                @Override
                public void run() {
                    //保存一个临时文件
                    if (personBean.getCount() < 10000) {
                        personBean.setCount(personBean.getCount() - 1);
                        PersonDao.update(personBean);
                    }
                    AccessRecordDao.insert(personBean, pairSuccessOtherBean, image, rate, rect, cameraData);
                }
            });
        }
    }

    /**
     * 匹配失败
     */
    public synchronized void pairFail(int type, String msg, int code) {
        if (type == Const.OPEN_DOOR_TYPE_FACE_IC || type == Const.OPEN_DOOR_TYPE_FACE_ID) {
            CameraUtil.PAIR_FACE_SUCCESS_COLOR = false;
            TtsUtil.getInstance().stop();
            TtsUtil.getInstance().speak(AppSettingUtil.getConfig().getAppFailMsg());
            if (mFindFaceInterface != null) {
                mFindFaceInterface.facePairFail(msg,code);
            }
        } else {

            if (mFacePairFailCurrentCount % Const.FACE_PAIR_FAIL_REPEAT_COUNT == 0) {
                CameraUtil.PAIR_FACE_SUCCESS_COLOR = false;
                TtsUtil.getInstance().stop();
                TtsUtil.getInstance().speak(AppSettingUtil.getConfig().getAppFailMsg());

                if (mFindFaceInterface != null) {

                    mFindFaceInterface.facePairFail(msg,code);
                }
            } else {
                if (mFindFaceInterface != null) {
                    LogUtils.d(TAG, "失败次数 = " + mFacePairFailCurrentCount);
                    mFindFaceInterface.facePairFailNoVisible();
                }
            }
            mFacePairFailCurrentCount++;
        }
    }

    public synchronized void pairDoNotThing() {
        LogUtils.d(TAG, "========== 当前匹配结束,什么也不干,打开获取人脸数据 ==========");
        if (mFindFaceInterface != null) {
            mFindFaceInterface.facePairFailNoVisible();
        }
    }

    public synchronized void pairNotAlive(String msg) {
        setLastFacePairSuccessAuthorityId(Const.DEFAULT_CONITUE_AUTHORITY_ID);
        if (mFindFaceInterface != null) {
            mFindFaceInterface.faceNotAlive(msg);
        }
    }

    /**
     * 匹配结束
     */
    public synchronized void pairFinish() {
        if (mFindFaceInterface != null) {
            mFindFaceInterface.facePairFinish();
        }
    }

    public synchronized void pairContinueOnce() {
        LogUtils.d(TAG, "========== 本次匹配跳过 ==========");
    }

    public synchronized void noFindFace() {

    }

    /**
     * IC卡没有这个人
     */
    public synchronized void noIcCardPeople() {

    }

    /**
     * 人脸比对的线程放弃一次,等待下一次比对
     */
    public void facePairThreadContinueOnce() {
        ThreadManager.getThread(FacePairThread9.class.getSimpleName()).continueOnce();
//        ThreadManager.getThread(FacePairThread10.class.getSimpleName()).continueOnce();
    }

    public synchronized void resetFailCount() {
        mFacePairFailCurrentCount = 1;
    }

    public synchronized long getLastFacePairSuccessAuthorityId() {
        return mLastFacePairSuccessAuthorityId;
    }

    public synchronized void setLastFacePairSuccessAuthorityId(long lastFacePairSuccessAuthorityId) {
        mLastFacePairSuccessAuthorityId = lastFacePairSuccessAuthorityId;
    }

    public void unInit() {
        if (mFindFaceInterface != null) {
            mFindFaceInterface = null;
        }
    }
}
