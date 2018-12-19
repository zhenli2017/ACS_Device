package com.thdtek.acs.terminal.thread;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.thdtek.acs.terminal.util.ByteFormatTransferUtils;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SwitchConst;
import com.thdtek.acs.terminal.util.WGUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Time:2018/8/22
 * User:lizhen
 * Description:
 */

public class SerialThread extends Thread {

    private static final String TAG = SerialThread.class.getSimpleName();

    private FileOutputStream mOutputStream;
    private FileInputStream mInputStream;

    private byte[] msgSET = {(byte) 0x0F1, 0x01, 0x05, 0x01, (byte) 0xFA, 0x07, (byte) 0xD0, 0x55, (byte) 0xAA};
    private byte[] msgWG0 = {(byte) 0x0F1, 0x02, 0x05, 0x00, 0x12, 0x34, (byte) 0xAA, 0x55, (byte) 0xAA};
    private byte[] msgWG1 = {(byte) 0x0F1, 0x03, 0x05, 0x00, 0x12, 0x34, (byte) 0xAA, 0x55, (byte) 0xAA};
    private byte[] msgWG66H = {(byte) 0x0F1, 0x04, 0x05, 0x00, 0x12, 0x34, (byte) 0xAA, 0x55, (byte) 0xAA};
    private byte[] msgWG66P = {(byte) 0x0F1, 0x05, 0x05, 0x00, 0x12, 0x34, (byte) 0xAA, 0x55, (byte) 0xAA};
    private byte[] msgGPIO0 = {(byte) 0x0F2, 0x01, 0x05, 0x00, 0x12, 0x34, (byte) 0xAA, 0x55, (byte) 0xAA};
    private byte[] msgGPIO1 = {(byte) 0x0F2, 0x01, 0x05, 0x01, 0x12, 0x34, (byte) 0xAA, 0x55, (byte) 0xAA};
    private byte[] msgGetVer = {(byte) 0x0F0, 0x02, 0x05, 0x00, 0x00, 0x00, 0x00, 0x55, (byte) 0xAA};
    private byte[] msgGetSig = {(byte) 0x0F3, 0x01, 0x05, 0x01, 0x23, 0x45, 0x67, 0x55, (byte) 0xAA};

    private byte[] msgGetSigRom = {(byte) 0x0F3, 0x01, 0x05, 0x01, 0x23, 0x45, 0x67, 0x55, (byte) 0xAA};
    private byte[] msgReceve = {(byte) 0x0F0, (byte) 0x82, 0x05, 0x00, 0x00, 0x00, 0x00, 0x55, (byte) 0xAA};


    byte[] msgSIDX = {(byte) 0x0F0, 0x03, 0x05, 0x00, 0x00, 0x00, 0x00, 0x55, (byte) 0xAA};
    byte[] msgRIDX = {(byte) 0x0F0, (byte) 0x83, 0x05, 0x00, 0x00, 0x00, 0x00, 0x55, (byte) 0xAA};

