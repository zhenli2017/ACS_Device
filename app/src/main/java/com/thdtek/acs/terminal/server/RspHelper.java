package com.thdtek.acs.terminal.server;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RspHelper {
    private int status;
    private String msg;
    private Object data;


    public RspHelper() {

    }

    public RspHelper(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public RspHelper(int status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RspHelper{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public String toJsonString() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", getStatus());
        map.put("msg", getMsg());
        map.put("data", getData());
        String s = new Gson().toJson(map);
        System.out.println("gson = "+s);
        return s;
    }
}
