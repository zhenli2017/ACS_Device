package com.thdtek.acs.terminal.bean;

/**
 * Time:2018/6/26
 * User:lizhen
 * Description:
 */

public class CameraPreviewBean {
    private byte[] data;
    private Object rect;
    private String type;

    public CameraPreviewBean(byte[] data, Object rect, String type) {
        this.data = data;
        this.rect = rect;
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Object getRect() {
        return rect;
    }

    public void setRect(Object rect) {
        this.rect = rect;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
