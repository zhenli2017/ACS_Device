package com.thdtek.acs.terminal.bean;

import android.graphics.Rect;

import java.util.Arrays;

/**
 * Time:2018/10/31
 * User:lizhen
 * Description:
 */

public class FacePairEvent {

    //0:人脸,1:IC卡,2:ID卡
    private String type;
    private PersonBean personBean;
    //摄像头抓拍数据
    private byte[] image;
    //特征值比对结果
    private float rate;
    //人脸框
    private Rect faceRect;
    //是否是同一个人
    private int samePerson;
    //是否是摄像头数据
    private boolean isCameraData;

    public FacePairEvent() {
    }

    public FacePairEvent(String type, PersonBean personBean, byte[] image, float rate, Rect faceRect, int samePerson, boolean isCameraData) {
        this.type = type;
        this.personBean = personBean;
        this.image = image;
        this.rate = rate;
        this.faceRect = faceRect;
        this.samePerson = samePerson;
        this.isCameraData = isCameraData;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PersonBean getPersonBean() {
        return personBean;
    }

    public void setPersonBean(PersonBean personBean) {
        this.personBean = personBean;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public Rect getFaceRect() {
        return faceRect;
    }

    public void setFaceRect(Rect faceRect) {
        this.faceRect = faceRect;
    }

    public int getSamePerson() {
        return samePerson;
    }

    public void setSamePerson(int samePerson) {
        this.samePerson = samePerson;
    }

    public boolean isCameraData() {
        return isCameraData;
    }

    public void setCameraData(boolean cameraData) {
        isCameraData = cameraData;
    }

    @Override
    public String toString() {
        return "FacePairEvent{" +
                "type='" + type + '\'' +
                ", personBean=" + personBean +
                ", image=" + Arrays.toString(image) +
                ", rate=" + rate +
                ", faceRect=" + faceRect +
                ", samePerson=" + samePerson +
                ", isCameraData=" + isCameraData +
                '}';
    }
}
