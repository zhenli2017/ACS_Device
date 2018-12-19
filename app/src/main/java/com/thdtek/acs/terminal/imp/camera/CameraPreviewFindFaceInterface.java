package com.thdtek.acs.terminal.imp.camera;

import android.graphics.Rect;

import com.thdtek.acs.terminal.bean.AccessRecordBean;
import com.thdtek.acs.terminal.bean.FacePairBean;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.facelibrary.FaceRect;

/**
 * Time:2018/6/23
 * User:lizhen
 * Description:
 */

public interface CameraPreviewFindFaceInterface {
    //准备捕获人脸
    void findFaceReady();

    //没有找到人脸
    void findNotFace(Object rect, int color);

    //找到人脸
    void findFace(Object rect, int color);

    //正在匹配
    void facePairing();

    //匹配成功
    void facePairSuccess(FacePairBean facePairBean, int samePeople);

    //匹配失败
    void facePairFail(String msg,int code);

    //处于不显示的识别失败
    void facePairFailNoVisible();

    //本次匹配结束
    void facePairFinish();

    //非活体
    void faceNotAlive(String msg);
}
