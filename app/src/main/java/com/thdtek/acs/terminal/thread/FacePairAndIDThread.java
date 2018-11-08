package com.thdtek.acs.terminal.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.thdtek.acs.terminal.bean.IDBean;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.face.FacePairStatus;
import com.thdtek.acs.terminal.face.FaceTempData;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.BitmapUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;
@Deprecated
public class FacePairAndIDThread extends BaseThread {

    private static final String TAG = FacePairAndIDThread.class.getSimpleName();

    @Override
    public String getTag() {
        return TAG;
    }
//
//    @Override
//    public void handleData(Object faceApi, byte[] imageData, Object faceRect,String type) {
//
//        LogUtils.d(TAG, "======================= 开始比对身份证和人脸信息 =======================");
//        //正在准备匹配
//        FacePairStatus.getInstance().pairIng();
//        byte[] faceImageDate = getFaceImageDate();
//        if (faceImageDate == null) {
//            LogUtils.e(TAG, "获取身份证图片data数据失败,return");
//            return;
//        }
//        //获取身份证特征值
//        System.out.println("=== 获取身份证特征值");
//        byte[] idFaceFeature = getIdFaceFeature(faceApi, faceImageDate);
//        if (idFaceFeature == null) {
//            LogUtils.e(TAG, "获取身份证特征值失败,return");
//            return;
//        }
//
//        byte[] cameraFaceFeature = getCameraFaceFeature(faceApi, imageData, faceRect);
//        if (cameraFaceFeature == null) {
//            return;
//        }
//        facePair(faceApi, cameraFaceFeature, idFaceFeature, imageData, getRect(faceRect), true);
//    }
//
//    private byte[] getFaceImageDate() {
//        IDBean idMessage = FaceTempData.getInstance().getIdMessage();
//        Bitmap bitmap = BitmapFactory.decodeFile(idMessage.getImage());
//        if (bitmap == null) {
//            handleFail(Const.OPEN_DOOR_TYPE_FACE_ID, "人脸+身份证 : 获取身份证图片失败");
//            return null;
//        }
//
//        Bitmap backBitmap = BitmapUtil.getFull640Bitmap(bitmap);
//        return BitmapUtil.bitmap2Byte(backBitmap);
////        return Bmp2YUV.getYUV420sp(Const.CAMERA_BITMAP_WIDTH, Const.CAMERA_BITMAP_HEIGHT, backBitmap);
//    }
//
//    /**
//     * 获取身份证的人脸特征值
//     */
//    private byte[] getIdFaceFeature(Object faceApi, byte[] idData) {
//        Object faceRect = getFaceRect(faceApi, idData, false);
//        if (faceRect == null) {
//            handleFail(Const.OPEN_DOOR_TYPE_FACE_ID, "人脸+身份证 : 获取人脸失败");
//            return null;
//        }
//        byte[] faceFeature = getFaceFeature(faceApi, idData, faceRect, false);
//
//        if (faceFeature == null || faceFeature.length == 0) {
//            handleFail(Const.OPEN_DOOR_TYPE_FACE_ID, "人脸+身份证 : 获取特征值失败");
//            return null;
//        }
//        return faceFeature;
//    }
//
//    /**
//     * 获取相机图片的人脸特征值
//     */
//    private byte[] getCameraFaceFeature(Object faceApi, byte[] data, Object faceRect) {
////        Object faceRect = getFaceRect(faceApi, data, null, true);
////        if (faceRect == null) {
////            handleFail(Const.OPEN_DOOR_TYPE_FACE_ID, "人脸+身份证 : 相机图片,获取人脸失败");
////            return null;
////        }
//        byte[] faceFeature = getFaceFeature(faceApi, data, faceRect, true);
//        if (faceFeature == null || faceFeature.length == 0) {
//            handleFail(Const.OPEN_DOOR_TYPE_FACE_ID, "人脸+身份证 : 相机图片,获取特征值失败");
//            return null;
//        }
//        return faceFeature;
//    }
//
//
//    private boolean facePair(Object faceApi, byte[] cameraFaceFeature, byte[] idFaceFeature, byte[] image, Rect rect, boolean handleFail) {
//        float pairNumber = getPairNumber(faceApi, cameraFaceFeature, idFaceFeature);
//        LogUtils.d(TAG, "人脸+身份证 : 特征值 = " + pairNumber + " 当前设定阈值 = " + AppSettingUtil.getConfig().getIdFeaturePairNumber());
//        if (pairNumber >= AppSettingUtil.getConfig().getIdFeaturePairNumber() && pairNumber <= 1) {
//            LogUtils.d(TAG, "人脸+身份证 : 特征值符合阈值");
//            return faceSuccess(image, rect, pairNumber, handleFail);
//        } else {
//            if (handleFail) {
//                handleFail(Const.OPEN_DOOR_TYPE_FACE_ID, "人脸+身份证 : 匹配值小于阈值");
//            }
//            return false;
//        }
//    }
//
//    private boolean faceSuccess(byte[] image, Rect rect, float pairNumber, boolean handleFail) {
//        PersonBean personBean = PersonDao.query2IDCard(FaceTempData.getInstance().getIdMessage().getIdNumber().toLowerCase());
//        LogUtils.d(TAG, "身份证+人脸成功 = " + personBean);
//        if (personBean == null) {
//            //没有这个人
//            if (AppSettingUtil.getConfig().getGuestOpenDoorType() == Const.OPEN_DOOR_TYPE_GUEST_ID_FACE_UN_REGISTER) {
//                //访客模式开启,是人脸+身份证模式,没有登记
//                handleSuccess(new PersonBean(Const.DEFAULT_AUTHORITY_ID, Const.DEFAULT_AUTHORITY_ID,
//                                FaceTempData.getInstance().getIdMessage().getImage(),
//                                FaceTempData.getInstance().getIdMessage().getName().trim(),
//                                AppSettingUtil.getConfig().getGuestOpenDoorNumber()),
//                        null, pairNumber, null, Const.FACE_PAIR_NOT_SAME_PEOPLE, false);
//                LogUtils.e(TAG, "========== 身份证+人脸成功 未登记访客模式");
//                return true;
//            }
//        } else {
//            //员工模式 或 访客已经登记
//            //判断当前人是 员工 还是 登记的访客
//            LogUtils.d(TAG, "person = " + personBean.toString());
//            if (personBean.getPerson_id() >= Const.PERSON_TYPE_GUEST_DEFAULT_AUTHORITY_ID) {
//                //登记的访客
//                if (AppSettingUtil.getConfig().getGuestOpenDoorType() == Const.OPEN_DOOR_TYPE_GUEST_ID_FACE_REGISTER && checkPersonAccess(personBean)) {
//                    //判断访客登记模式是否已经开启
//                    handleSuccess(personBean,
//                            image, pairNumber, rect, Const.FACE_PAIR_NOT_SAME_PEOPLE, true);
//                    LogUtils.e(TAG, "========== 身份证+人脸成功 登记访客模式");
//                    return true;
//                }
//            } else {
//                //员工
//                if (checkPersonAccess(personBean)) {
//                    handleSuccess(personBean,
//                            image, pairNumber, rect, Const.FACE_PAIR_NOT_SAME_PEOPLE, true);
//                    LogUtils.e(TAG, "========== 身份证+人脸成功 员工模式");
//                    return true;
//                }
//            }
//        }
//        LogUtils.e(TAG, "========== 身份证+人脸成功 失败,id不对或权限不足");
//        if (handleFail) {
//            handleFail(Const.OPEN_DOOR_TYPE_FACE_ID, "身份证+人脸成功 访客模式没有开启,失败 = " + AppSettingUtil.getConfig().getGuestOpenDoorType());
//        }
//        return false;
//    }
}
