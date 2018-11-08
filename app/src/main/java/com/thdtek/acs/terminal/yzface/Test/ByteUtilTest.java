package com.thdtek.acs.terminal.yzface.Test;

import android.util.Log;

import com.thdtek.acs.terminal.yzface.Message.ByteUtil;


public class ByteUtilTest {
    private final static String TAG = "ByteUtilTest";

   public  static void HexStringToBytes(){

        String strBuf  = "00180611E9F5" +
                "C0A80196FFFFFF00C0A801010000000000000000021F401FA500000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000";

        //Log.d(TAG,"hexStringToBytes "+strBuf);
        byte [] byteBuf = ByteUtil.hexStringToBytes(strBuf);

        String strBuf2 = ByteUtil.bytesToHexString(byteBuf);
        //Log.d(TAG,"bytesToHexString "+strBuf2);

        if(strBuf.equals(strBuf2)){
            Log.e(TAG, "HexStringToBytes Test OK");
        }else {
            Log.e(TAG, "HexStringToBytes Test NG");
        }

    }
}
