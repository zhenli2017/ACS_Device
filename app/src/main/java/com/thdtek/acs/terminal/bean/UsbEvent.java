package com.thdtek.acs.terminal.bean;

/**
 * Time:2018/11/9
 * User:lizhen
 * Description:
 */

public class UsbEvent {
    private String msg ;
    private IDBean mIDBean;

    public UsbEvent(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public UsbEvent(IDBean IDBean) {
        mIDBean = IDBean;
    }

    public UsbEvent(String msg, IDBean IDBean) {
        this.msg = msg;
        mIDBean = IDBean;
    }

    public IDBean getIDBean() {
        return mIDBean;
    }

    public void setIDBean(IDBean IDBean) {
        mIDBean = IDBean;
    }
}
