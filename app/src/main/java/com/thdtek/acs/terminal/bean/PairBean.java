package com.thdtek.acs.terminal.bean;

/**
 * Time:2018/10/19
 * User:lizhen
 * Description:
 */

public class PairBean {
    private int type;
    private byte[] faceFeature;

    public PairBean(int type, byte[] faceFeature) {
        this.type = type;
        this.faceFeature = faceFeature;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(byte[] faceFeature) {
        this.faceFeature = faceFeature;
    }
}
