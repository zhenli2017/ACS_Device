package com.thdtek.acs.terminal.bean;

import com.intellif.FaceRecAttrResult;

/**
 * Time:2018/10/15
 * User:lizhen
 * Description:
 */

public class FaceAttribute {
    public FaceRecAttrResult[] faceRecAttrResult;

    public FaceAttribute(FaceRecAttrResult[] faceRecAttrResult) {
        this.faceRecAttrResult = faceRecAttrResult;
    }

    public FaceRecAttrResult[] getFaceRecAttrResult() {
        return faceRecAttrResult;
    }

    public void setFaceRecAttrResult(FaceRecAttrResult[] faceRecAttrResult) {
        this.faceRecAttrResult = faceRecAttrResult;
    }
}
