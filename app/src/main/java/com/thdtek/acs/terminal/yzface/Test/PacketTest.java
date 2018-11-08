package com.thdtek.acs.terminal.yzface.Test;

import android.util.Log;

import com.thdtek.acs.terminal.yzface.Message.ByteUtil;
import com.thdtek.acs.terminal.yzface.Message.Message;
import com.thdtek.acs.terminal.yzface.Message.Packet;


public class PacketTest {
    private final static String TAG = "PacketTest";

    public static Packet TransformTestX() {
        String strBuf = "7E11A2F70E4D432D35383132543137313030343138FFFFFFFF31FE000000008900180611E9F5" +
                "C0A80196FFFFFF00C0A801010000000000000000021F401FA500000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "00000000000000000000000000000000007B7E";

        String strMagicNumberS = "7E";
        String strToken = "7F02F70E";
        String strDeviceSN = "4D432D35383132543137313030343138";
        String strPassword = "7F7E7F01";

        String strMessage = "31FE000000008900180611E9F5" +
                "C0A80196FFFFFF00C0A801010000000000000000021F401FA500000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000";
        String strVerify = "7B";
        String strMagicNumberE = "7E";

        byte[] byteBuf = ByteUtil.hexStringToBytes(strBuf);

        Log.e(TAG, "Run ConstructionByBuffer Send Packet Test");
        Log.e(TAG, "buffer " + strBuf);

        Packet packet = null;
        try {
            packet = new Packet(byteBuf, true);

            packet.setPassword(ByteUtil.hexStringToBytes(strPassword));
            packet.setToken(ByteUtil.hexStringToBytes(strToken));
            packet.setVerify(new byte[1]);

            String str = ByteUtil.bytesToHexString(packet.toBytes(true));
            Log.e(TAG, "bufferEX " + str);

            Log.e(TAG, "Packet.MagicNumberS =" + ByteUtil.bytesToHexString(packet.getMagicNumberS()).equals(strMagicNumberS));
            Log.e(TAG, "Packet.Token  =" + ByteUtil.bytesToHexString(packet.getToken()).equals(strToken));
            Log.e(TAG, "Packet.DeviceSN  =" + ByteUtil.bytesToHexString(packet.getDeviceSN()).equals(strDeviceSN));
            Log.e(TAG, "Packet.Password =" + ByteUtil.bytesToHexString(packet.getPassword()).equals(strPassword));

            Log.e(TAG, "Packet.Message =" + ByteUtil.bytesToHexString(packet.getMsg().toBytes()).equals(strMessage));
          /*
            Log.e(TAG, "  Packet.Message.Category =" + ByteUtil.bytesToHexString(packet.getMsg().getCategory()));
            Log.e(TAG, "  Packet.Message.Command =" + ByteUtil.bytesToHexString(packet.getMsg().getCommand()));
            Log.e(TAG, "  Packet.Message.Parameter =" + ByteUtil.bytesToHexString(packet.getMsg().getParameter()));
            Log.e(TAG, "  Packet.Message.DataLength =" + ByteUtil.bytesToHexString(packet.getMsg().getDataLength()));
            Log.e(TAG, "  Packet.Message.Data =" + ByteUtil.bytesToHexString(packet.getMsg().getData()));
          */
            Log.e(TAG, "Packet.Verify =" + ByteUtil.bytesToHexString(packet.getVerify()).equals(strVerify));
            Log.e(TAG, "Packet.MagicNumberE =" + ByteUtil.bytesToHexString(packet.getMagicNumberE()).equals(strMagicNumberE));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  packet;
    }

    public  static void TransformTest3()
    {
                       //7E30303030303030303030303030303030FFFFFFFF1E7E97DB01FE00000000026841B47E
        String strBuf  ="7E30303030303030303030303030303030FFFFFFFF1E7F0197DB01FE00000000026841B47E";

        String strMagicNumberS                   = "7E";
        String strToken                       = "1E7E97DB";
        String strDeviceSN                    = "30303030303030303030303030303030";
        String strPassword                  = "FFFFFFFF";
        String strMessage                   ="01FE00000000026841";
        String strVerify                       = "B4";
        String strMagicNumberE                   = "7E";

        byte [] byteBuf = ByteUtil.hexStringToBytes(strBuf);

        Log.e(TAG,"Run TransformTest3  Packet Test");
        Log.e(TAG,"buffer   "+strBuf);

        try {
            Packet packet = new Packet();
            packet.setDeviceSN(ByteUtil.hexStringToBytes(strDeviceSN));
            packet.setPassword(ByteUtil.hexStringToBytes(strPassword));
            packet.setToken(ByteUtil.hexStringToBytes(strToken));
            Message message = new Message(ByteUtil.hexStringToBytes(strMessage));
            packet.setMsg(message);
            packet.setVerify(new byte[1]);

            String str = ByteUtil.bytesToHexString(packet.toBytes(false));
            Log.e(TAG,"bufferEX "+str);

            Log.e(TAG, "Packet.MagicNumberS =" + ByteUtil.bytesToHexString(packet.getMagicNumberS()).equals(strMagicNumberS));
            Log.e(TAG, "Packet.Token  =" + ByteUtil.bytesToHexString(packet.getToken()).equals(strToken));
            Log.e(TAG, "Packet.DeviceSN  =" + ByteUtil.bytesToHexString(packet.getDeviceSN()).equals(strDeviceSN));
            Log.e(TAG, "Packet.Password =" + ByteUtil.bytesToHexString(packet.getPassword()).equals(strPassword));

            Log.e(TAG, "Packet.Message =" + ByteUtil.bytesToHexString(packet.getMsg().toBytes()).equals(strMessage));
          /*
            Log.e(TAG, "  Packet.Message.Category =" + ByteUtil.bytesToHexString(packet.getMsg().getCategory()));
            Log.e(TAG, "  Packet.Message.Command =" + ByteUtil.bytesToHexString(packet.getMsg().getCommand()));
            Log.e(TAG, "  Packet.Message.Parameter =" + ByteUtil.bytesToHexString(packet.getMsg().getParameter()));
            Log.e(TAG, "  Packet.Message.DataLength =" + ByteUtil.bytesToHexString(packet.getMsg().getDataLength()));
            Log.e(TAG, "  Packet.Message.Data =" + ByteUtil.bytesToHexString(packet.getMsg().getData()));
          */
            Log.e(TAG, "Packet.Verify =" + ByteUtil.bytesToHexString(packet.getVerify()).equals(strVerify));
            Log.e(TAG, "Packet.MagicNumberE =" + ByteUtil.bytesToHexString(packet.getMagicNumberE()).equals(strMagicNumberE));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public  static void TransformTest2()
    {
                       //7E30303030303030303030303030303030FFFFFFFF1E7E97DB01FE00000000026841B47E
        String strBuf  ="7E30303030303030303030303030303030FFFFFFFF1E7F0197DB01FE00000000026841B47E";

        String strMagicNumberS                   = "7E";
        String strToken                       = "1E7E97DB";
        String strDeviceSN                    = "30303030303030303030303030303030";
        String strPassword                  = "FFFFFFFF";
        String strMessage                   ="01FE00000000026841";
        String strVerify                       = "B4";
        String strMagicNumberE                   = "7E";

        byte [] byteBuf = ByteUtil.hexStringToBytes(strBuf);

        Log.e(TAG,"Run TransformTest2 Receive Packet Test");
        Log.e(TAG,"buffer   "+strBuf);

        try {
            Packet packet = new Packet(byteBuf, false);

            String str = ByteUtil.bytesToHexString(packet.toBytes(false));
            Log.e(TAG,"bufferEX "+str);

            Log.e(TAG, "Packet.MagicNumberS =" + ByteUtil.bytesToHexString(packet.getMagicNumberS()).equals(strMagicNumberS));
            Log.e(TAG, "Packet.Token  =" + ByteUtil.bytesToHexString(packet.getToken()).equals(strToken));
            Log.e(TAG, "Packet.DeviceSN  =" + ByteUtil.bytesToHexString(packet.getDeviceSN()).equals(strDeviceSN));
            Log.e(TAG, "Packet.Password =" + ByteUtil.bytesToHexString(packet.getPassword()).equals(strPassword));

            Log.e(TAG, "Packet.Message =" + ByteUtil.bytesToHexString(packet.getMsg().toBytes()).equals(strMessage));
          /*
            Log.e(TAG, "  Packet.Message.Category =" + ByteUtil.bytesToHexString(packet.getMsg().getCategory()));
            Log.e(TAG, "  Packet.Message.Command =" + ByteUtil.bytesToHexString(packet.getMsg().getCommand()));
            Log.e(TAG, "  Packet.Message.Parameter =" + ByteUtil.bytesToHexString(packet.getMsg().getParameter()));
            Log.e(TAG, "  Packet.Message.DataLength =" + ByteUtil.bytesToHexString(packet.getMsg().getDataLength()));
            Log.e(TAG, "  Packet.Message.Data =" + ByteUtil.bytesToHexString(packet.getMsg().getData()));
          */
            Log.e(TAG, "Packet.Verify =" + ByteUtil.bytesToHexString(packet.getVerify()).equals(strVerify));
            Log.e(TAG, "Packet.MagicNumberE =" + ByteUtil.bytesToHexString(packet.getMagicNumberE()).equals(strMagicNumberE));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public  static void TransformTest()
    {
        String strBuf  ="7E7F0202F70E4D432D353831325431373130303431387F027F017F020131FE000000008900180611E9F5C0A80196FFFFFF00C0A801010000000000000000021F401FA500000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000CA7E";

        String strMagicNumberS                   = "7E";
        String strToken                       = "7F02F70E";
        String strDeviceSN                    = "4D432D35383132543137313030343138";
        String strPassword                  = "7F7E7F01";

        String strMessage  ="31FE000000008900180611E9F5C0A80196FFFFFF00C0A801010000000000000000021F401FA500000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

        String strVerify                       = "CA";
        String strMagicNumberE                   = "7E";

        byte [] byteBuf = ByteUtil.hexStringToBytes(strBuf);

        Log.e(TAG,"Run TransformTest Send Packet Test");
        Log.e(TAG,"buffer   "+strBuf);

        try {
            Packet packet = new Packet(byteBuf, true);

           // packet.setPassword(ByteUtil.hexStringToBytes(strPassword));
           // packet.setToken(ByteUtil.hexStringToBytes(strToken));
           // packet.setVerify(new byte[1]);

            String str = ByteUtil.bytesToHexString(packet.toBytes(true));
            Log.e(TAG,"bufferEX "+str);

            Log.e(TAG, "Packet.MagicNumberS =" + ByteUtil.bytesToHexString(packet.getMagicNumberS()).equals(strMagicNumberS));
            Log.e(TAG, "Packet.Token  =" + ByteUtil.bytesToHexString(packet.getToken()).equals(strToken));
            Log.e(TAG, "Packet.DeviceSN  =" + ByteUtil.bytesToHexString(packet.getDeviceSN()).equals(strDeviceSN));
            Log.e(TAG, "Packet.Password =" + ByteUtil.bytesToHexString(packet.getPassword()).equals(strPassword));

            Log.e(TAG, "Packet.Message =" + ByteUtil.bytesToHexString(packet.getMsg().toBytes()).equals(strMessage));
          /*
            Log.e(TAG, "  Packet.Message.Category =" + ByteUtil.bytesToHexString(packet.getMsg().getCategory()));
            Log.e(TAG, "  Packet.Message.Command =" + ByteUtil.bytesToHexString(packet.getMsg().getCommand()));
            Log.e(TAG, "  Packet.Message.Parameter =" + ByteUtil.bytesToHexString(packet.getMsg().getParameter()));
            Log.e(TAG, "  Packet.Message.DataLength =" + ByteUtil.bytesToHexString(packet.getMsg().getDataLength()));
            Log.e(TAG, "  Packet.Message.Data =" + ByteUtil.bytesToHexString(packet.getMsg().getData()));
          */
            Log.e(TAG, "Packet.Verify =" + ByteUtil.bytesToHexString(packet.getVerify()).equals(strVerify));
            Log.e(TAG, "Packet.MagicNumberE =" + ByteUtil.bytesToHexString(packet.getMagicNumberE()).equals(strMagicNumberE));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public  static void CloneTest()
    {
        String strBuf  ="7E11A2F70E4D432D35383132543137313030343138FFFFFFFF31FE000000008900180611E9F5" +
                "C0A80196FFFFFF00C0A801010000000000000000021F401FA500000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "00000000000000000000000000000000007B7E";

        String strMagicNumberS                   = "7E";
        String strToken                       = "11A2F70E";
        String strDeviceSN                    = "4D432D35383132543137313030343138";
        String strPassword                  = "FFFFFFFF";

        String strMessage  ="31FE000000008900180611E9F5" +
                "C0A80196FFFFFF00C0A801010000000000000000021F401FA500000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000";
        String strVerify                       = "7B";
        String strMagicNumberE                   = "7E";

        byte [] byteBuf = ByteUtil.hexStringToBytes(strBuf);

        Log.e(TAG,"Run CloneTest Send Packet Test");
        //Log.e(TAG,"buffer "+strBuf);

        try {
            Packet packet = new Packet(byteBuf, true);

            Log.e(TAG, "Packet.MagicNumberS =" + ByteUtil.bytesToHexString(packet.getMagicNumberS()));
            Log.e(TAG, "Packet.Token  =" + ByteUtil.bytesToHexString(packet.getToken()));
            Log.e(TAG, "Packet.DeviceSN  =" + ByteUtil.bytesToHexString(packet.getDeviceSN()));
            Log.e(TAG, "Packet.Password =" + ByteUtil.bytesToHexString(packet.getPassword()));

            Log.e(TAG, "Packet.Message =" + ByteUtil.bytesToHexString(packet.getMsg().toBytes()));
          /*
            Log.e(TAG, "  Packet.Message.Category =" + ByteUtil.bytesToHexString(packet.getMsg().getCategory()));
            Log.e(TAG, "  Packet.Message.Command =" + ByteUtil.bytesToHexString(packet.getMsg().getCommand()));
            Log.e(TAG, "  Packet.Message.Parameter =" + ByteUtil.bytesToHexString(packet.getMsg().getParameter()));
            Log.e(TAG, "  Packet.Message.DataLength =" + ByteUtil.bytesToHexString(packet.getMsg().getDataLength()));
            Log.e(TAG, "  Packet.Message.Data =" + ByteUtil.bytesToHexString(packet.getMsg().getData()));
          */
            Log.e(TAG, "Packet.Verify =" + ByteUtil.bytesToHexString(packet.getVerify()));
            Log.e(TAG, "Packet.NumberE =" + ByteUtil.bytesToHexString(packet.getMagicNumberE()));

            Packet packet2 = (Packet)packet.clone();
            Log.e(TAG, "Packet2.MagicNumberS =" + ByteUtil.bytesToHexString(packet2.getMagicNumberS()));
            Log.e(TAG, "Packet2Token  =" + ByteUtil.bytesToHexString(packet2.getToken()));
            Log.e(TAG, "Packet2.DeviceSN  =" + ByteUtil.bytesToHexString(packet2.getDeviceSN()));
            Log.e(TAG, "Packet2.Password =" + ByteUtil.bytesToHexString(packet2.getPassword()));

            Log.e(TAG, "Packet2.Message =" + ByteUtil.bytesToHexString(packet2.getMsg().toBytes()));
          /*
            Log.e(TAG, "  Packet.Message.Category =" + ByteUtil.bytesToHexString(packet.getMsg().getCategory()));
            Log.e(TAG, "  Packet.Message.Command =" + ByteUtil.bytesToHexString(packet.getMsg().getCommand()));
            Log.e(TAG, "  Packet.Message.Parameter =" + ByteUtil.bytesToHexString(packet.getMsg().getParameter()));
            Log.e(TAG, "  Packet.Message.DataLength =" + ByteUtil.bytesToHexString(packet.getMsg().getDataLength()));
            Log.e(TAG, "  Packet.Message.Data =" + ByteUtil.bytesToHexString(packet.getMsg().getData()));
          */
            Log.e(TAG, "Packet2.Verify =" + ByteUtil.bytesToHexString(packet2.getVerify()));
            Log.e(TAG, "Packet2.MagicNumberE =" + ByteUtil.bytesToHexString(packet2.getMagicNumberE()));


            packet2.setPassword(new byte[4]);
            packet2.setVerify(new byte[1]);

            Log.e(TAG, "Packet2.MagicNumberS =" + ByteUtil.bytesToHexString(packet2.getMagicNumberS()));
            Log.e(TAG, "Packet2Token  =" + ByteUtil.bytesToHexString(packet2.getToken()));
            Log.e(TAG, "Packet2.DeviceSN  =" + ByteUtil.bytesToHexString(packet2.getDeviceSN()));
            Log.e(TAG, "Packet2.Password =" + ByteUtil.bytesToHexString(packet2.getPassword()));

            Log.e(TAG, "Packet2.Message =" + ByteUtil.bytesToHexString(packet2.getMsg().toBytes()));
          /*
            Log.e(TAG, "  Packet.Message.Category =" + ByteUtil.bytesToHexString(packet.getMsg().getCategory()));
            Log.e(TAG, "  Packet.Message.Command =" + ByteUtil.bytesToHexString(packet.getMsg().getCommand()));
            Log.e(TAG, "  Packet.Message.Parameter =" + ByteUtil.bytesToHexString(packet.getMsg().getParameter()));
            Log.e(TAG, "  Packet.Message.DataLength =" + ByteUtil.bytesToHexString(packet.getMsg().getDataLength()));
            Log.e(TAG, "  Packet.Message.Data =" + ByteUtil.bytesToHexString(packet.getMsg().getData()));
          */
            Log.e(TAG, "Packet2.Verify =" + ByteUtil.bytesToHexString(packet2.getVerify()));
            Log.e(TAG, "Packet2.MagicNumberE =" + ByteUtil.bytesToHexString(packet2.getMagicNumberE()));

            byte[] test = packet2.toBytes(true);
            String strBuf2 = ByteUtil.bytesToHexString(test);
            Log.e(TAG,strBuf2);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public  static void ConstructionByBuffer()
    {
        String strBuf  ="7E11A2F70E4D432D35383132543137313030343138FFFFFFFF31FE000000008900180611E9F5" +
                "C0A80196FFFFFF00C0A801010000000000000000021F401FA500000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "00000000000000000000000000000000007B7E";

        String strMagicNumberS                   = "7E";
        String strToken                       = "11A2F70E";
        String strDeviceSN                    = "4D432D35383132543137313030343138";
        String strPassword                  = "FFFFFFFF";

        String strMessage  ="31FE000000008900180611E9F5" +
                "C0A80196FFFFFF00C0A801010000000000000000021F401FA500000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000";
        String strVerify                       = "7B";
        String strMagicNumberE                   = "7E";

        byte [] byteBuf = ByteUtil.hexStringToBytes(strBuf);

        Log.e(TAG,"Run ConstructionByBuffer Send Packet Test");
        //Log.e(TAG,"buffer "+strBuf);

        try {
            Packet packet = new Packet(byteBuf, true);
            Log.e(TAG, "Packet.MagicNumberS =" + ByteUtil.bytesToHexString(packet.getMagicNumberS()).equals(strMagicNumberS));
            Log.e(TAG, "Packet.Token  =" + ByteUtil.bytesToHexString(packet.getToken()).equals(strToken));
            Log.e(TAG, "Packet.DeviceSN  =" + ByteUtil.bytesToHexString(packet.getDeviceSN()).equals(strDeviceSN));
            Log.e(TAG, "Packet.Password =" + ByteUtil.bytesToHexString(packet.getPassword()).equals(strPassword));

            Log.e(TAG, "Packet.Message =" + ByteUtil.bytesToHexString(packet.getMsg().toBytes()).equals(strMessage));
          /*
            Log.e(TAG, "  Packet.Message.Category =" + ByteUtil.bytesToHexString(packet.getMsg().getCategory()));
            Log.e(TAG, "  Packet.Message.Command =" + ByteUtil.bytesToHexString(packet.getMsg().getCommand()));
            Log.e(TAG, "  Packet.Message.Parameter =" + ByteUtil.bytesToHexString(packet.getMsg().getParameter()));
            Log.e(TAG, "  Packet.Message.DataLength =" + ByteUtil.bytesToHexString(packet.getMsg().getDataLength()));
            Log.e(TAG, "  Packet.Message.Data =" + ByteUtil.bytesToHexString(packet.getMsg().getData()));
          */
            Log.e(TAG, "Packet.Verify =" + ByteUtil.bytesToHexString(packet.getVerify()).equals(strVerify));
            Log.e(TAG, "Packet.MagicNumberE =" + ByteUtil.bytesToHexString(packet.getMagicNumberE()).equals(strMagicNumberE));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public  static void ConstructionByBuffer2()
    {
        String strBuf  ="7E4D432D35383132543137313030343138FFFFFFFF11A2F70E31FE000000008900180611E9F5" +
                "C0A80196FFFFFF00C0A801010000000000000000021F401FA500000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "00000000000000000000000000000000007B7E";

        String strMagicNumberS                   = "7E";
        String strToken                       = "11A2F70E";
        String strDeviceSN                    = "4D432D35383132543137313030343138";
        String strPassword                  = "FFFFFFFF";

        String strMessage  ="31FE000000008900180611E9F5" +
                "C0A80196FFFFFF00C0A801010000000000000000021F401FA500000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000";
        String strVerify                       = "7B";
        String strMagicNumberE                   = "7E";

        byte [] byteBuf = ByteUtil.hexStringToBytes(strBuf);

        Log.e(TAG,"Run ConstructionByBuffer2 receive Packet Test");
        //Log.e(TAG,"buffer "+strBuf);

        try {
            Packet packet = new Packet(byteBuf, false);
            Log.e(TAG, "Packet.MagicNumberS =" + ByteUtil.bytesToHexString(packet.getMagicNumberS()).equals(strMagicNumberS));
            Log.e(TAG, "Packet.DeviceSN  =" + ByteUtil.bytesToHexString(packet.getDeviceSN()).equals(strDeviceSN));
            Log.e(TAG, "Packet.Password =" + ByteUtil.bytesToHexString(packet.getPassword()).equals(strPassword));
            Log.e(TAG, "Packet.Token  =" + ByteUtil.bytesToHexString(packet.getToken()).equals(strToken));

            Log.e(TAG, "Packet.Message =" + ByteUtil.bytesToHexString(packet.getMsg().toBytes()).equals(strMessage));
          /*
            Log.e(TAG, "  Packet.Message.Category =" + ByteUtil.bytesToHexString(packet.getMsg().getCategory()));
            Log.e(TAG, "  Packet.Message.Command =" + ByteUtil.bytesToHexString(packet.getMsg().getCommand()));
            Log.e(TAG, "  Packet.Message.Parameter =" + ByteUtil.bytesToHexString(packet.getMsg().getParameter()));
            Log.e(TAG, "  Packet.Message.DataLength =" + ByteUtil.bytesToHexString(packet.getMsg().getDataLength()));
            Log.e(TAG, "  Packet.Message.Data =" + ByteUtil.bytesToHexString(packet.getMsg().getData()));
          */
            Log.e(TAG, "Packet.Verify =" + ByteUtil.bytesToHexString(packet.getVerify()).equals(strVerify));
            Log.e(TAG, "Packet.MagicNumberE =" + ByteUtil.bytesToHexString(packet.getMagicNumberE()).equals(strMagicNumberE));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public  static void ToByteBuffer()
    {
        String strBuf  ="7E11A2F70E4D432D35383132543137313030343138FFFFFFFF31FE000000008900180611E9F5" +
                "C0A80196FFFFFF00C0A801010000000000000000021F401FA500000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "00000000000000000000000000000000007B7E";

        byte [] byteBuf = ByteUtil.hexStringToBytes(strBuf);

        Log.e(TAG,"Run ToByteBuffer Test");

        try {
            Packet packet = new Packet(byteBuf, true);
            byte[] byteBuf2 = packet.toBytes(true);

            //Log.e(TAG,"Buffer "+strBuf);
            String strBuf2 = ByteUtil.bytesToHexString(byteBuf2);
            //Log.e(TAG,"Buffer "+strBuf2);

            if(strBuf.equals(strBuf2)){
                Log.e(TAG, "ToByteBuffer Send Packet Test OK");
            }else {
                Log.e(TAG, "ToByteBuffer Send Packet Test NG");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public  static void ToByteBuffer2()
    {
        String strBuf  ="7E4D432D35383132543137313030343138FFFFFFFF11A2F70E31FE000000008900180611E9F5" +
                "C0A80196FFFFFF00C0A801010000000000000000021F401FA500000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "0000000000000000000000000000000000000000000000000000000000000000000000000000" +
                "00000000000000000000000000000000007B7E";

        byte [] byteBuf = ByteUtil.hexStringToBytes(strBuf);

        Log.e(TAG,"Run ToByteBuffer Test");

        try {
            Packet packet = new Packet(byteBuf, false);
            byte[] byteBuf2 = packet.toBytes(false);

            //Log.e(TAG,"Buffer "+strBuf);
            String strBuf2 = ByteUtil.bytesToHexString(byteBuf2);
            //Log.e(TAG,"Buffer "+strBuf2);

            if(strBuf.equals(strBuf2)){
                Log.e(TAG, "ToByteBuffer receive Packet Test OK");
            }else {
                Log.e(TAG, "ToByteBuffer receive Packet Test NG");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
