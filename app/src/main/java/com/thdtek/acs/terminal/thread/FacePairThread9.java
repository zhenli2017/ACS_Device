package com.thdtek.acs.terminal.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.SystemClock;

import com.thdtek.acs.terminal.bean.FaceAttribute;
import com.thdtek.acs.terminal.bean.FaceFeatureHexBean;
import com.thdtek.acs.terminal.bean.IDBean;
import com.thdtek.acs.terminal.bean.NoAliveEvent;
import com.thdtek.acs.terminal.bean.NowPicFeatureHexBean;
import com.thdtek.acs.terminal.bean.PairBean;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.dao.FaceFeatureDao;
import com.thdtek.acs.terminal.dao.NowPicFeatureDao;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.face.FacePairStatus;
import com.thdtek.acs.terminal.face.FaceTempData;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.BitmapUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * Time:2018/6/21
 * User:lizhen
 * Description:
 */
public class FacePairThread9 extends BaseThread {

    private static final String TAG = FacePairThread9.class.getSimpleName();

    @Override
    public void init(boolean initDataBase) {
        super.init(initDataBase);
    }

    @Override
    public String getTag() {
        return TAG;
    }


    @Override
    public void handleData(Object faceApi, byte[] imageData, Object faceRect, String type) {
        LogUtils.d(TAG, "====== 准备开始处理数据,类型是 :" + type + " ======");
        if (Const.SDK_YUN_TIAN_LI_FEI.equals(Const.SDK)) {
            Object tempFaceRect = getFaceRect(faceApi, imageData, true);
            if (tempFaceRect == null) {
                LogUtils.d(TAG, "匹配前再次寻找人脸没有找到,return");
                handleContinueOnce();
                return;
            }
        }
        if (Const.SDK_YUN_TIAN_LI_FEI.equals(Const.SDK)) {
            FaceAttribute faceAttribute = getFaceAttribute(faceApi, imageData, faceRect, Const.IFACEREC_QUALITY_MASK, true);
            if (faceAttribute == null) {
                handleContinueOnce();
                return;
            }
            for (int i = 0; i < faceAttribute.faceRecAttrResult.length; i++) {
                if (faceAttribute.faceRecAttrResult[i].fConfidence <= 0.72) {
                    LogUtils.d(TAG, "图片质量不高 -> " + faceAttribute.faceRecAttrResult[i].fConfidence + " 当前设置质量 -> " + 0.72);
                    handleContinueOnce();
                    return;
                }
            }
        }
        //判断是否是活体
        if (Const.SDK_YUN_TIAN_LI_FEI.equals(Const.SDK) && AppSettingUtil.getConfig().getCameraDetectType() == Const.FACE_ONE_EYE_ALIVE) {
            FaceAttribute faceAttribute = getFaceAttribute(faceApi, imageData, faceRect, Const.IFACEREC_LIVE_MASK, true);
            if (faceAttribute == null) {
                handleContinueOnce();
                return;
            }
            for (int i = 0; i < faceAttribute.faceRecAttrResult.length; i++) {
                if (faceAttribute.faceRecAttrResult[i].fConfidence <= Const.test) {
                    LogUtils.d(TAG, "非活体 -> " + faceAttribute.faceRecAttrResult[i].fConfidence + " 当前设置质量 -> " + Const.test);
                    handleNotAlive("图片检测为 : 图片非活体");
                    return;
                }
            }
        }

        if (Const.PAIR_TYPE_FACE.equals(type)) {
            handleFace(faceApi, imageData, faceRect);
        } else if (Const.PAIR_TYPE_IC.equals(type)) {
            handleIC(faceApi, imageData, faceRect);
        } else if (Const.PAIR_TYPE_ID.equals(type)) {
            handleID(faceApi, imageData, faceRect);
        } else {
            LogUtils.d(TAG, "========== 所有类型都不对,不匹配 ==========");
            handleContinueOnce();
        }
    }