    byte[] mcuid = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c};

    String strMCUUID = "";

    private int sigToken = 0;
    private int fwVer = 0;

    private int MAX_COUNT = 10;
    private int WAIT_TIME = 100;
    private int mSignTokenCount = 0;
    private int mFwVercount = 0;
    private int mMcuUidCount = 0;
    private android_serialport_api.SerialPort mSerialPort;
    private android_serialport_api.SerialPort mSerialPort1;


    private SerialThread() throws IOException {
        openPort();
        openPortOpenDoor();
    }

    private static SerialThread mSerialThread;

    public static SerialThread getInstance() {
        if (mSerialThread == null) {
            synchronized (SerialThread.class) {
                if (mSerialThread == null) {
                    try {
                        mSerialThread = new SerialThread();
                        if (open) {
                            mSerialThread.start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return mSerialThread;
    }


    public void openPort() throws IOException {
        mSerialPort = new android_serialport_api.SerialPort(new File("/dev/ttyS4"), 115200, 0);
        mOutputStream = (FileOutputStream) mSerialPort.getOutputStream();
        mInputStream = (FileInputStream) mSerialPort.getInputStream();
    }

    private InputStream mTtys1InputStream;

    public void openPortOpenDoor() throws IOException {
        mSerialPort1 = new android_serialport_api.SerialPort(new File("/dev/ttyS1"), 115200, 0);
        mTtys1InputStream = mSerialPort1.getInputStream();
    }

    public android_serialport_api.SerialPort getSerialPort1() {
        return mSerialPort1;
    }

    public InputStream getTtys1InputStream() {
        return mTtys1InputStream;
    }

    private String numToHex8(int b) {
        return String.format("%02x", b);//2表示需要两个16进行数
    }

    private String numToHex32(int b) {
        return String.format("%08x", b);
    }

    /**
     * 字节数组转成16进制表示格式的字符串
     *
     * @param byteArray 需要转换的字节数组
     * @return 16进制表示格式的字符串
     **/
    public String toHexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length < 1)
            throw new IllegalArgumentException("this byteArray must not be null or empty");

        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if ((byteArray[i] & 0xff) < 0x10)//0~F前面不零
                hexString.append("0");
            hexString.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return hexString.toString().toLowerCase();
    }

    public boolean getFWVer() throws IOException, InterruptedException {
        mFwVercount = 0;
        Thread.sleep(100);
        mOutputStream.write(msgGetVer);
        Thread.sleep(100);
        Thread.sleep(100);
        while (fwVer == 0) {
            String s = numToHex32(fwVer);
            LogUtils.e(TAG, "Wait...  FW VERSION = " + s + " count = " + mFwVercount);
            Thread.sleep(WAIT_TIME);
            if (mFwVercount > MAX_COUNT) {
                return false;
            }
            mFwVercount++;
        }
        return true;
    }

    public int getSignToken(int token) throws IOException, InterruptedException {
        sigToken = 0;
        mSignTokenCount = 0;
        msgGetSigRom[3] = (byte) ((token >> 24) & 0xFF);
        msgGetSigRom[4] = (byte) ((token >> 16) & 0xFF);
        msgGetSigRom[5] = (byte) ((token >> 8) & 0xFF);
        msgGetSigRom[6] = (byte) (token & 0xFF);
        Thread.sleep(100);
        mOutputStream.write(msgGetSigRom);
        Thread.sleep(100);
        while (sigToken == 0) {
            String s = numToHex32(sigToken);
            LogUtils.e(TAG, "Wait... SIG token = " + s + " count = " + mSignTokenCount);
            Thread.sleep(WAIT_TIME);
            if (mSignTokenCount > MAX_COUNT) {
                return Const.FACE_INIT_GET_SIGN_TOKEN_FAIL;
            }
            mSignTokenCount++;
        }
        return sigToken;
    }

    public String getMCUID() {
        try {
            Thread.sleep(100);
            msgSIDX[1] = (byte) 0x03;
            mOutputStream.write(msgSIDX);
            LogUtils.d("UART", "get MCU ID0 ");
            Thread.sleep(100);
            msgSIDX[1] = (byte) 0x04;
            mOutputStream.write(msgSIDX);
            LogUtils.d("UART", "get MCU ID1 ");
            Thread.sleep(100);
            msgSIDX[1] = (byte) 0x05;
            mOutputStream.write(msgSIDX);
            Thread.sleep(100);
            LogUtils.d("UART", "get MCU ID2 ");

            while (TextUtils.isEmpty(strMCUUID)) {
                LogUtils.e(TAG, "Wait... strMCUUID = " + strMCUUID + " count = " + mMcuUidCount);
                Thread.sleep(WAIT_TIME);
                if (mMcuUidCount > MAX_COUNT) {
                    return strMCUUID;
                }
                mMcuUidCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strMCUUID;
    }

    public void relayOpen() {
        if (mOutputStream != null) {
            try {
                mOutputStream.write(msgGPIO0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

        }
    }

    public void relayClose() {
        if (mOutputStream != null) {
            try {
                mOutputStream.write(msgGPIO1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onWG26(String number) {

        byte[] bytesOne = ByteFormatTransferUtils.hexStringToBytes(number.substring(2, 4));
        byte[] bytesTwo = ByteFormatTransferUtils.hexStringToBytes(number.substring(4, 6));
        byte[] bytesThree = ByteFormatTransferUtils.hexStringToBytes(number.substring(6, 8));

        System.out.println("one = " + ByteFormatTransferUtils.bytesToHexString(bytesOne)
                + " two = " + ByteFormatTransferUtils.bytesToHexStringNoSpace(bytesTwo)
                + " three = " + ByteFormatTransferUtils.bytesToHexStringNoSpace(bytesThree));
        msgWG0[4] = bytesOne[0];
        msgWG0[5] = bytesTwo[0];
        msgWG0[6] = bytesThree[0];
//        msgWG0[6] = (byte) (carid & 0xFF);


        try {
            Thread.sleep(100);
            mOutputStream.write(msgWG0);
            Thread.sleep(100);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onWG66(String number) {

        String parseWG66 = WGUtil.parseWG66(number);

        byte[] bytesZeroH = ByteFormatTransferUtils.hexStringToBytes(parseWG66.substring(0, 2));
        byte[] bytesOneH = ByteFormatTransferUtils.hexStringToBytes(parseWG66.substring(2, 4));
        byte[] bytesTwoH = ByteFormatTransferUtils.hexStringToBytes(parseWG66.substring(4, 6));
        byte[] bytesThreeH = ByteFormatTransferUtils.hexStringToBytes(parseWG66.substring(6, 8));

        byte[] bytesZeroP = ByteFormatTransferUtils.hexStringToBytes(parseWG66.substring(8, 10));
        byte[] bytesOneP = ByteFormatTransferUtils.hexStringToBytes(parseWG66.substring(10, 12));
        byte[] bytesTwoP = ByteFormatTransferUtils.hexStringToBytes(parseWG66.substring(12, 14));
        byte[] bytesThreeP = ByteFormatTransferUtils.hexStringToBytes(parseWG66.substring(14, 16));
        LogUtils.d(TAG, "H Zero = " + ByteFormatTransferUtils.bytesToHexString(bytesZeroH)
                + " H one = " + ByteFormatTransferUtils.bytesToHexString(bytesOneH)
                + " H two = " + ByteFormatTransferUtils.bytesToHexStringNoSpace(bytesTwoH)
                + " H three = " + ByteFormatTransferUtils.bytesToHexStringNoSpace(bytesThreeH)
                + " P Zero = " + ByteFormatTransferUtils.bytesToHexString(bytesZeroP)
                + " P one = " + ByteFormatTransferUtils.bytesToHexString(bytesOneP)
                + " P two = " + ByteFormatTransferUtils.bytesToHexStringNoSpace(bytesTwoP)
                + " P three = " + ByteFormatTransferUtils.bytesToHexStringNoSpace(bytesThreeP));
        msgWG66H[3] = bytesZeroH[0];
        msgWG66H[4] = bytesOneH[0];
        msgWG66H[5] = bytesTwoH[0];
        msgWG66H[6] = bytesThreeH[0];

        msgWG66P[3] = bytesZeroP[0];
        msgWG66P[4] = bytesOneP[0];
        msgWG66P[5] = bytesTwoP[0];
        msgWG66P[6] = bytesThreeP[0];
        try {
            Thread.sleep(100);
            mOutputStream.write(msgWG66H);
            Thread.sleep(100);
            mOutputStream.write(msgWG66P);
            Thread.sleep(100);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onWG34(String number) {



        byte[] bytesZero = ByteFormatTransferUtils.hexStringToBytes(number.substring(0, 2));
        byte[] bytesOne = ByteFormatTransferUtils.hexStringToBytes(number.substring(2, 4));
        byte[] bytesTwo = ByteFormatTransferUtils.hexStringToBytes(number.substring(4, 6));
        byte[] bytesThree = ByteFormatTransferUtils.hexStringToBytes(number.substring(6, 8));

        System.out.println(
                "Zero = " + ByteFormatTransferUtils.bytesToHexString(bytesZero)
                        + " one = " + ByteFormatTransferUtils.bytesToHexString(bytesOne)
                        + " two = " + ByteFormatTransferUtils.bytesToHexStringNoSpace(bytesTwo)
                        + " three = " + ByteFormatTransferUtils.bytesToHexStringNoSpace(bytesThree));
        msgWG1[3] = bytesZero[0];
        msgWG1[4] = bytesOne[0];
        msgWG1[5] = bytesTwo[0];
        msgWG1[6] = bytesThree[0];

        try {
            Thread.sleep(100);
            mOutputStream.write(msgWG1);
            Thread.sleep(100);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public void onWG34(int carid) {
//
//        msgWG0[3] = (byte) ((carid >> 24) & 0xFF);
//        msgWG0[4] = (byte) ((carid >> 16) & 0xFF);
//        msgWG0[5] = (byte) ((carid >> 8) & 0xFF);
//        msgWG0[6] = (byte) (carid & 0xFF);
//
//
//        try {
//            Thread.sleep(100);
//            mOutputStream.write(msgWG1);
//            Thread.sleep(100);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private static boolean open = true;

    public void close(boolean closePort) {
        open = false;
        if (closePort && mSerialPort != null) {
            mSerialPort.close();
        }
        if (closePort && mSerialPort1 != null) {
            mSerialPort1.close();
        }
        interrupt();
    }

    @Override
    public void run() {
        super.run();
        LogUtils.d("UART ", "Start ReceivedThread");
        open = true;
        while (open) {
            int size;
            try {
                byte[] buffer = new byte[17];
                if (mInputStream == null) {
                    break;
                }
                size = mInputStream.read(buffer);
                if (size >= 9) {
                    if ((buffer[0] & 0xf0) == 0xf0 && buffer[8] == (byte) 0xAA) {
                        for (int i = 0; i < size; i++) {
                            msgReceve[i] = buffer[i];
                            //dataReceived.add(buffer[i]);
                            LogUtils.d("UART ", numToHex8(msgReceve[i]) + "");
                        }

                        if ((msgReceve[0] == (byte) 0xf0) && (msgReceve[1] == (byte) 0x82)) {
                            fwVer = msgReceve[4] & 0xFF |
                                    (msgReceve[3] & 0xFF) << 8;

                            LogUtils.d("UART ", "MCU FW ver:" + msgReceve[3] + "." + msgReceve[4]);

                        } else if ((msgReceve[0] == (byte) 0xf3) && (msgReceve[1] == (byte) 0x81)) {
                            sigToken = msgReceve[6] & 0xFF |
                                    (msgReceve[5] & 0xFF) << 8 |
                                    (msgReceve[4] & 0xFF) << 16 |
                                    (msgReceve[3] & 0xFF) << 24;

                            LogUtils.d("UART ", "SIG token " + numToHex32(sigToken));
                        } else if ((msgReceve[0] == (byte) 0xf0) && (msgReceve[1] == (byte) 0x83)) {
                            mcuid[0] = msgReceve[3];
                            mcuid[1] = msgReceve[4];
                            mcuid[2] = msgReceve[5];
                            mcuid[3] = msgReceve[6];
                        } else if ((msgReceve[0] == (byte) 0xf0) && (msgReceve[1] == (byte) 0x84)) {
                            mcuid[4] = msgReceve[3];
                            mcuid[5] = msgReceve[4];
                            mcuid[6] = msgReceve[5];
                            mcuid[7] = msgReceve[6];
                        } else if ((msgReceve[0] == (byte) 0xf0) && (msgReceve[1] == (byte) 0x85)) {
                            mcuid[8] = msgReceve[3];
                            mcuid[9] = msgReceve[4];
                            mcuid[10] = msgReceve[5];
                            mcuid[11] = msgReceve[6];

                            strMCUUID = toHexString(mcuid);
                            LogUtils.d(TAG, "MCU UID " + strMCUUID);
                        }
                    }
                }
                Thread.sleep(0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.getMessage();
                break;
            }
        }
        LogUtils.d(TAG, "====== SerialThread 线程结束 ======");
    }
}
