package com.thdtek.acs.terminal.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Author  :  lizhen
 * Date    :  2017/2/20.
 * package :  com.devond.zxing.
 */

public class WeakHandler extends Handler {

    private WeakReference<WeakHandlerCallBack> mCallBackWeakReference;
    private ArrayList<WeakHandlerBean> mBeanList;

    public WeakHandler(WeakHandlerCallBack callBack) {
        mCallBackWeakReference = new WeakReference<WeakHandlerCallBack>(callBack);
        mBeanList = new ArrayList<>();
    }

    public WeakHandler(WeakHandlerCallBack callBack, Callback callback) {
        super(callback);
        mCallBackWeakReference = new WeakReference<WeakHandlerCallBack>(callBack);
        mBeanList = new ArrayList<>();
    }

    public WeakHandler(WeakHandlerCallBack callBack, Looper looper) {
        super(looper);
        mCallBackWeakReference = new WeakReference<WeakHandlerCallBack>(callBack);
        mBeanList = new ArrayList<>();
    }

    public WeakHandler(WeakHandlerCallBack callBack, Looper looper, Callback callback) {
        super(looper, callback);
        mCallBackWeakReference = new WeakReference<WeakHandlerCallBack>(callBack);
        mBeanList = new ArrayList<>();
    }

    public void sendMessageLoop(int what, long time) {
        sendMessageLoop(what, -1, time);
    }

    public void sendMessageLoop(int what, int count, long time) {
        mBeanList.add(new WeakHandlerBean(what, count, 1, time));
        sendEmptyMessageDelayed(what, time);
    }

    public void stopMessageLoop(int what) {

        ListIterator<WeakHandlerBean> iterator = mBeanList.listIterator();
        while (iterator.hasNext()) {
            WeakHandlerBean next = iterator.next();
            if (next.getWhat() == what) {
                iterator.remove();
            }
        }
    }

    public void cancelMessage(Message message) {
        cancelMessage(message.what);
    }

    public void cancelMessage(int what) {
        if (hasMessages(what)) {
            removeMessages(what);
        }
    }

    public void reSendMessage(Message message) {
        reSendMessageDelay(message, 0);
    }

    public void reSendMessage(int what) {
        reSendMessageDelay(what, 0);
    }

    public void reSendMessageDelay(Message message, long delayTime) {
        if (hasMessages(message.what)) {
            removeMessages(message.what);
        }
        sendMessageDelayed(message, delayTime);
    }

    public void reSendMessageDelay(int what, long delayTime) {
        if (hasMessages(what)) {
            removeMessages(what);
        }
        sendEmptyMessageDelayed(what, delayTime);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        WeakHandlerCallBack back = mCallBackWeakReference.get();
        if (back == null) {
            return;
        }

        for (WeakHandlerBean bean : mBeanList) {
            if (bean == null) {
                return;
            }
            if (bean.getWhat() != msg.what) {
                continue;
            }
            if (bean.getCount() == -1) {
                //无限循环
                removeMessages(bean.getWhat());
                sendEmptyMessageDelayed(bean.getWhat(), bean.getTime());
            } else if (bean.getCurrentCount() < bean.getCount()) {
                //有限循环
                removeMessages(bean.getWhat());
                bean.setCurrentCount(bean.getCurrentCount() + 1);
                sendEmptyMessageDelayed(bean.getWhat(), bean.getTime());
            }
        }
        back.handleMessage(msg);
    }


    public interface WeakHandlerCallBack {
        void handleMessage(Message message);
    }

    private class WeakHandlerBean {
        private int what;
        private int count;
        private long time;
        private long currentCount;


        public WeakHandlerBean() {
        }

        public WeakHandlerBean(int what, int count, long currentCount, long time) {
            this.what = what;
            this.count = count;
            this.time = time;
            this.currentCount = currentCount;
        }

        public long getCurrentCount() {
            return currentCount;
        }

        public void setCurrentCount(long currentCount) {
            this.currentCount = currentCount;
        }

        public int getWhat() {
            return what;
        }

        public void setWhat(int what) {
            this.what = what;
        }


        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            WeakHandlerBean bean = (WeakHandlerBean) o;

            return what != bean.what;

        }
    }
}
