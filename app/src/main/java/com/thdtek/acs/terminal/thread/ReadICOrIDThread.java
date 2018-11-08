package com.thdtek.acs.terminal.thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;

import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.bean.FacePairEvent;
import com.thdtek.acs.terminal.bean.ICEvent;
import com.thdtek.acs.terminal.bean.IDBean;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.face.FacePairStatus;
import com.thdtek.acs.terminal.face.FaceTempData;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.IHALUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SoundUtil;
import com.thdtek.acs.terminal.util.WGUtil;
import com.thdtek.acs.terminal.util.camera.CameraUtil;
import com.thdtek.acs.terminal.util.camera.GsonUtils;

import org.greenrobot.eventbus.EventBus;


public class ReadICOrIDThread extends Thread {


    private static final String TAG = ReadICOrIDThread.class.getSimpleName();
    private static ReadICOrIDThread readICThread = null;

    public static ReadICOrIDThread getInstance() {
        if (readICThread == null) {
            synchronized (ReadICOrIDThread.class) {
                if (readICThread == null) {
                    readICThread = new ReadICOrIDThread();
                    readICThread.start();
                }
            }
        }
        return readICThread;
    }

    //是否继续循环的获取id数据
    private boolean mReadIdLoop = true;

    @Override
    public void run() {
        super.run();
        long bInitCarder = -1;
        LogUtils.e(TAG, "========== ReadICOrIDThread 准备开启 ==========");
        try {
            if (IHALUtil.getmIhalInterface() != null) {
                bInitCarder = IHALUtil.getmIhalInterface().IReader_Init("BYYJ", "1.0");
                IHALUtil.getmIhalInterface().IReader_GetIC_ID("S50");
            }
        } catch (Exception e) {
            LogUtils.e(TAG, e.getMessage());
        }

        if (bInitCarder == 0) {
            LogUtils.d(TAG, "========== 远程服务连接成功,准备开始监听IC 和 iD 模块... ========== ");
            while (mReadIdLoop) {
                try {
                    getMessage();
                } catch (Exception e) {
                    LogUtils.e(TAG, "获取 IC或ID卡 Message 信息异常 = " + e.getMessage());
                }
            }
        } else {
            LogUtils.e(TAG, "========== ReadICOrIDThread 远程连接发生异常 ==========");
        }
        LogUtils.e(TAG, "========== ReadICOrIDThread 完结撒花 ==========");
    }

    private void getMessage() throws Exception {
        if (AppSettingUtil.getConfig().getOpenDoorType() == Const.OPEN_DOOR_TYPE_FACE_ID) {
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
        String icNumber = IHALUtil.getmIhalInterface().IReader_GetIC_ID("S50");
        if (TextUtils.isEmpty(icNumber)) {
            return;
        }
        LogUtils.d(TAG, "IC 卡信息 = " + icNumber + " 维根 = " + AppSettingUtil.getConfig().getOpenDoorType() + " openDoor = " + openDoor
                + " 26||34 = " + AppSettingUtil.getConfig().getDoorType());

        if (openDoor) {//单纯的工号开门
            //1.播放声音
            SoundUtil.soundWelcome(MyApplication.getContext());
            //2.判断IC卡是否已经注册
            String parseIcNumber = WGUtil.parseIcNumber(icNumber).toLowerCase().trim();
            PersonBean bean = PersonDao.query2ICCard(parseIcNumber);
            if (bean == null) {
                EventBus.getDefault().post(new ICEvent("", "IC卡未登记,请登记", Const.WG_34));
                LogUtils.d(TAG, "没有这个人的IC卡数据");
                SystemClock.sleep(1000);
                return;
            }
            //3.打开继电器
            HWUtil.openDoorRelay();
            //4.设置已经检测到IC卡数据
            FaceTempData.getInstance().setHaveICMessage(true);
            //5.输出维根信息
            if (AppSettingUtil.getConfig().getDoorType() == Const.WG_26) {
                //维根 26
                HWUtil.openDoorWeigen26(WGUtil.parseWG26(icNumber) + "");
                EventBus.getDefault().post(new ICEvent("维根26", WGUtil.parseIcNumber(icNumber), Const.WG_26));
            } else if (AppSettingUtil.getConfig().getDoorType() == Const.WG_34) {
                //维根 34,16进制的数翻转 , 1:直接转成 10 进制数,输出到维根,2:抛弃第一个字节,后面3个字节转成10进制数,前面补0后输出到维根34
                try {
                    HWUtil.openDoorWeigen34(WGUtil.parseWG34(icNumber) + "");
                    EventBus.getDefault().post(new ICEvent("维根34", WGUtil.parseIcNumber(icNumber), Const.WG_34));
                } catch (Exception e) {
                    EventBus.getDefault().post(new ICEvent("维根34", e.getMessage(), Const.WG_34));
                }
            } else if (AppSettingUtil.getConfig().getDoorType() == Const.WG_0) {
                EventBus.getDefault().post(new ICEvent("继电器", parseIcNumber, Const.WG_0));
            }
            //6.暂停,持续开门时间
            SystemClock.sleep(AppSettingUtil.getConfig().getOpenDoorContinueTime());
            //7.关门和重置IC卡检测
            HWUtil.closeDoor();
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
        handleIcMessage(icNumber, true, Const.HANDLER_DELAY_TIME_3000);
    }

    private void getIdCardMessage() throws Exception {
        if (FaceTempData.getInstance().isHaveIdMessage()) {
            return;
        }
        String s = IHALUtil.getmIhalInterface().IReader_GetID_Text();
        if (TextUtils.isEmpty(s)) {
            return;
        }
        LogUtils.d(TAG, "ID message = " + s);
        if (!TextUtils.isEmpty(s)) {
            //清空以前的临时数据
            FaceTempData.getInstance().setIdMessage(null);
            IDBean idBean = GsonUtils.fromJson(s, IDBean.class);
            Bitmap bitmap = BitmapFactory.decodeFile(idBean.getImage());
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
            FacePairStatus.getInstance().facePairThreadContinueOnce();
            SoundUtil.soundWelcome(MyApplication.getContext());
            CameraUtil.resetCameraVariable(true);
            SystemClock.sleep(3000);
            LogUtils.d(TAG, "==== 开始重新读取 身份证 内容 ====");
            FaceTempData.getInstance().setHaveIdMessage(false);
        }
    }

    public void stopThread() {
        mReadIdLoop = false;
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
        SystemClock.sleep(delayTime);
        LogUtils.d(TAG, "==== 开始重新读取IC卡内容 ====");
        FaceTempData.getInstance().setHaveICMessage(false);
    }
}
