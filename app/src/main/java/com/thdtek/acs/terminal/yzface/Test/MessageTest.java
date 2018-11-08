package com.thdtek.acs.terminal.yzface.Test;

import android.util.Log;

import com.thdtek.acs.terminal.yzface.Message.ByteUtil;
import com.thdtek.acs.terminal.yzface.Message.Message;


public class MessageTest {
    private final static String TAG = "MessageTest";

    public  static void ConstructionByBuffer()
    {
        String strBuf  = "31FE000000008900180611E9F5" +
                "C0A80196FFFFFF00C0A801010000000000000000021F401FA500000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000";

        String strCategory                   = "31";
        String strCommand                    = "FE";
        String strParameter                  = "00";
        String strDataLength                 = "00000089";
        String strData  = "00180611E9F5" +
                "C0A80196FFFFFF00C0A801010000000000000000021F401FA500000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000";


        byte [] byteBuf = ByteUtil.hexStringToBytes(strBuf);

        Log.e(TAG,"Run ConstructionByBuffer Test");
        //Log.e(TAG,"buffer "+strBuf);

        Message message = new Message(byteBuf);
        Log.e(TAG,"Message.Category ="+ByteUtil.bytesToHexString(message.getCategory()).equals(strCategory));
        Log.e(TAG,"Message.Command ="+ByteUtil.bytesToHexString(message.getCommand()).equals(strCommand));
        Log.e(TAG,"Message.Parameter ="+ByteUtil.bytesToHexString(message.getParameter()).equals(strParameter));
        Log.e(TAG,"Message.DataLength ="+ByteUtil.bytesToHexString(message.getDataLength()).equals(strDataLength));
        Log.e(TAG,"Message.Data ="+ByteUtil.bytesToHexString(message.getData()).equals(strData));

    }

    public  static void ToByteBuffer()
    {
               String strBuf  = "31FE000000008900180611E9F5" +
                "C0A80196FFFFFF00C0A801010000000000000000021F401FA500000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000";

        byte [] byteBuf = ByteUtil.hexStringToBytes(strBuf);

       Log.e(TAG,"Run ConstructionByBuffer Test");

        Message message = new Message(byteBuf);

        byte[] byteBuf2 = message.toBytes();

        //Log.e(TAG,"Buffer "+strBuf);
        String strBuf2 = ByteUtil.bytesToHexString(byteBuf2);
        //Log.e(TAG,"Buffer "+strBuf2);

        if(strBuf.equals(strBuf2)){
            Log.e(TAG, "ToByteBuffer Test OK");
        }else {
            Log.e(TAG, "ToByteBuffer Test NG");
        }
    }
}
