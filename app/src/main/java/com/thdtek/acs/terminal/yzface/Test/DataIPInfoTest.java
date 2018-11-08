package com.thdtek.acs.terminal.yzface.Test;

import android.util.Log;

import com.thdtek.acs.terminal.yzface.Message.ByteUtil;
import com.thdtek.acs.terminal.yzface.Message.DataIPInfo;


public class DataIPInfoTest {
    private final static String TAG = "DataIPInfoTest";

    public  static void ConstructionByBuffer()
    {
        String strBuf  = "00180611E9F5" +
                "C0A80196FFFFFF00C0A801010000000000000000021F401FA500000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000";

        String strMAC                   = "00180611E9F5";
        String strIP                    = "C0A80196";
        String strMASK                  = "FFFFFF00";
        String strGatewayIP             = "C0A80101";
        String strDNS                   = "00000000";
        String strSecondDNS             = "00000000";
        String strTCPMode               = "02";
        String strLocalTCPListenPor     = "1F40";
        String strLocalUDPListenPort    = "1FA5";
        String strTargetPort            = "0000";
        String strTargetIP              = "00000000";
        String strAutoGetIP             = "00";
        String strDomainName            = "00000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                                          "00000000000000000000000000000000000000000000000000000000000000000000000000000000" +
                                          "00000000000000000000000000000000000000";

        byte [] byteBuf = ByteUtil.hexStringToBytes(strBuf);

        Log.e(TAG,"Run ConstructionByBuffer Test");
        //Log.e(TAG,"buffer "+strBuf);

        DataIPInfo dataIPInfo = new DataIPInfo(byteBuf);
        Log.e(TAG,"DataIPInfo.MAC ="+ByteUtil.bytesToHexString(dataIPInfo.getMAC()).equals(strMAC));
        Log.e(TAG,"DataIPInfo.IP ="+ByteUtil.bytesToHexString(dataIPInfo.getIP()).equals(strIP));
        Log.e(TAG,"DataIPInfo.MASK ="+ByteUtil.bytesToHexString(dataIPInfo.getMASK()).equals(strMASK));
        Log.e(TAG,"DataIPInfo.GatewayIP ="+ByteUtil.bytesToHexString(dataIPInfo.getGatewayIP()).equals(strGatewayIP));
        Log.e(TAG,"DataIPInfo.DNS ="+ByteUtil.bytesToHexString(dataIPInfo.getDNS()).equals(strDNS));
        Log.e(TAG,"DataIPInfo.SecondDNS ="+ByteUtil.bytesToHexString(dataIPInfo.getSecondDNS()).equals(strSecondDNS));
        Log.e(TAG,"DataIPInfo.TCPMode ="+ByteUtil.bytesToHexString(dataIPInfo.getTCPMode()).equals(strTCPMode));
        Log.e(TAG,"DataIPInfo.LocalTCPListenPort ="+ByteUtil.bytesToHexString(dataIPInfo.getLocalTCPListenPort()).equals(strLocalTCPListenPor));
        Log.e(TAG,"DataIPInfo.LocalUDPListenPort ="+ByteUtil.bytesToHexString(dataIPInfo.getLocalUDPListenPort()).equals(strLocalUDPListenPort));
        Log.e(TAG,"DataIPInfo.TargetPort ="+ByteUtil.bytesToHexString(dataIPInfo.getTargetPort()).equals(strTargetPort));
        Log.e(TAG,"DataIPInfo.TargetIP ="+ByteUtil.bytesToHexString(dataIPInfo.getTargetIP()).equals(strTargetIP));
        Log.e(TAG,"DataIPInfo.AutoGetIP ="+ByteUtil.bytesToHexString(dataIPInfo.getAutoGetIP()).equals(strAutoGetIP));
        Log.e(TAG,"DataIPInfo.DomainName ="+ByteUtil.bytesToHexString(dataIPInfo.getDomainName()).equals(strDomainName));
    }

    public  static void ToByteBuffer()
    {
        String strBuf  = "00180611E9F5" +
                "C0A80196FFFFFF00C0A801010000000000000000021F401FA500000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000";

        byte [] byteBuf = ByteUtil.hexStringToBytes(strBuf);
        DataIPInfo dataIPInfo = new DataIPInfo(byteBuf);

        Log.e(TAG,"Run ToByteBuffer Test");

       // Log.e(TAG,"Buffer "+strBuf);
       byte[] byteBuf2 = dataIPInfo.toBytes();
       String strBuf2 = ByteUtil.bytesToHexString(byteBuf2);
       // Log.e(TAG,"Buffer "+strBuf2);

        if(strBuf.equals(strBuf2)){
            Log.e(TAG, "ToByteBuffer Test OK");
        }else {
            Log.e(TAG, "ToByteBuffer Test NG");
        }
    }
}
