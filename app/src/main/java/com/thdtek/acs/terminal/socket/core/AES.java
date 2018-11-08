package com.thdtek.acs.terminal.socket.core;

import android.content.Context;
import android.util.Log;

import com.thdtek.acs.terminal.util.AESUtils;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.ByteFormatTransferUtils;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;

public class AES {
    private static final String TAG = AES.class.getSimpleName();
    //aes的key
    public static final String AES_KEY = "1-23iop780as88xb";

    private static String getAesKey16() {
        return AES_KEY;
    }

    private static String getAesKey32() {
        if(Const.IS_OPEN_DYNAMIC_AESKEY){
            return AES_KEY + AppSettingUtil.getDeviceAesKey();
        }else{
            return getAesKey16();
        }

    }

    public static String getEncKey(int seq) {
        String key = "";
        if(seq % 2 == 1){
            if (RequestSeq.isDevKeyRequestCode(seq)) {
                key = getAesKey16();
            } else if (RequestSeq.isLoginRequestCode(seq)) {
                key = getAesKey16();
            } else {
                key = getAesKey32();
            }
        } else{
            key = getAesKey32();
        }
//        LogUtils.d(TAG, "getEncKey="+key);
        return key;
    }

    public static String getDecKey(int seq) {
        String key = "";

        if (seq % 2 == 1) {
            if (RequestSeq.isDevKeyRequestCode(seq)) {
                key = getAesKey16();
            } else if (RequestSeq.isLoginRequestCode(seq)) {
                key = getAesKey32();
            } else {
                key = getAesKey32();
            }
        } else {
            key = getAesKey32();
        }
//        LogUtils.d(TAG, "getDecKey="+key);
        return key;
    }
}
