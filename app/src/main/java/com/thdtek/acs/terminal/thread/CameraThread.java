package com.thdtek.acs.terminal.thread;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.SystemClock;

import com.intellif.FaceRect;
import com.thdtek.acs.terminal.bean.CameraPhotoEvent;
import com.thdtek.acs.terminal.bean.IDBean;
import com.thdtek.acs.terminal.face.FacePairStatus;
import com.thdtek.acs.terminal.face.FaceTempData;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.BitmapUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.camera.CameraUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

/**
 * Time:2018/11/12
 * User:lizhen
 * Description:
 */

public class CameraThread extends BaseThread {
    private static final String TAG = CameraThread.class.getSimpleName();

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public void handleData(Object faceApi, byte[] imageData, Object faceRect, String type) {
        super.handleData(faceApi, imageData, faceRect, type);
        LogUtils.d(TAG, "======================= CameraThread 开始比对身份证和人脸信息 =======================");

        CameraPhotoEvent cameraPhotoEvent = new CameraPhotoEvent(CameraPhotoEvent.CODE_FAIL);
        byte[] cameraFaceFeature = getCameraFaceFeature(faceApi, imageData, faceRect);
        if (cameraFaceFeature == null) {
            LogUtils.e(TAG, "身份证录入 : 获取摄像头特征值失败,return");
            EventBus.getDefault().post(cameraPhotoEvent);
            return;
        }
        IDBean idMessage = FaceTempData.getInstance().getIdMessage();
        if (idMessage == null) {
            LogUtils.e(TAG, "身份证录入 : 身份证 IDBean = null return");
            EventBus.getDefault().post(cameraPhotoEvent);
            return;
        }
        //获取身份证图片信息
        Bitmap bitmap = getFaceImageDate(idMessage);
        if (bitmap == null) {
            LogUtils.e(TAG, "身份证录入 : 获取身份证图片bitmap数据失败,return");
            EventBus.getDefault().post(cameraPhotoEvent);
            return;
        }
        //获取身份证特征值
        byte[] idFaceFeature = getIdFaceFeature(faceApi, bitmap);
        if (idFaceFeature == null) {
            LogUtils.e(TAG, "身份证录入 : 获取身份证特征值失败,return");
            EventBus.getDefault().post(cameraPhotoEvent);
            return;
        }

        float pairNumber = getPairNumber(faceApi, cameraFaceFeature, idFaceFeature);
        float tempNumber = Const.SDK.equals(Const.SDK_YUN_TIAN_LI_FEI) ? 0.75f : 0.5f;
        LogUtils.d(TAG, "身份证录入 : 特征值 = " + pairNumber + " 当前设定阈值 = " + tempNumber);
        if (pairNumber >= tempNumber && pairNumber <= 1) {
            LogUtils.d(TAG, "身份证录入 : 特征值符合阈值");
            cameraPhotoEvent.setType(CameraPhotoEvent.CODE_SUCCESS);
        } else {
            LogUtils.d(TAG, "身份证录入 : 特征值 不 符合阈值");
            EventBus.getDefault().post(cameraPhotoEvent);
            return;
        }
        //保存图片到临时路径
        try {
            String s = CameraUtil.save2Temp(imageData, System.currentTimeMillis() + "_temp_insert" + Const.IMAGE_TYPE_DEFAULT_JPG, Bitmap.CompressFormat.JPEG, null);
            cameraPhotoEvent.setImagePath(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().post(cameraPhotoEvent);

    }

    /**
     * 获取身份证的人脸特征值
     */
    private byte[] getIdFaceFeature(Object faceApi, Bitmap bitmap) {
        byte[] idData = BitmapUtil.bitmap2Byte(bitmap);
        Object faceRect = getFaceRect(faceApi, idData, false, bitmap.getWidth(), bitmap.getHeight());
        if (faceRect == null) {
            LogUtils.d(TAG, "身份证录入 : 获取人脸失败");
            return null;
        }
        byte[] faceFeature = getFaceFeature(faceApi, idData, faceRect, false, bitmap.getWidth(), bitmap.getHeight());

        if (faceFeature == null || faceFeature.length == 0) {
            LogUtils.d(TAG, "身份证录入 : 获取特征值失败");
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
        byte[] faceFeature = getFaceFeature(faceApi, data, faceRect, true, Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT);
        if (faceFeature == null || faceFeature.length == 0) {
            handleFail(Const.OPEN_DOOR_TYPE_FACE_ID, "身份证录入 : 相机图片,获取特征值失败", Const.FACE_PAIR_ERROR_CODE_FACE_FEATURE_FAIL);
            return null;
        }
        return faceFeature;
    }

}
