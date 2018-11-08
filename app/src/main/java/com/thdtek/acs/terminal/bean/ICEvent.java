package com.thdtek.acs.terminal.bean;

/**
 * Time:2018/11/7
 * User:lizhen
 * Description:
 */

public class ICEvent {
    private String msg;
    private String icNumber;
    private int type ;

    public ICEvent(String msg, String icNumber, int type) {
        this.msg = msg;
        this.icNumber = icNumber;
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getIcNumber() {
        return icNumber;
    }

    public void setIcNumber(String icNumber) {
        this.icNumber = icNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
