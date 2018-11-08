package com.thdtek.acs.terminal.bean;

import android.graphics.Bitmap;

public class PairEvent {

    //当前状态,0:准备匹配,1:找到人脸,2:正在匹配,3:匹配成功,4:匹配失败,5:没有权限,6:本次匹配结束,7:数据库中没有人
    public int status;
    //状态信息
    public String message;
    //姓名
    public String name;
    //工号
    public String iCCardNumber;
    //图片路径
    public String imagePath;
    //通过的bean类
    public AccessRecordBean accessRecordBean;
    public PersonBean personBean;
    public Bitmap bitmap;

    public PairEvent(int status, PersonBean personBean) {
        this.status = status;
        this.personBean = personBean;
    }

    public PairEvent(int status, Bitmap bitmap) {
        this.status = status;
        this.bitmap = bitmap;
    }

    public PairEvent(int status) {
        this.status = status;
    }

    public PairEvent(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public PairEvent(int status, AccessRecordBean accessRecordBean, PersonBean personBean) {
        this.status = status;
        this.accessRecordBean = accessRecordBean;
        this.personBean = personBean;
    }

    public PairEvent(int status, String message, String name, String iCCardNumber, String imagePath, AccessRecordBean accessRecordBean) {
        this.status = status;
        this.message = message;
        this.name = name;
        this.iCCardNumber = iCCardNumber;
        this.imagePath = imagePath;
        this.accessRecordBean = accessRecordBean;
    }
}