    /**
     * 处理人脸比对
     */
    public void handleFace(Object faceApi, byte[] imageData, Object faceRect) {
        LogUtils.d(TAG, "================================= 开始人脸比对 =================================");
        if (FacePairStatus.getInstance().getLastFacePairSuccessAuthorityId() == Const.DEFAULT_CONITUE_AUTHORITY_ID) {
            if (checkContinue()) {
                return;
            }
            handlePairing();
            LogUtils.d(TAG, "====== 准备重新获取特征值 ======");
            if (checkContinue()) {
                return;
            }
            byte[] faceFeature = getFaceFeature(faceApi, imageData, faceRect, true);
            if (faceFeature == null) {
                handleFail(Const.OPEN_DOOR_TYPE_FACE, "获取特征值失败", Const.FACE_PAIR_ERROR_CODE_FACE_FEATURE_FAIL);
                return;
            }
            if (checkContinue()) {
                return;
            }
            //开始匹配特征值
            checkFaceFeature(faceApi, imageData, faceFeature, faceRect);
        } else {
            LogUtils.d(TAG, "====== 检测上一次匹配成功的人的id,获取特征值 ====== ");
            //上一次有成功识别的人
            byte[] faceFeature = getFaceFeature(faceApi, imageData, faceRect, true);
            if (checkContinue()) {
                return;
            }
            if (faceFeature == null) {
                //提取失败本次匹配结束
                handleFinish();
                return;
            }
            PersonBean personBean = getPairLastPeople(faceApi, faceFeature, FacePairStatus.getInstance().getLastFacePairSuccessAuthorityId());
            if (checkContinue()) {
                return;
            }
            if (personBean == null) {
                FacePairStatus.getInstance().setLastFacePairSuccessAuthorityId(Const.DEFAULT_CONITUE_AUTHORITY_ID);
                LogUtils.d(TAG, "上次匹配 : Person 信息 = null");
                //提取失败本次匹配结束
                handleFinish();
            } else {
                LogUtils.d(TAG, "上次匹配 : Person 信息 = " + personBean);
                //还是上次那个人
                SystemClock.sleep(300);
                if (checkContinue()) {
                    return;
                }
                if (!checkPersonAccess(personBean)) {
                    LogUtils.d(TAG, "上次匹配 : Person 信息 ,权限不足,不继续认为是同一人");
                    handleFinish();
                } else {
                    handleSuccess(personBean, null, 0, null, Const.FACE_PAIR_SAME_PEOPLE, true);
                }
            }
        }
    }

