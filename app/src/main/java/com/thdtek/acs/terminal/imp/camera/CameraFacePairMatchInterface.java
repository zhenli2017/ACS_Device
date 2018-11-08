package com.thdtek.acs.terminal.imp.camera;

import android.graphics.Rect;

import com.thdtek.acs.terminal.bean.PersonBean;

/**
 * Time:2018/6/23
 * User:lizhen
 * Description:
 */

public interface CameraFacePairMatchInterface {
    void facePairMatchPre(byte[] bytes);

    void facePairMatchSuccess(PersonBean bean, byte[] bytes, byte[] faceFeature, float maxRate, float personRate, float accordRate, Rect rect);

    void facePairMatchFail(String message);

    void faceNoAuthority(String msg);

    void facePairThisTimeOver();

}
