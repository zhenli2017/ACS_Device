package com.thdtek.acs.terminal.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.SurfaceHolder;

import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.imp.camera.CameraPreviewCallBack;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SPUtils;
import com.thdtek.acs.terminal.util.camera.CameraUtil;

import java.io.IOException;


/**
 * Time:2018/6/20
 * User:lizhen
 * Description:
 * 照相机预览类
 */

public class CameraPreview2 implements Camera.PreviewCallback {

    private final String TAG = CameraPreview2.class.getSimpleName();
    private Camera mCamera;
    private CameraPreviewCallBack mCallBack;

    private int mCameraId;

    /**
     * 打开默认摄像头,背后的摄像头
     *
     * @param callBack
     */
    public CameraPreview2(CameraPreviewCallBack callBack) {
        //默认获取背部的镜头
        mCallBack = callBack;
        int numberOfCameras = getNumberCamera();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCamera = getCameraInstance(i);
                break;
            }
        }
    }

    /**
     * 打开指定的摄像头
     *
     * @param cameraId 摄像头ID
     * @param callBack 实时预览数据回调
     */
    public CameraPreview2(int cameraId, CameraPreviewCallBack callBack) {
        mCameraId = cameraId;
        mCallBack = callBack;
        mCamera = getCameraInstance(cameraId);
    }

    public Camera getCamera() {
        return mCamera;
    }

    /**
     * 获取相机对象,照相机ID
     *
     * @return
     */
    public Camera getCameraInstance(int cameraId) {


        int numberOfCameras = Camera.getNumberOfCameras();
        LogUtils.d(TAG, "numberOfCameras = " + numberOfCameras);
        Camera camera = null;
        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            LogUtils.e(TAG, "getCameraInstance error = " + e.getMessage());
        }
        try {
            if (camera != null) {
                CameraUtil.parameters(camera);
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "getCameraInstance parameters error = " + e.getMessage());
        }

        return camera;
    }

    /**
     * 获取可用照相机的数量
     *
     * @return
     */
    public int getNumberCamera() {
        return Camera.getNumberOfCameras();
    }

    public void setParameters(int width, int height) {
        if (mCamera == null) {
            LogUtils.d(TAG, "setParameters fail,mCamera == null");
            return;
        }
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(Const.CAMERA_PREVIEW_WIDTH, Const.CAMERA_PREVIEW_HEIGHT);
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            LogUtils.d(TAG, "setParameters fail = " + e.getMessage());
        }
    }

    /**
     * 开始预览数据
     *
     * @param holder
     */
    public void startPreview(SurfaceHolder holder) {
        if (mCamera == null) {
            LogUtils.d(TAG, "startPreview fail,mCamera == null");
            return;
        }
        if (holder == null) {
            LogUtils.d(TAG, "startPreview fail,mHolder == null");
        } else {
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (Exception e) {
                LogUtils.d(TAG, "startPreview fail = " + e.getMessage());
            }
        }
        try {
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
            LogUtils.d(TAG, "startPreview success");
            SPUtils.put(MyApplication.getContext(), Const.DEVICE_REBOOT, false);
        } catch (Exception e) {
            LogUtils.d(TAG, "startPreview start Preview fail = " + e.getMessage());
        }

    }

    /**
     * 停止预览数据
     *
     * @param release
     */
    public void stopPreview(boolean release) {
        if (mCamera == null) {
            LogUtils.d(TAG, "stopPreview fail,mCamera == null");
            return;
        }
        try {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
        } catch (Exception e) {
            LogUtils.d(TAG, "startPreview stop Preview fail = " + e.getMessage());
        }
        if (release) {
            mCamera.release();
            mCamera = null;
        }
        LogUtils.d(TAG, "stopPreview success");
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (mCallBack == null) {

        }
        mCallBack.onPreviewFrame(bytes, camera);
    }
}
