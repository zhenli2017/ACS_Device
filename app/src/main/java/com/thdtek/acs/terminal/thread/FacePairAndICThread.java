package com.thdtek.acs.terminal.thread;

import android.os.SystemClock;

import com.thdtek.acs.terminal.bean.FaceFeatureHexBean;
import com.thdtek.acs.terminal.bean.ImageSaveBean;
import com.thdtek.acs.terminal.bean.NowPicFeatureHexBean;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.face.FacePairStatus;
import com.thdtek.acs.terminal.face.FaceTempData;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.WGUtil;
import com.thdtek.facelibrary.FaceApi;
import com.thdtek.facelibrary.FaceFeature;
import com.thdtek.facelibrary.FaceRect;

/**
 * Time:2018/9/19
 * User:lizhen
 * Description:
 */
@Deprecated
public class FacePairAndICThread extends BaseThread {
    @Override
    public String getTag() {
        return FacePairAndICThread.class.getSimpleName();
    }
//
//    private static final String TAG = FacePairAndICThread.class.getSimpleName();
//
//    @Override
//    public String getTag() {
//        return TAG;
//    }
//
//    @Override
//    public void handleData(Object faceApi, byte[] imageData, Object faceRect,String type) {
//        LogUtils.d(TAG, "=================================开始IC卡人脸比对===============================");
//        handlePairing();
//        PersonBean bean = PersonDao.query2ICCard(FaceTempData.getInstance().getIcMessage());
//        //没有找到这个人,识别失败
//        if (bean == null) {
//            SystemClock.sleep(1000);
//            handleFail(Const.OPEN_DOOR_TYPE_FACE_IC, "人脸+IC : IC 卡号不存在");
//            return;
//        }
//
//        //通过照的特征值
//        byte[] cameraFaceFeature = getFaceFeature(faceApi, imageData, faceRect, true);
//        if (cameraFaceFeature == null) {
//            handleFail(Const.OPEN_DOOR_TYPE_FACE_IC, "人脸+IC : 获取特征值失败");
//            return;
//        }
//
//        float facePair = facePair(faceApi, bean, cameraFaceFeature);
//        if (facePair != 2.0f && bean.getPerson_id() < Const.PERSON_TYPE_GUEST_DEFAULT_AUTHORITY_ID&&checkPersonAccess(bean)) {
//            //IC卡比对成功,并且人员的id不是访客的id
//            handleSuccess(bean, imageData, facePair, getRect(faceRect), Const.FACE_PAIR_NOT_SAME_PEOPLE, true);
//        } else {
//            handleFail(Const.OPEN_DOOR_TYPE_FACE_IC, "人脸+IC : 没有人高于指定阈值");
//        }
//    }
//
//    private float facePair(Object faceApi, PersonBean personBean, byte[] faceFeature) {
//        NowPicFeatureHexBean nowPicFeatureHexBean = mNowPicMap.get(personBean.getAuth_id());
//        float defaultPairNumber = 2.0f;
//        if (nowPicFeatureHexBean == null) {
//            LogUtils.d(TAG, "相机 : 获取最后一个人的特征值bean失败");
//            return defaultPairNumber;
//        }
//        byte[] oneByte = nowPicFeatureHexBean.getNowPicOneByte();
//        if (oneByte == null) {
//            LogUtils.d(TAG, "相机 : 获取最后一个人的第一个学习特征值失败");
//            return defaultPairNumber;
//        }
//        float oneFaceFeature = getPairNumber(faceApi, faceFeature, oneByte);
//        float twoFaceFeature = 0f;
//        float threeFaceFeature = 0f;
//
//        LogUtils.d(TAG, "相机 : 获取最后一个人的第一个学习特征值 = " + oneFaceFeature);
//        //第一次学习特征值比对
//        if (oneFaceFeature <= 1 && oneFaceFeature >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
//            byte[] twoByte = nowPicFeatureHexBean.getNowPicTwoByte();
//            if (twoByte == null) {
//                LogUtils.d(TAG, "相机 : 获取最后一个人的第二个学习特征值失败 ");
//                return oneFaceFeature;
//            } else {
//                twoFaceFeature = getPairNumber(faceApi, faceFeature, twoByte);
//                LogUtils.d(TAG, "相机 : 获取最后一个人的第二个学习特征值 = " + twoFaceFeature);
//                //第二次学习特征值比对
//                if (twoFaceFeature <= 1 && twoFaceFeature >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
//                    byte[] threeByte = nowPicFeatureHexBean.getNowPicThreeByte();
//                    if (threeByte == null) {
//                        LogUtils.d(TAG, "相机 : 获取最后一个人的第三个学习特征值特征值失败");
//                        return Math.max(oneFaceFeature, twoFaceFeature);
//                    } else {
//                        threeFaceFeature = getPairNumber(faceApi, faceFeature, threeByte);
//                        LogUtils.d(TAG, "相机 : 获取最后一个人的第三个学习特征值特征值 = " + threeFaceFeature);
//                        //第一次学习特征值比对
//                        if (threeFaceFeature <= 1 && threeFaceFeature >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
//                            return Math.max(threeFaceFeature, Math.max(oneFaceFeature, twoFaceFeature));
//                        }
//                    }
//                }
//            }
//        }
//
//        FaceFeatureHexBean faceFeatureHexBean = mFaceMap.get(personBean.getAuth_id());
//        if (faceFeatureHexBean == null) {
//            LogUtils.d(TAG, "相机 : 获取最后一个人的正装特征值失败 = ");
//            return defaultPairNumber;
//        }
//
//        float pairMatching = getPairNumber(faceApi, faceFeature, faceFeatureHexBean.getFaceFeatureByte());
//        LogUtils.d(TAG, "相机 : 获取最后一个人的正装特征值 = " + pairMatching);
//        if (pairMatching <= 1 && pairMatching >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
//            return pairMatching;
//        }
//        return defaultPairNumber;
//    }
}
