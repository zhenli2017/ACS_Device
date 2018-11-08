package com.thdtek.acs.terminal.bean;

/**
 * Time:2018/10/24
 * User:lizhen
 * Description:
 */

public class HttpResponseBean {

    /**
     * msg : 成功
     * status : 0
     */

    private String msg;
    private int status;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
