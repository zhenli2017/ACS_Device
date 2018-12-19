package com.thdtek.acs.terminal.haogonge;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ParseResp {

    public void parseRegisterDevice(String resp){
        if(TextUtils.isEmpty(resp)){
            return;
        }

        String[] arr = resp.split("\n");

        Map<String, String> map = new HashMap<>();
        if(arr != null){
            for (int i = 0; i < arr.length; i++) {
                String item = arr[i];
                if("transInterval".equals(item)){
                    map.put("transInterval", item);
                }
                else if("encryptKey".equals(item)){
                    map.put("encryptKey", item);
                }
                else if("uploadAttPic".equals(item)){
                    map.put("uploadAttPic", item);
                }
                else if("verifyType".equals(item)){
                    map.put("verifyType", item);
                }
                else if("offlineAction".equals(item)){
                    map.put("offlineAction", item);
                }
            }
        }


    }
}
