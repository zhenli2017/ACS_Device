package com.thdtek.acs.terminal.haogonge;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sign {

    public String getSignString(String sn, long time, String body, String encryptKey){
        if(TextUtils.isEmpty(encryptKey)){
            return null;
        }
        if(TextUtils.isEmpty(sn)){
            return null;
        }

        String bodyNew = "";
        if(TextUtils.isEmpty(body)){
            return null;
        }else{
            bodyNew = body.replaceAll("\n", "_");
        }

        String oriSort = sort(sn, String.valueOf(time), bodyNew, encryptKey);

        String sign = new MD5().encode(oriSort);

//        LogUtils.d(TAG, "sn："+sn);
//        LogUtils.d(TAG, "time："+time);
//        LogUtils.d(TAG, "bodyNew："+bodyNew);
//        LogUtils.d(TAG, "encryptKey："+encryptKey);

//        LogUtils.d(TAG, "排序前："+ori);
//        LogUtils.d(TAG, "排序后："+oriSort);
//        LogUtils.d(TAG, "MD5加密后："+sign);

        return sign;

    }

    private String sort(String ... contents){
        List<String> lst = new ArrayList<>();
        if(contents != null){
            for (int i = 0; i < contents.length; i++) {
                String item = contents[i];
                lst.add(item);
            }
        }
        Collections.sort(lst);

        String result = null;
        if(lst.size() > 0){
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < lst.size(); i++) {
                sb.append(lst.get(i));
            }
            result = sb.toString();
        }else{
            result = null;
        }
        return result;
    }
}
