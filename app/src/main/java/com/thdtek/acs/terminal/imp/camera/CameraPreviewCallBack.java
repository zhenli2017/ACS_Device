package com.thdtek.acs.terminal.imp.camera;

import android.hardware.Camera;

/**
 * Time:2018/6/23
 * User:lizhen
 * Description:
 */

public interface CameraPreviewCallBack {
    void onPreviewFrame(byte[] bytes, Camera camera);
}
