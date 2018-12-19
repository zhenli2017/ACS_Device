package com.thdtek.acs.terminal.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;

import com.sdses.JniCommonInterface;
import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.bean.FacePairEvent;
import com.thdtek.acs.terminal.bean.HardwareStatusEvent;
import com.thdtek.acs.terminal.bean.ICEvent;
import com.thdtek.acs.terminal.bean.ICOrIDEvent;
import com.thdtek.acs.terminal.bean.IDBean;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.bean.UsbEvent;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.face.FacePairStatus;
import com.thdtek.acs.terminal.face.FaceTempData;
import com.thdtek.acs.terminal.ui.cameraphoto.CameraPhotoActivity;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.ByteFormatTransferUtils;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SoundUtil;
import com.thdtek.acs.terminal.util.WGUtil;
import com.thdtek.acs.terminal.util.camera.CameraUtil;
import com.thdtek.acs.terminal.util.camera.GsonUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.HashMap;

/**
 * Time:2018/11/13
 * User:lizhen
 * Description:
 */

public class ReadIcOrIdThread2 extends Thread {

    private static final String TAG = ReadIcOrIdThread2.class.getSimpleName();
    public static final int HANDLER_IC_MESSAGE = 100;
    public static final int HANDLER_ID_MESSAGE = 101;

    private static final String PORT_PARA = "261A0011";
    private int VID = 0;
    private int PID = 0;
    private long mIDHandler = -1L;
    private boolean mLoop = true;

    private byte mType = 0;
    private byte mInfoEncoding = 1;
    private long mTimeOutMs = 0;
    private byte[] mUid = new byte[16];
    private final MyHandler mMyHandler;

    public static class MyHandler extends Handler {

        private WeakReference<ReadIcOrIdThread2> mWeakReference;

        public MyHandler(Looper looper, ReadIcOrIdThread2 readIcOrIdThread2) {
            super(looper);
            mWeakReference = new WeakReference<ReadIcOrIdThread2>(readIcOrIdThread2);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ReadIcOrIdThread2 readIcOrIdThread2 = mWeakReference.get();
            if (readIcOrIdThread2 == null) {
                return;
            }
            switch (msg.what) {
                case HANDLER_IC_MESSAGE:
                    LogUtils.d(TAG, "==== 开始重新读取 IC 卡内容 ====");
                    FaceTempData.getInstance().setHaveICMessage(false);
                    break;
                case HANDLER_ID_MESSAGE:
                    LogUtils.d(TAG, "==== 开始重新读取 ID 卡内容 ====");
                    FaceTempData.getInstance().setHaveIdMessage(false);
                    break;
                default:
                    break;
            }
        }
    }

    public ReadIcOrIdThread2() {
        mMyHandler = new MyHandler(Looper.getMainLooper(), this);
    }

    @Override
    public void run() {
        super.run();
        boolean initIDModel = false;
        while (mLoop) {
            initIDModel = initIDModel();
            if (initIDModel) {
                LogUtils.d(TAG, "====== ReadIcOrIdThread2 初始化成功 ======");
                break;
            }
            SystemClock.sleep(10000);
        }

        while (mLoop) {
            try {
                getMessage();
            } catch (Exception e) {
                FaceTempData.getInstance().setHaveICMessage(false);
                FaceTempData.getInstance().setHaveIdMessage(false);
                LogUtils.e(TAG, "====== ReadIcOrIdThread2 error = " + e.getMessage());
            }
        }
        LogUtils.e(TAG, "====== " + TAG + " 完结撒花,特征值比对线程关闭 ======");
    }


