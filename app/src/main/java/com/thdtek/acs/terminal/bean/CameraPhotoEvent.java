package com.thdtek.acs.terminal.bean;

/**
 * Time:2018/11/12
 * User:lizhen
 * Description:
 */

public class CameraPhotoEvent {

    public static final int CODE_FAIL = 0;
    public static final int CODE_SUCCESS = 1;
    //0:成功,1:失败
    private int type;
    private String imagePath;

    public CameraPhotoEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
