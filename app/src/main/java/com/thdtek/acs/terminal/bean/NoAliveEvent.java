package com.thdtek.acs.terminal.bean;

/**
 * Time:2018/11/7
 * User:lizhen
 * Description:
 */

public class NoAliveEvent {
    private int type;
    private String msg;

    public NoAliveEvent(int type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
