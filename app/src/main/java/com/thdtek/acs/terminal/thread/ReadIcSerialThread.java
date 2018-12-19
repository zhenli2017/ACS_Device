package com.thdtek.acs.terminal.thread;

import android.os.SystemClock;
import android.text.TextUtils;

import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.bean.ICEvent;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.face.FacePairStatus;
import com.thdtek.acs.terminal.face.FaceTempData;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.ByteFormatTransferUtils;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SoundUtil;
import com.thdtek.acs.terminal.util.WGUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;

/**
 * Time:2018/10/11
 * User:lizhen
 * Description:
 */

public class ReadIcSerialThread extends Thread {

    private static final String TAG = ReadIcSerialThread.class.getSimpleName();
    private boolean close = false;

    public ReadIcSerialThread() {
    }

    public void close() {
        close = true;
    }

    @Override
    public void run() {
        super.run();
        InputStream ttys1InputStream = SerialThread.getInstance().getTtys1InputStream();
        byte[] bytes = new byte[1024];
        LogUtils.d(TAG, "========= ReadIcSerialThread 开启 ==========");
        while (!close) {
            if (ttys1InputStream == null) {
                break;
            }
            try {
                int read = ttys1InputStream.read(bytes);
                if (read == -1) {
                    break;
                }
                byte[] newByte = new byte[read];
                System.arraycopy(bytes, 0, newByte, 0, read);
                if (FaceTempData.getInstance().isHaveICMessage()) {
                    LogUtils.d(TAG, "当前已经读取到IC卡数据,正在匹配,本次读取跳过 = ");
                    continue;
                }
                String icNumber = ByteFormatTransferUtils.bytesToHexStringNoSpace(newByte);
                String replaceAll = icNumber.replaceAll(" ", "");
                if (TextUtils.isEmpty(replaceAll)) {
                    continue;
                }
                LogUtils.d(TAG, "ReadIcSerialThread msg = " + icNumber);
                if (icNumber.length() < 26) {
                    continue;
                }
                String substring = icNumber.substring(icNumber.length() - 12, icNumber.length() - 4);
                LogUtils.d(TAG, "ReadIcSerialThread substring = " + substring);
                //1.播放声音
//                SoundUtil.soundWelcome(MyApplication.getContext());
                //2.检测卡号是否存在
                String parseIcNumber = WGUtil.parseIcNumber(substring).toLowerCase().trim();
                PersonBean bean = PersonDao.query2ICCard(parseIcNumber);
                if (bean == null) {
                    LogUtils.d(TAG, "没有这个人的IC卡数据 " +parseIcNumber);
                    EventBus.getDefault().post(new ICEvent("", MyApplication.getContext().getString(R.string.ic_card_no_register), AppSettingUtil.getConfig().getDoorType()));
                    continue;
                }
                //3.输出维根信息
                if (AppSettingUtil.getConfig().getOpenDoorType() == Const.OPEN_DOOR_TYPE_I_C) {
                    int doorType = AppSettingUtil.getConfig().getDoorType();
                    //直接开门
                    if (doorType == Const.WG_26) {
                        //维根 26
                        EventBus.getDefault().post(new ICEvent("维根26", WGUtil.parseWG16To10(parseIcNumber), Const.WG_26));
                        HWUtil.openDoorWeigen26(parseIcNumber);

                    } else if (doorType == Const.WG_26_0) {
                        //4.打开继电器
                        HWUtil.openDoorRelay();
                        EventBus.getDefault().post(new ICEvent("维根26", WGUtil.parseWG16To10(parseIcNumber), Const.WG_26));
                        HWUtil.openDoorWeigen26(parseIcNumber);
                    } else if (doorType == Const.WG_34) {
                        //维根 34,16进制的数翻转 , 1:直接转成 10 进制数,输出到维根,2:抛弃第一个字节,后面3个字节转成10进制数,前面补0后输出到维根34
                        try {
                            HWUtil.openDoorWeigen34(parseIcNumber);
                            EventBus.getDefault().post(new ICEvent("维根34", WGUtil.parseWG16To10(parseIcNumber), Const.WG_34));
                        } catch (Exception e) {
                            EventBus.getDefault().post(new ICEvent("维根34", e.getMessage(), Const.WG_34));
                        }
                    } else if (doorType == Const.WG_34_0) {
                        //4.打开继电器
                        HWUtil.openDoorRelay();
                        try {
                            HWUtil.openDoorWeigen34(parseIcNumber);
                            EventBus.getDefault().post(new ICEvent("维根34",WGUtil.parseWG16To10(parseIcNumber), Const.WG_34));
                        } catch (Exception e) {
                            EventBus.getDefault().post(new ICEvent("维根34", e.getMessage(), Const.WG_34));
                        }
                    } else if (doorType == Const.WG_66) {
                        //维根 66
                        HWUtil.openDoorWeigen66(parseIcNumber);
                        EventBus.getDefault().post(new ICEvent("WG 66", WGUtil.parseWG16To10(parseIcNumber), Const.WG_66));
                    } else if (doorType == Const.WG_66_0) {
                        //3.打开继电器
                        HWUtil.openDoorRelay();
                        //维根 26
                        HWUtil.openDoorWeigen66(parseIcNumber);
                        EventBus.getDefault().post(new ICEvent("WG 66", WGUtil.parseWG16To10(parseIcNumber), Const.WG_66));
                    } else if (doorType == Const.WG_0) {
                        //4.打开继电器
                        HWUtil.openDoorRelay();
                        EventBus.getDefault().post(new ICEvent("继电器", "", Const.WG_0));
                    }
                    //6.暂停和关门
                    SystemClock.sleep(AppSettingUtil.getConfig().getOpenDoorContinueTime());
                    if (doorType == Const.WG_34_0
                            || doorType == Const.WG_26_0
                            || doorType == Const.WG_0
                            || doorType == Const.WG_66_0) {

                        //7.关门和重置IC卡检测
                        HWUtil.closeDoor();
                    }

                } else if (AppSettingUtil.getConfig().getOpenDoorType() == Const.OPEN_DOOR_TYPE_FACE_IC) {
                    //需要和人脸匹配
                    FaceTempData.getInstance().setHaveICMessage(true);
                    FaceTempData.getInstance().setIcMessage(parseIcNumber);
                    FacePairStatus.getInstance().facePairThreadContinueOnce();
                    SystemClock.sleep(2000);
                    FaceTempData.getInstance().setHaveICMessage(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (e.getMessage().equals("read failed: EINVAL (Invalid argument)")) {
                    if (ttys1InputStream != null) {
                        try {
                            ttys1InputStream.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    try {
                        SerialThread.getInstance().getSerialPort1().close();
                    } catch (Exception e1) {
                        LogUtils.e(TAG, "SerialThread.getInstance().getSerialPort1().close() error = " + e1.getMessage());
                    }
                    SystemClock.sleep(2000);
                    try {
                        SerialThread.getInstance().openPortOpenDoor();
                        ttys1InputStream = SerialThread.getInstance().getTtys1InputStream();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    SystemClock.sleep(1000);
                }
                LogUtils.e(TAG, "ReadIcSerialThread error = " + e.getMessage());
            }
        }
        LogUtils.e(TAG, "========== ReadIcSerialThread 完结撒花 ==========");
    }
}
