package com.thdtek.acs.terminal.thread;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.SystemClock;
import android.util.Log;


import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.bean.HardwareStatusEvent;
import com.thdtek.acs.terminal.bean.UsbEvent;
import com.thdtek.acs.terminal.util.ByteFormatTransferUtils;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Time:2018/11/9
 * User:lizhen
 * Description:
 */

public class UsbThread extends Thread {
    private static final String TAG = UsbThread.class.getSimpleName();

    private static final String NUMBER_CONTINUE = "000000000000000000";
    private static final String NUMBER_1 = "00001E000000000000";
    private static final String NUMBER_2 = "00001F000000000000";
    private static final String NUMBER_3 = "000020000000000000";
    private static final String NUMBER_4 = "000021000000000000";
    private static final String NUMBER_5 = "000022000000000000";
    private static final String NUMBER_6 = "000023000000000000";
    private static final String NUMBER_7 = "000024000000000000";
    private static final String NUMBER_8 = "000025000000000000";
    private static final String NUMBER_9 = "000026000000000000";
    private static final String NUMBER_0 = "000027000000000000";
    private static final String NUMBER_LEFT = "020025000000000000";
    private static final String NUMBER_RIGHT = "020020000000000000";

    private static final int USB_KEY_BOARD_PRODUCTID = 48066;
    private static final int USB_KEY_BOARD_VENDORID = 43693;

    private int current_connect_count = 0;
    private boolean mLoop = true;
    private UsbEndpoint mEndpoint;
    private UsbDeviceConnection mUsbDeviceConnection;

    @Override
    public void run() {
        super.run();
        mUsbDeviceConnection = initUsb();
        while (mLoop) {
            try {
                byte[] bytes = new byte[9];
                if (mUsbDeviceConnection == null) {
                    mUsbDeviceConnection = initUsb();
                    if (mUsbDeviceConnection == null) {
                        SystemClock.sleep(10000);
                        continue;
                    }
                }
                current_connect_count = 0;
                int i = mUsbDeviceConnection.bulkTransfer(mEndpoint, bytes, bytes.length, 5000);
                if (i == -1) {
                    EventBus.getDefault().post(new HardwareStatusEvent(HardwareStatusEvent.HARDWARE_STATUS_NUMBER, HardwareStatusEvent.HARDWARE_STATUS_SUCCESS, HardwareStatusEvent.HARDWARE_STATUS_READ_FAIL));
                    Log.d(TAG, "====== 清空数据");
                    continue;
                }
                String msg = ByteFormatTransferUtils.bytesToHexStringNoSpace(bytes);
                parseMsg(msg);
                EventBus.getDefault().post(new HardwareStatusEvent(HardwareStatusEvent.HARDWARE_STATUS_NUMBER, HardwareStatusEvent.HARDWARE_STATUS_SUCCESS, HardwareStatusEvent.HARDWARE_STATUS_READ_SUCCESS));

            } catch (Exception e) {
                LogUtils.e(TAG, "========== UsbThread error = " + e.getMessage());
                SystemClock.sleep(10000);
            }
        }
        if (mUsbDeviceConnection != null) {
            mUsbDeviceConnection.close();
        }
        LogUtils.e(TAG, "========== UsbThread 完结撒花 ==========");
    }

