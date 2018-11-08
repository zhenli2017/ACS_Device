package com.thdtek.acs.terminal.util;


import android.content.Context;

import com.thdtek.acs.terminal.base.MyApplication;

public class CodeUtil {

    private static Context context = MyApplication.getContext();

    public static String getStringByCode(int code){
        int id = context.getResources().getIdentifier(
                "code_"+code,
                "string",
                context.getPackageName());

        return context.getString(id);

    }
}
