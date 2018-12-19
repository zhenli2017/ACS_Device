package com.thdtek.acs.terminal.bean;

/**
 * Time:2018/11/27
 * User:lizhen
 * Description:
 */

public class CacheFaceFeatureBean {
    private long time;
    private byte[] faceFeature;

    public CacheFaceFeatureBean(long time, byte[] faceFeature) {
        this.time = time;
        this.faceFeature = faceFeature;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public byte[] getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(byte[] faceFeature) {
        this.faceFeature = faceFeature;
    }
}