    private UsbDeviceConnection initUsb() {
        UsbManager manager = (UsbManager) MyApplication.getContext().getSystemService(Context.USB_SERVICE);
        if (manager == null) {
            LogUtils.e(TAG, "====== initUsb 失败,manager == null,return");
            EventBus.getDefault().post(new HardwareStatusEvent(HardwareStatusEvent.HARDWARE_STATUS_NUMBER, HardwareStatusEvent.HARDWARE_STATUS_FAIL, HardwareStatusEvent.HARDWARE_STATUS_READ_FAIL));

            return null;
        }
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Collection<UsbDevice> values = deviceList.values();
        ArrayList<UsbDevice> list = new ArrayList<>(values);
        int usbDeviceIndex = -1;
        int usbInterfaceIndex = 0;
        int usbEndpointIndex = 0;
        for (int i = 0; i < list.size(); i++) {
            UsbDevice usbDevice = list.get(i);
//            LogUtils.d(TAG, "usbName = " + usbDevice.getDeviceName()
////                    + " interface = " + stringBuilder.toString()
//                    + " deviceId = " + usbDevice.getDeviceId()
//                    + " getVendorId = " + usbDevice.getVendorId()
//                    + " getProductName = " + usbDevice.getProductName()
//                    + " getProductId = " + usbDevice.getProductId());
            if (usbDevice.getProductId() == 48066 && usbDevice.getVendorId() == 43693 && "KeyBoard".equals(usbDevice.getProductName())) {
                usbDeviceIndex = i;
            }
        }
        //获取小键盘的device
        if (usbDeviceIndex == -1) {
            LogUtils.d(TAG, "====== 没有找到数字小键盘");
            EventBus.getDefault().post(new HardwareStatusEvent(HardwareStatusEvent.HARDWARE_STATUS_NUMBER, HardwareStatusEvent.HARDWARE_STATUS_FAIL, HardwareStatusEvent.HARDWARE_STATUS_READ_FAIL));
            return null;
        }
        LogUtils.d(TAG, "====== 找到数字小键盘 = " + usbDeviceIndex);
        UsbDevice usbDevice = list.get(usbDeviceIndex);
        LogUtils.d(TAG, "====== 连接USB ");
        UsbDeviceConnection connection = manager.openDevice(usbDevice);

        LogUtils.d(TAG, "====== 找到数字小键盘接口 = " + usbInterfaceIndex);
        UsbInterface anInterface = usbDevice.getInterface(usbInterfaceIndex);
        connection.claimInterface(anInterface, true);

        LogUtils.d(TAG, "====== 找到数字小键盘 Endpoint = " + usbEndpointIndex);
        mEndpoint = anInterface.getEndpoint(usbEndpointIndex);
        EventBus.getDefault().post(new HardwareStatusEvent(HardwareStatusEvent.HARDWARE_STATUS_NUMBER, HardwareStatusEvent.HARDWARE_STATUS_SUCCESS, HardwareStatusEvent.HARDWARE_STATUS_READ_FAIL));
        return connection;
    }

    public void parseMsg(String msg) {
        switch (msg) {
            case NUMBER_0:
                LogUtils.d(TAG, "usb msg = 0");
                EventBus.getDefault().post(new UsbEvent("0"));
                break;
            case NUMBER_1:
                LogUtils.d(TAG, "usb msg = 1");
                EventBus.getDefault().post(new UsbEvent("1"));
                break;
            case NUMBER_2:
                LogUtils.d(TAG, "usb msg = 2");
                EventBus.getDefault().post(new UsbEvent("2"));
                break;
            case NUMBER_3:
                LogUtils.d(TAG, "usb msg = 3");
                EventBus.getDefault().post(new UsbEvent("3"));
                break;
            case NUMBER_4:
                LogUtils.d(TAG, "usb msg = 4");
                EventBus.getDefault().post(new UsbEvent("4"));
                break;
            case NUMBER_5:
                LogUtils.d(TAG, "usb msg = 5");
                EventBus.getDefault().post(new UsbEvent("5"));
                break;
            case NUMBER_6:
                LogUtils.d(TAG, "usb msg = 6");
                EventBus.getDefault().post(new UsbEvent("6"));
                break;
            case NUMBER_7:
                LogUtils.d(TAG, "usb msg = 7");
                EventBus.getDefault().post(new UsbEvent("7"));
                break;
            case NUMBER_8:
                LogUtils.d(TAG, "usb msg = 8");
                EventBus.getDefault().post(new UsbEvent("8"));
                break;
            case NUMBER_9:
                LogUtils.d(TAG, "usb msg = 9");
                EventBus.getDefault().post(new UsbEvent("9"));
                break;
            case NUMBER_LEFT:
                LogUtils.d(TAG, "usb msg = *");
                EventBus.getDefault().post(new UsbEvent("*"));
                break;
            case NUMBER_RIGHT:
                LogUtils.d(TAG, "usb msg = #");
                EventBus.getDefault().post(new UsbEvent("#"));
                break;
            case NUMBER_CONTINUE:
                LogUtils.d(TAG, "usb msg = --");
                break;
            default:
                break;
        }
    }


    public void close() {
        mLoop = false;
        if (mUsbDeviceConnection != null) {
            mUsbDeviceConnection.close();
        }
    }
}
