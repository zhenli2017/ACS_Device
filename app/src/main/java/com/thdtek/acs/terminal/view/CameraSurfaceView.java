package com.thdtek.acs.terminal.view;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.thdtek.acs.terminal.camera.CameraPreview2;
import com.thdtek.acs.terminal.imp.camera.CameraPreviewCallBack;
import com.thdtek.acs.terminal.util.LogUtils;

/**
 * Time:2018/11/7
 * User:lizhen
 * Description:
 */

public class CameraSurfaceView extends SurfaceView implements
        SurfaceHolder.Callback {
    private static final String TAG = CameraSurfaceView.class.getSimpleName();
    private CameraPreview2 mCameraPreview2;

    public CameraSurfaceView(Context context) {
        super(context);
        init();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
        mCameraPreview2 = new CameraPreview2(0, mCameraPreviewCallBack);
    }

    private CameraPreviewCallBack mCameraPreviewCallBack;

    public void setPreviewCallBack(CameraPreviewCallBack callBack) {
        mCameraPreviewCallBack = callBack;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtils.i(TAG, "=============== surfaceCreated ===============");
        if (mCameraPreview2 != null) {
            mCameraPreview2.startPreview(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtils.i(TAG, "=============== surfaceChanged ===============");
        if (mCameraPreview2 != null) {
            mCameraPreview2.stopPreview(false);
            mCameraPreview2.setParameters(width, height);
            mCameraPreview2.startPreview(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtils.i(TAG, "=============== surfaceDestroyed ===============");
        if (mCameraPreview2 != null) {
            mCameraPreview2.stopPreview(true);
        }
        mCameraPreviewCallBack = null;
    }

}