    private boolean initIDModel() {
        LogUtils.e(TAG, "====== 准备初始化 ReadIcOrIdThread2 ======");
        VID = Integer.parseInt(PORT_PARA.substring(0, 4), 16);
        PID = Integer.parseInt(PORT_PARA.substring(4, 8), 16);

        //----连接USB设备----
        long usbPermission = JniCommonInterface.GetUsbPermission(MyApplication.getContext(), VID, PID);
        if (usbPermission != 0) {
            LogUtils.d(TAG, "====== usbPermission = " + usbPermission + " " + parseCode((int) usbPermission));
            EventBus.getDefault().post(new HardwareStatusEvent(HardwareStatusEvent.HARDWARE_STATUS_ID, HardwareStatusEvent.HARDWARE_STATUS_FAIL, parseCode((int) usbPermission)));
            return false;
        }
        mIDHandler = JniCommonInterface.OpenDevice("USB", PORT_PARA, "");
        if (mIDHandler < 0) {
            LogUtils.d(TAG, "====== mHandler = " + mIDHandler + " " + parseCode((int) mIDHandler));
            EventBus.getDefault().post(new HardwareStatusEvent(HardwareStatusEvent.HARDWARE_STATUS_ID, HardwareStatusEvent.HARDWARE_STATUS_FAIL, parseCode((int) usbPermission)));

            return false;
        }
        LogUtils.d(TAG, "====== 连接成功 mHandler = " + mIDHandler + " " + parseCode((int) mIDHandler));
        EventBus.getDefault().post(new HardwareStatusEvent(HardwareStatusEvent.HARDWARE_STATUS_ID, HardwareStatusEvent.HARDWARE_STATUS_SUCCESS, parseCode((int) usbPermission)));
        return true;
    }

    private String getIc() throws Exception {
        long[] uidLen = new long[1];
        long res = JniCommonInterface.M1FindCard(mUid, uidLen);
        if (res != 0) {
            return "";
        }
        byte[] uidAsc = new byte[32];
        JniCommonInterface.HexToAsc(mUid, uidLen[0], uidAsc);
        return new String(uidAsc, "GBK").trim();
    }

    private boolean mLastIdMessageFail = true;

    private String getIdMessage() throws Exception {
        byte[] IdCardInfo = new byte[40960];
        //第一个参数Type可为0（二代证、外国人都读）、1（只读二代证）、2（只读外国人）
        //第二个参数编码方式1（GBK）、2（Unicode16-LE）
        //第四个参数TimeOutMs：等待读卡时间，超过该时间，读卡失败（单位ms）
        long res = JniCommonInterface.IdReadCard(mType, mInfoEncoding, IdCardInfo, mTimeOutMs);
        if (res != 0) {
            if (!mLastIdMessageFail) {
                EventBus.getDefault().post(new HardwareStatusEvent(HardwareStatusEvent.HARDWARE_STATUS_ID, HardwareStatusEvent.HARDWARE_STATUS_SUCCESS, parseCode((int) res)));
            }
            mLastIdMessageFail = true;
            return "";
        }
        mLastIdMessageFail = false;
        return new String(IdCardInfo, "GBK").trim();
    }


    private void getMessage() throws Exception {
        if (Const.PERSON_TYPE_CAMERA_PHOTO) {
            //当前是身份证录入几面
            getIdCardMessage();
        } else if (AppSettingUtil.getConfig().getOpenDoorType() == Const.OPEN_DOOR_TYPE_FACE_ID) {
            //人脸加身份证开门,获取身份证信息
            getIdCardMessage();
        } else if (AppSettingUtil.getConfig().getOpenDoorType() == Const.OPEN_DOOR_TYPE_I_C) {
            //IC卡开门,获取IC卡信息
            getIcCardMessage(true);
        } else if (AppSettingUtil.getConfig().getOpenDoorType() == Const.OPEN_DOOR_TYPE_FACE_IC) {
            //人脸+IC卡开门,获取IC卡信息
            getIcCardMessage(false);
        } else if (AppSettingUtil.getConfig().getGuestOpenDoorType() == Const.OPEN_DOOR_TYPE_GUEST_ID_FACE_UN_REGISTER
                || AppSettingUtil.getConfig().getGuestOpenDoorType() == Const.OPEN_DOOR_TYPE_GUEST_ID_FACE_REGISTER) {
            //访客模式人脸+身份证
            getIdCardMessage();
        }
    }