    private void checkFaceFeature(Object faceApi, byte[] imageData, byte[] updateFaceFeature, Object faceRect) {

        LogUtils.d(TAG, "=================================开始通过照片匹配===============================");

        HashMap<Float, PersonBean> takeThree = null;
        HashMap<Float, PersonBean> takeTwo = null;

        //获取三次学习特征值的比对结果
        HashMap<Float, PersonBean> map = new HashMap<>();
        if (Const.SDK_HONG_RUAN.equals(Const.SDK)) {

            try {
                LogUtils.d(TAG, "========== 添加数据到多线程比对 2 学习照");
                ThreadManager.getArrayBlockingQueue(PairThreadTwo.class.getSimpleName()).put(new PairBean(Const.THREAD_PAIR_TYPE_LEARN, updateFaceFeature));
            } catch (InterruptedException e) {
                e.printStackTrace();
                LogUtils.e(TAG, "========== 多线程比对 2 存入失败 学习照 = " + e.getMessage());
                handleContinueOnce();
                return;
            }
            try {
                LogUtils.d(TAG, "========== 添加数据到多线程比对 3 学习照");
                ThreadManager.getArrayBlockingQueue(PairThreadThree.class.getSimpleName()).put(new PairBean(Const.THREAD_PAIR_TYPE_LEARN, updateFaceFeature));
            } catch (InterruptedException e) {
                e.printStackTrace();
                LogUtils.e(TAG, "========== 多线程比对 3 存入失败 学习照 = " + e.getMessage());
                handleContinueOnce();
                return;
            }

            map = getPairMap(faceApi, Const.THREAD_PAIR_INDEX_ONE, updateFaceFeature);
            LogUtils.d(TAG, "========== 多线程比对 1 结束 学习照 = " + map.size());

            try {
                takeTwo = ThreadManager.getSyncQueueTwo().take();
                LogUtils.d(TAG, "========== 多线程比对 2 结束 学习照 = " + takeTwo.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
                LogUtils.e(TAG, "========== 多线程比对 2 取出失败 学习照 = " + e.getMessage());
            }

            try {
                takeThree = ThreadManager.getSyncQueueThree().take();
                LogUtils.d(TAG, "========== 多线程比对 3 结束 学习照 = " + takeThree.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
                LogUtils.e(TAG, "========== 多线程比对 3 取出失败 学习照 = " + e.getMessage());
            }
            map.putAll(takeThree);
            map.putAll(takeTwo);
        }


        if (map.size() == 0) {
//            判断三次结果中是否有数据,没有数据,此时去正装照中获取比对的特征值数据
            try {
                LogUtils.d(TAG, "========== 添加数据到多线程比对 2 正装照");
                ThreadManager.getArrayBlockingQueue(PairThreadTwo.class.getSimpleName()).put(new PairBean(Const.THREAD_PAIR_TYPE_OFFICIAL, updateFaceFeature));
            } catch (InterruptedException e) {
                e.printStackTrace();
                LogUtils.e(TAG, "========== 多线程比对 2 存入失败 正装照 = " + e.getMessage());
                handleContinueOnce();
                return;
            }
            try {
                LogUtils.d(TAG, "========== 添加数据到多线程比对 3 正装照");
                ThreadManager.getArrayBlockingQueue(PairThreadThree.class.getSimpleName()).put(new PairBean(Const.THREAD_PAIR_TYPE_OFFICIAL, updateFaceFeature));
            } catch (InterruptedException e) {
                e.printStackTrace();
                LogUtils.e(TAG, "========== 多线程比对 3 存入失败 正装照 = " + e.getMessage());
                handleContinueOnce();
                return;
            }
            map = getPairOfficialMap(faceApi, Const.THREAD_PAIR_INDEX_ONE, updateFaceFeature, map);
            LogUtils.d(TAG, "========== 多线程比对 1 比对结束 正装照");

            try {
                takeTwo = ThreadManager.getSyncQueueTwo().take();
                LogUtils.d(TAG, "========== 多线程比对 2 结束 正装照 = " + takeTwo.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
                LogUtils.e(TAG, "========== 多线程比对 2 取出失败 正装照 = " + e.getMessage());
            }

            try {
                takeThree = ThreadManager.getSyncQueueThree().take();
                LogUtils.d(TAG, "========== 多线程比对 3 结束 正装照 = " + takeThree.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
                LogUtils.e(TAG, "========== 多线程比对 3 取出失败 正装照 = " + e.getMessage());
            }
            map.putAll(takeTwo);
            map.putAll(takeThree);
        }


        if (checkContinue()) {
            map.clear();
            return;
        }
        if (map.size() == 0) {
            map.clear();
            handleFail(Const.OPEN_DOOR_TYPE_FACE, "没有人高于指定阈值", Const.FACE_PAIR_ERROR_CODE_NOT_LOGIN);
            return;
        }
        //获取比对的阈值list,从大到小排序
        ArrayList<Float> faceFeatureRateList = getFaceFeatureRateList(map);

        PersonBean personBean = updateOfficialFaceHex(faceApi, updateFaceFeature, map, faceFeatureRateList);
        if (checkContinue()) {
            map.clear();
            return;
        }
        if (personBean == null) {
            //更新正装照失败
            map.clear();
            handleFail(Const.OPEN_DOOR_TYPE_FACE, "更新学习特征值失败", Const.FACE_PAIR_ERROR_CODE_NOT_LOGIN);
            return;
        }
        //检查权限
        if (checkContinue()) {
            map.clear();
            return;
        }
        if (!checkPersonAccess(personBean)) {
            map.clear();
            handleFail(Const.OPEN_DOOR_TYPE_FACE, "权限不足,无法通行", Const.FACE_PAIR_ERROR_CODE_NOT_AUTHORITY);
            return;
        }

        LogUtils.d(TAG, "最终比对值 = " + faceFeatureRateList + " \nbean = " + personBean.getName());

        handleSuccess(personBean, imageData, faceFeatureRateList.get(0), getRect(faceRect), Const.FACE_PAIR_NOT_SAME_PEOPLE, true);
    }

    private PersonBean getPairLastPeople(Object faceApi, byte[] updateFaceFeature, long lastAuthorityId) {
        FaceFeatureHexBean faceFeatureHexBean = mFaceMap.get(lastAuthorityId);
        if (faceFeatureHexBean == null) {
            LogUtils.d(TAG, "获取最后一个人的正装特征值失败");
            return null;
        }

        float pairMatching = getPairNumber(faceApi, updateFaceFeature, faceFeatureHexBean.getFaceFeatureByte());
        LogUtils.d(TAG, "获取最后一个人的正装特征值 = " + pairMatching);
        if (pairMatching <= 1 && pairMatching >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
            return PersonDao.query2AuthorityId(lastAuthorityId);
        }
        return null;
    }


    private ArrayList<Float> getFaceFeatureRateList(HashMap<Float, PersonBean> map) {
        //对比对后的值进行排序,从小到大
        Set<Float> faceFeatureList = map.keySet();
        ArrayList<Float> floats = new ArrayList<>(faceFeatureList);
        Collections.sort(floats);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < faceFeatureList.size(); i++) {
            stringBuilder.append("名字 = ");
            stringBuilder.append(map.get(floats.get(i)).getName());
            stringBuilder.append(" 通过比对值 = ");
            stringBuilder.append(floats.get(i));
        }
        LogUtils.d(TAG, "通过值比对超过阈值的人 = " + stringBuilder.toString());
        LogUtils.d(TAG, "======== 开始排序，从大到小 ========");

        //比对的值从大到小
        Collections.reverse(floats);
        LogUtils.d(TAG, "正装照的特征值列表 = " + floats);
        return floats;
    }

    private PersonBean updateOfficialFaceHex(Object faceApi, byte[] updateFaceFeature, HashMap<Float, PersonBean> map, ArrayList<Float> faceFeatureRateList) {
        //取第一个最大的值作为识别的人
        Float accordFeatureNumber = faceFeatureRateList.get(0);
        PersonBean personBean = map.get(accordFeatureNumber);
        FaceFeatureHexBean faceFeatureHexBean = FaceFeatureDao.query2AuthorityId(personBean.getAuth_id());
        //判断这个人是否存在正装照
        if (faceFeatureHexBean == null) {
            return null;
        }
        byte[] faceFeatureByte = faceFeatureHexBean.getFaceFeatureByte();
        float personFeatureNumber = getPairNumber(faceApi, updateFaceFeature, faceFeatureByte);
        LogUtils.d(TAG, "正装照更新匹配 = " + personFeatureNumber + " 名称 = " + personBean.getName());

        if (personFeatureNumber >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()
                && personFeatureNumber <= AppSettingUtil.getConfig().getFaceFeaturePairNumber() + 0.05) {
            LogUtils.d(TAG, "======== 学习照特征值更新 ======= " + personFeatureNumber);
            NowPicFeatureDao.insertOrReplace(personBean.getAuth_id(), personBean.getPerson_id(), updateFaceFeature, false);
        }
        return personBean;
    }


    /**
     * 处理IC卡逻辑
     *
     * @param faceApi
     * @param imageData
     * @param faceRect
     */
    public void handleIC(Object faceApi, byte[] imageData, Object faceRect) {
        LogUtils.d(TAG, "================================= 开始IC卡人脸比对 ===============================");
        handlePairing();
        PersonBean bean = PersonDao.query2ICCard(FaceTempData.getInstance().getIcMessage());
        //没有找到这个人,识别失败
        if (bean == null) {
            SystemClock.sleep(1000);
            handleFail(Const.OPEN_DOOR_TYPE_FACE_IC, "人脸+IC : IC 卡号不存在", Const.FACE_PAIR_ERROR_CODE_NOT_LOGIN);
            return;
        }

        //通过照的特征值
        byte[] cameraFaceFeature = getFaceFeature(faceApi, imageData, faceRect, true);
        if (cameraFaceFeature == null) {
            handleFail(Const.OPEN_DOOR_TYPE_FACE_IC, "人脸+IC : 获取特征值失败", Const.FACE_PAIR_ERROR_CODE_FACE_FEATURE_FAIL);
            return;
        }

        float facePair = facePair(faceApi, bean, cameraFaceFeature);
        if (facePair != 2.0f && bean.getPerson_id() < Const.PERSON_TYPE_GUEST_DEFAULT_AUTHORITY_ID && checkPersonAccess(bean)) {
            //IC卡比对成功,并且人员的id不是访客的id
            handleSuccess(bean, imageData, facePair, getRect(faceRect), Const.FACE_PAIR_NOT_SAME_PEOPLE, true);
        } else {
            handleFail(Const.OPEN_DOOR_TYPE_FACE_IC, "人脸+IC : 没有人高于指定阈值", Const.FACE_PAIR_ERROR_CODE_NOT_LOGIN);
        }
    }

    private float facePair(Object faceApi, PersonBean personBean, byte[] faceFeature) {
        NowPicFeatureHexBean nowPicFeatureHexBean = mNowPicMap.get(personBean.getAuth_id());
        float defaultPairNumber = 2.0f;
        if (nowPicFeatureHexBean == null) {
            LogUtils.d(TAG, "相机 : 获取最后一个人的特征值bean失败");
            return defaultPairNumber;
        }
        byte[] oneByte = nowPicFeatureHexBean.getNowPicOneByte();
        if (oneByte == null) {
            LogUtils.d(TAG, "相机 : 获取最后一个人的第一个学习特征值失败");
            return defaultPairNumber;
        }
        float oneFaceFeature = getPairNumber(faceApi, faceFeature, oneByte);
        float twoFaceFeature = 0f;
        float threeFaceFeature = 0f;

        LogUtils.d(TAG, "相机 : 获取最后一个人的第一个学习特征值 = " + oneFaceFeature);
        //第一次学习特征值比对
        if (oneFaceFeature <= 1 && oneFaceFeature >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
            byte[] twoByte = nowPicFeatureHexBean.getNowPicTwoByte();
            if (twoByte == null) {
                LogUtils.d(TAG, "相机 : 获取最后一个人的第二个学习特征值失败 ");
                return oneFaceFeature;
            } else {
                twoFaceFeature = getPairNumber(faceApi, faceFeature, twoByte);
                LogUtils.d(TAG, "相机 : 获取最后一个人的第二个学习特征值 = " + twoFaceFeature);
                //第二次学习特征值比对
                if (twoFaceFeature <= 1 && twoFaceFeature >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
                    byte[] threeByte = nowPicFeatureHexBean.getNowPicThreeByte();
                    if (threeByte == null) {
                        LogUtils.d(TAG, "相机 : 获取最后一个人的第三个学习特征值特征值失败");
                        return Math.max(oneFaceFeature, twoFaceFeature);
                    } else {
                        threeFaceFeature = getPairNumber(faceApi, faceFeature, threeByte);
                        LogUtils.d(TAG, "相机 : 获取最后一个人的第三个学习特征值特征值 = " + threeFaceFeature);
                        //第一次学习特征值比对
                        if (threeFaceFeature <= 1 && threeFaceFeature >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
                            return Math.max(threeFaceFeature, Math.max(oneFaceFeature, twoFaceFeature));
                        }
                    }
                }
            }
        }

        FaceFeatureHexBean faceFeatureHexBean = mFaceMap.get(personBean.getAuth_id());
        if (faceFeatureHexBean == null) {
            LogUtils.d(TAG, "相机 : 获取最后一个人的正装特征值失败 = ");
            return defaultPairNumber;
        }

        float pairMatching = getPairNumber(faceApi, faceFeature, faceFeatureHexBean.getFaceFeatureByte());
        LogUtils.d(TAG, "相机 : 获取最后一个人的正装特征值 = " + pairMatching);
        if (pairMatching <= 1 && pairMatching >= AppSettingUtil.getConfig().getFaceFeaturePairNumber()) {
            return pairMatching;
        }
        return defaultPairNumber;
    }

    /**
     * 处理ID卡逻辑
     *
     * @param faceApi
     * @param imageData
     * @param faceRect
     */
    public void handleID(Object faceApi, byte[] imageData, Object faceRect) {

        LogUtils.d(TAG, "======================= 开始比对身份证和人脸信息 =======================");
        //正在准备匹配
        FacePairStatus.getInstance().pairIng();
        byte[] faceImageDate = getFaceImageDate();
        if (faceImageDate == null) {
            LogUtils.e(TAG, "获取身份证图片data数据失败,return");
            return;
        }
        //获取身份证特征值
        System.out.println("=== 获取身份证特征值");
        byte[] idFaceFeature = getIdFaceFeature(faceApi, faceImageDate);
        if (idFaceFeature == null) {
            LogUtils.e(TAG, "获取身份证特征值失败,return");
            return;
        }

        byte[] cameraFaceFeature = getCameraFaceFeature(faceApi, imageData, faceRect);
        if (cameraFaceFeature == null) {
            return;
        }
        facePair(faceApi, cameraFaceFeature, idFaceFeature, imageData, getRect(faceRect), true);
    }

    private byte[] getFaceImageDate() {
        IDBean idMessage = FaceTempData.getInstance().getIdMessage();
        if (idMessage == null) {
            handleFail(Const.OPEN_DOOR_TYPE_FACE_ID, "人脸+身份证 : 获取身份证图片失败", Const.FACE_PAIR_ERROR_CODE_NOT_ID_IMAGE);
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(idMessage.getImage());
        if (bitmap == null) {
            handleFail(Const.OPEN_DOOR_TYPE_FACE_ID, "人脸+身份证 : 获取身份证图片失败", Const.FACE_PAIR_ERROR_CODE_NOT_ID_IMAGE);
            return null;
        }

        Bitmap backBitmap = BitmapUtil.getFull640Bitmap(bitmap);
        return BitmapUtil.bitmap2Byte(backBitmap);
//        return Bmp2YUV.getYUV420sp(Const.CAMERA_BITMAP_WIDTH, Const.CAMERA_BITMAP_HEIGHT, backBitmap);
    }

    /**
     * 获取身份证的人脸特征值
     */
    private byte[] getIdFaceFeature(Object faceApi, byte[] idData) {
        Object faceRect = getFaceRect(faceApi, idData, false);
        if (faceRect == null) {
            handleFail(Const.OPEN_DOOR_TYPE_FACE_ID, "人脸+身份证 : 获取人脸失败", Const.FACE_PAIR_ERROR_CODE_FACE_RECT);
            return null;
        }
        byte[] faceFeature = getFaceFeature(faceApi, idData, faceRect, false);

        if (faceFeature == null || faceFeature.length == 0) {
            handleFail(Const.OPEN_DOOR_TYPE_FACE_ID, "人脸+身份证 : 获取特征值失败", Const.FACE_PAIR_ERROR_CODE_FACE_FEATURE_FAIL);
            return null;
        }
        return faceFeature;
    }

    /**
     * 获取相机图片的人脸特征值
     */
    private byte[] getCameraFaceFeature(Object faceApi, byte[] data, Object faceRect) {
//        Object faceRect = getFaceRect(faceApi, data, null, true);
//        if (faceRect == null) {
//            handleFail(Const.OPEN_DOOR_TYPE_FACE_ID, "人脸+身份证 : 相机图片,获取人脸失败");
//            return null;
//        }
        byte[] faceFeature = getFaceFeature(faceApi, data, faceRect, true);
        if (faceFeature == null || faceFeature.length == 0) {
            handleFail(Const.OPEN_DOOR_TYPE_FACE_ID, "人脸+身份证 : 相机图片,获取特征值失败", Const.FACE_PAIR_ERROR_CODE_FACE_FEATURE_FAIL);
            return null;
        }
        return faceFeature;
    }


    private boolean facePair(Object faceApi, byte[] cameraFaceFeature, byte[] idFaceFeature, byte[] image, Rect rect, boolean handleFail) {
        float pairNumber = getPairNumber(faceApi, cameraFaceFeature, idFaceFeature);
        LogUtils.d(TAG, "人脸+身份证 : 特征值 = " + pairNumber + " 当前设定阈值 = " + AppSettingUtil.getConfig().getIdFeaturePairNumber());
        if (pairNumber >= AppSettingUtil.getConfig().getIdFeaturePairNumber() && pairNumber <= 1) {
            LogUtils.d(TAG, "人脸+身份证 : 特征值符合阈值");
            return faceSuccess(image, rect, pairNumber, handleFail);
        } else {
            if (handleFail) {
                handleFail(Const.OPEN_DOOR_TYPE_FACE_ID, "人脸+身份证 : 匹配值小于阈值", Const.FACE_PAIR_ERROR_CODE_NOT_LOGIN);
            }
            return false;
        }
    }

    private boolean faceSuccess(byte[] image, Rect rect, float pairNumber, boolean handleFail) {
        PersonBean personBean = PersonDao.query2IDCard(FaceTempData.getInstance().getIdMessage().getIdNumber().toLowerCase());
        LogUtils.d(TAG, "身份证+人脸成功 = " + personBean);
        if (personBean == null) {
            //没有这个人
            if (AppSettingUtil.getConfig().getGuestOpenDoorType() == Const.OPEN_DOOR_TYPE_GUEST_ID_FACE_UN_REGISTER) {
                //访客模式开启,是人脸+身份证模式,没有登记
                LogUtils.e(TAG, "========== 身份证+人脸成功 未登记访客模式");
                handleSuccess(new PersonBean(Const.DEFAULT_AUTHORITY_ID, Const.DEFAULT_AUTHORITY_ID,
                                FaceTempData.getInstance().getIdMessage().getImage(),
                                FaceTempData.getInstance().getIdMessage().getName().trim(),
                                AppSettingUtil.getConfig().getGuestOpenDoorNumber()),
                        null, pairNumber, null, Const.FACE_PAIR_NOT_SAME_PEOPLE, false);
                return true;
            }
        } else {
            //员工模式 或 访客已经登记
            //判断当前人是 员工 还是 登记的访客
            LogUtils.d(TAG, "person = " + personBean.toString());
            if (personBean.getPerson_id() >= Const.PERSON_TYPE_GUEST_DEFAULT_AUTHORITY_ID) {
                //登记的访客
                if (AppSettingUtil.getConfig().getGuestOpenDoorType() == Const.OPEN_DOOR_TYPE_GUEST_ID_FACE_REGISTER && checkPersonAccess(personBean)) {
                    //判断访客登记模式是否已经开启
                    LogUtils.e(TAG, "========== 身份证+人脸成功 登记访客模式");
                    handleSuccess(personBean,
                            image, pairNumber, rect, Const.FACE_PAIR_NOT_SAME_PEOPLE, true);
                    return true;
                }
            } else {
                //员工
                if (checkPersonAccess(personBean)) {
                    LogUtils.e(TAG, "========== 身份证+人脸成功 员工模式");
                    handleSuccess(personBean,
                            image, pairNumber, rect, Const.FACE_PAIR_NOT_SAME_PEOPLE, true);

                    return true;
                }
            }
        }
        LogUtils.e(TAG, "========== 身份证+人脸成功 失败,id不对或权限不足");
        if (handleFail) {
            handleFail(Const.OPEN_DOOR_TYPE_FACE_ID, "身份证+人脸成功 访客模式没有开启,失败 = " + AppSettingUtil.getConfig().getGuestOpenDoorType(), Const.FACE_PAIR_ERROR_CODE_NOT_GUEST_MODE);
        }
        return false;
    }


}
