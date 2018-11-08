package com.thdtek.acs.terminal.server;

public class VerifyUtils {

    public boolean isLong(String str){
        try {
            Long.parseLong(str);

        }catch (Exception e){
            return false;
        }
        return true;
    }
}
