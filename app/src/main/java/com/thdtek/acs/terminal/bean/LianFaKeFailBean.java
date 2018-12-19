package com.thdtek.acs.terminal.bean;

import java.util.List;

/**
 * Time:2018/11/22
 * User:lizhen
 * Description:
 */

public class LianFaKeFailBean {

    /**
     * code : -1
     * msg : 身份证格式错误
     * result : []
     */

    private int code;
    private String msg;
    private List<?> result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<?> getResult() {
        return result;
    }

    public void setResult(List<?> result) {
        this.result = result;
    }
}
