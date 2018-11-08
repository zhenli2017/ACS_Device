package com.thdtek.acs.terminal.socket.core;


import com.thdtek.acs.terminal.Msg;

/**
 * 获取请求码
 */
public class RequestSeq {

    //普通请求seq
    private static int seq = 99;
    private static synchronized int getRequestCode() {
        if (seq >= 999999) {
            seq = 99;
        }
        seq += 2;
        return seq;
    }


    //上传dev_key请求seq
    private static int seq_dev_key = -1;
    private static synchronized int getDevKeyRequestCode() {
        if (seq_dev_key >= 49) {
            seq_dev_key = -1;
        }
        seq_dev_key += 2;
        return seq_dev_key;
    }

    //登录请求seq
    private static int seq_login = 49;
    private static synchronized int getLoginRequestCode() {
        if (seq_login >= 99) {
            seq_login = 49;
        }
        seq_login += 2;
        return seq_login;
    }

    public static synchronized int getRequestKey(Msg.Message msg){
        int seq_local = -1;
        if(msg.hasRegisterDeviceReq()){
            seq_local = RequestSeq.getDevKeyRequestCode();
        } else if(msg.hasLoginReq()){
            seq_local = RequestSeq.getLoginRequestCode();
        } else{
            seq_local = RequestSeq.getRequestCode();
        }
        return seq_local;
    }

    public static synchronized boolean isDevKeyRequestCode(int code){
        if(1 <= code && code <= 49){
            return true;
        }
        return false;
    }

    public static synchronized boolean isLoginRequestCode(int code){
        if(51 <= code && code <= 99){
            return true;
        }
        return false;
    }
}