    private void getIcCardMessage(boolean openDoor) throws Exception {
        if (FaceTempData.getInstance().isHaveICMessage()) {
            return;
        }
        //获取IC卡信息
        String icNumber = getIc();
        if (TextUtils.isEmpty(icNumber)) {
            return;
        }
        int doorType = AppSettingUtil.getConfig().getDoorType();
        LogUtils.d(TAG, "IC 卡信息 = " + icNumber + " 维根 = " + AppSettingUtil.getConfig().getOpenDoorType() + " openDoor = " + openDoor
                + " 26||34||260||340||0||66||660 = " + doorType);

        if (openDoor) {//单纯的工号开门
            //1.播放声音
            SoundUtil.soundWelcome(MyApplication.getContext());
            //2.判断IC卡是否已经注册
            String parseIcNumber = WGUtil.parseIcNumber(icNumber).toLowerCase().trim();
            PersonBean bean = PersonDao.query2ICCard(parseIcNumber);
            if (bean == null) {
                EventBus.getDefault().post(new ICEvent("", MyApplication.getContext().getString(R.string.ic_no_login), Const.WG_0));
                LogUtils.d(TAG, "没有这个人的IC卡数据");
                SystemClock.sleep(500);
                return;
            }
            //4.设置已经检测到IC卡数据
            FaceTempData.getInstance().setHaveICMessage(true);
            //5.输出维根信息

            if (doorType == Const.WG_26) {
                //维根 26
                HWUtil.openDoorWeigen26(parseIcNumber);
                EventBus.getDefault().post(new ICEvent("WG 26", WGUtil.parseWG16To10(parseIcNumber), Const.WG_26));
            } else if (doorType == Const.WG_26_0) {
                //3.打开继电器
                HWUtil.openDoorRelay();
                //维根 26
                HWUtil.openDoorWeigen26(parseIcNumber);
                EventBus.getDefault().post(new ICEvent("WG 26", WGUtil.parseWG16To10(parseIcNumber), Const.WG_26));
            } else if (doorType == Const.WG_34_0) {
                //3.打开继电器
                HWUtil.openDoorRelay();
                try {
                    HWUtil.openDoorWeigen34(parseIcNumber);
                    EventBus.getDefault().post(new ICEvent("WG 34", WGUtil.parseWG16To10(parseIcNumber), Const.WG_34));
                } catch (Exception e) {
                    EventBus.getDefault().post(new ICEvent("WG 34", e.getMessage(), Const.WG_34));
                }
            } else if (doorType == Const.WG_34) {
                //维根 34,16进制的数翻转 , 1:直接转成 10 进制数,输出到维根,2:抛弃第一个字节,后面3个字节转成10进制数,前面补0后输出到维根34
                try {
                    HWUtil.openDoorWeigen34(parseIcNumber);
                    EventBus.getDefault().post(new ICEvent("WG 34", WGUtil.parseWG16To10(parseIcNumber), Const.WG_34));
                } catch (Exception e) {
                    EventBus.getDefault().post(new ICEvent("WG 34", e.getMessage(), Const.WG_34));
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
                //3.打开继电器
                HWUtil.openDoorRelay();
                EventBus.getDefault().post(new ICEvent(MyApplication.getContext().getString(R.string.ji_dian_qi), "", Const.WG_0));
            }
            //6.暂停,持续开门时间
            SystemClock.sleep(AppSettingUtil.getConfig().getOpenDoorContinueTime());
            if (doorType == Const.WG_34_0
                    || doorType == Const.WG_26_0
                    || doorType == Const.WG_0
                    || doorType == Const.WG_66_0) {

                //7.关门和重置IC卡检测
                HWUtil.closeDoor();
            }
            FaceTempData.getInstance().setHaveICMessage(false);
        } else {
            LogUtils.d(TAG, "人脸+工号 读取数据 = " + icNumber);
            handleICMessage(icNumber);
        }
    }

    private void handleICMessage(String icNumber) {
        icNumber = icNumber.toLowerCase().trim();
        icNumber = WGUtil.parseIcNumber(icNumber);
        if (TextUtils.isEmpty(icNumber)) {
            LogUtils.d(TAG, "读取到的 IC message 的值为空,return ");
            return;
        }
        //使用10进制的数查询
        PersonBean bean = PersonDao.query2ICCard(icNumber);
        if (bean == null) {
            //使用16进制的数查询
            bean = PersonDao.query2ICCard(icNumber);
            if (bean == null) {
                LogUtils.d(TAG, "没有这个人的IC卡数据");
                handleIcMessage(icNumber, false, Const.HANDLER_DELAY_TIME_1000);
                return;
            }
        }
        //停止人脸识别的线程
        handleIcMessage(icNumber, true, Const.HANDLER_DELAY_TIME_2000);
    }

    private void getIdCardMessage() throws Exception {
        if (FaceTempData.getInstance().isHaveIdMessage()) {
            return;
        }
        String s = getIdMessage();
        if (TextUtils.isEmpty(s)) {
            return;
        }

        String[] idInfoStr = s.split(":", 16);

        LogUtils.d(TAG, "ID message = " + s);
        if (!TextUtils.isEmpty(s)) {
            //清空以前的临时数据
            FaceTempData.getInstance().setIdMessage(null);
            IDBean idBean = new IDBean();
            //出生日期
            idBean.setBirthday(idInfoStr[7]);
            //家庭住址
            idBean.setLocal(idInfoStr[8]);
            //身份证号码
            idBean.setIdNumber(idInfoStr[9]);
            //姓名
            idBean.setName(idInfoStr[1]);
            //性别
            idBean.setSex(idInfoStr[3]);
            //民族
            idBean.setNation(idInfoStr[5]);
            //有效日期
            idBean.setValidityTime(idInfoStr[11] + "-" + idInfoStr[12]);
            //签发机关
            idBean.setSigningOrganization(idInfoStr[10]);
            Bitmap bitmap = null;
            if (!TextUtils.isEmpty(idInfoStr[14])) {
                byte[] imgbytes = Base64.decode(idInfoStr[14], Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(imgbytes, 0, imgbytes.length);
            }
            if (bitmap == null) {
                LogUtils.d(TAG, "====== 身份证照片读取失败");
                return;
            }
            try {
                String path = CameraUtil.save2IDImage(bitmap, System.currentTimeMillis() + Const.IMAGE_TYPE_DEFAULT_JPG);
                idBean.setImage(path);
            } catch (Exception e) {
                LogUtils.d(TAG, "读取身份证获取去身份证图片失败 = " + e.getMessage());
                return;
            }

            //开始添加数据
            FaceTempData.getInstance().setHaveIdMessage(true);
            FaceTempData.getInstance().setIdMessage(idBean);
            //停止人脸识别的线程
            if (Const.PERSON_TYPE_CAMERA_PHOTO) {
                //当前是身份证录入界面
                EventBus.getDefault().post(new UsbEvent(CameraPhotoActivity.USB_CAMERA_TAKE_PHOTO, idBean));
                EventBus.getDefault().post(new HardwareStatusEvent(HardwareStatusEvent.HARDWARE_STATUS_ID
                        , HardwareStatusEvent.HARDWARE_STATUS_SUCCESS, HardwareStatusEvent.HARDWARE_STATUS_READ_SUCCESS));
            } else {
                FacePairStatus.getInstance().facePairThreadContinueOnce();
            }
            SoundUtil.soundWelcome(MyApplication.getContext());
            if (mMyHandler != null) {
                mMyHandler.removeMessages(HANDLER_ID_MESSAGE);
                mMyHandler.sendEmptyMessageDelayed(HANDLER_ID_MESSAGE, Const.HANDLER_DELAY_TIME_3000);
            }
        }
    }

    private void handleIcMessage(String icNumber, boolean stopThread, long delayTime) {
        FaceTempData.getInstance().setHaveICMessage(true);
        FaceTempData.getInstance().setIcMessage(icNumber);
        if (stopThread) {
            FacePairStatus.getInstance().facePairThreadContinueOnce();
            FacePairEvent facePairEvent = new FacePairEvent();
            facePairEvent.setType(Const.PAIR_TYPE_IC);
            FaceTempData.getInstance().mQueue.add(facePairEvent);
        }
        SoundUtil.soundWelcome(MyApplication.getContext());
        if (mMyHandler != null) {
            mMyHandler.removeMessages(HANDLER_IC_MESSAGE);
            mMyHandler.sendEmptyMessageDelayed(HANDLER_IC_MESSAGE, Const.HANDLER_DELAY_TIME_3000);
        }

    }


    public void close() {
        mLoop = false;
        if (mMyHandler != null) {
            mMyHandler.removeCallbacksAndMessages(null);
        }
    }

    public String parseCode(int code) {
        switch (code) {
            case 0:
                return "操作成功";
            case -1:
                return "操作失败";
            case -2:
                return "参数错误";
            case -3:
                return "句柄无效";
            case -4:
                return "暂不支持";
            case -5:
                return "指针为空";
            case -6:
                return "数据头错";
            case -7:
                return "校验和错";
            case -8:
                return "发送出错";
            case -9:
                return "接收出错";
            case -10:
                return "通讯超时";
            case -11:
                return "解码库加载失败";
            case -12:
                return "头像解码失败";
            case -13:
                return "文件读写失败";
            case -14:
                return "图片格式转换失败";
            case -15:
                return "证件类型不符(二代证/外国人/港澳台)";
            default:
                return "";
        }
    }
}
