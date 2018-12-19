package com.thdtek.acs.terminal.face;

import com.thdtek.acs.terminal.bean.FacePairEvent;
import com.thdtek.acs.terminal.bean.IDBean;

import java.util.concurrent.ArrayBlockingQueue;


public class FaceTempData {

    private static final String TAG = FaceTempData.class.getSimpleName();
    private static FaceTempData faceTempData = new FaceTempData();

    public static FaceTempData getInstance() {
        return faceTempData;
    }

    //读取到 ID 卡信息
    private boolean mHaveIdMessage = false;
    //身份证所有的数据
    private IDBean mIdMessage;
    //读取到 IC 卡信息
    private boolean mHaveICMessage;
    //当前 IC 卡的内容
    private String mIcMessage;
    //当前是否正在下载人员信息
    private boolean mDownLoadPersonMsg;
    public ArrayBlockingQueue<FacePairEvent> mQueue = new ArrayBlockingQueue<FacePairEvent>(1024*1024);


    public synchronized boolean isDownLoadPersonMsg() {
        return mDownLoadPersonMsg;
    }

    public synchronized void setDownLoadPersonMsg(boolean downLoadPersonMsg) {
        mDownLoadPersonMsg = downLoadPersonMsg;
    }

    public synchronized String getIcMessage() {
        return mIcMessage;
    }

    public synchronized void setIcMessage(String icMessage) {
        mIcMessage = icMessage;
    }

    public synchronized boolean isHaveICMessage() {
        return mHaveICMessage;
    }

    public synchronized void setHaveICMessage(boolean stopReadICNumber) {
        mHaveICMessage = stopReadICNumber;
    }

    public synchronized boolean isHaveIdMessage() {
        return mHaveIdMessage;
    }

    public synchronized void setHaveIdMessage(boolean mHaveIdMessage) {
        this.mHaveIdMessage = mHaveIdMessage;
    }

    public synchronized IDBean getIdMessage() {
        return mIdMessage;
    }

    public synchronized void setIdMessage(IDBean mIdMessage) {
        this.mIdMessage = mIdMessage;
    }
}
