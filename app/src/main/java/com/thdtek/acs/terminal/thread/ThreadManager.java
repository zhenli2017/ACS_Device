package com.thdtek.acs.terminal.thread;

import android.util.LongSparseArray;

import com.thdtek.acs.terminal.bean.PairBean;
import com.thdtek.acs.terminal.bean.PersonBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * Time:2018/9/25
 * User:lizhen
 * Description:
 */

public class ThreadManager {

    private static HashMap<String, BaseThread> mThreadHashMap = new HashMap<>();
    private static HashMap<String, ArrayBlockingQueue<Object>> mQueueHashMap = new HashMap<>();

    private static SynchronousQueue<HashMap<Float, PersonBean>> mSyncQueueTwo = new SynchronousQueue<>();
    private static SynchronousQueue<HashMap<Float, PersonBean>> mSyncQueueThree = new SynchronousQueue<>();

    public static void addThread(BaseThread baseThread) {
        if (baseThread != null) {
            mThreadHashMap.put(baseThread.getClass().getSimpleName(), baseThread);
            mQueueHashMap.put(baseThread.getClass().getSimpleName(), new ArrayBlockingQueue<Object>(1024*50));

        }
    }


    public static SynchronousQueue<HashMap<Float, PersonBean>> getSyncQueueThree() {
        return mSyncQueueThree;
    }

    public static SynchronousQueue<HashMap<Float, PersonBean>> getSyncQueueTwo() {
        return mSyncQueueTwo;
    }

    public static ImageSaveThread getImageSaveThread() {
        return (ImageSaveThread) getThread(ImageSaveThread.class.getSimpleName());
    }

    public static BaseThread getThread(String name) {
        return mThreadHashMap.get(name);
    }

    public static ArrayBlockingQueue<Object> getArrayBlockingQueue(String name) {
        return mQueueHashMap.get(name);
    }


    public static BaseThread removeThread(String name) {
        return mThreadHashMap.remove(name);
    }

    public static ArrayBlockingQueue<Object> removeArrayBlockingQueue(String name) {
        return mQueueHashMap.remove(name);
    }


    public static void closeThread(String name) {
        BaseThread baseThread = mThreadHashMap.remove(name);
        if (baseThread != null) {
            baseThread.close();
        }
    }

    public static void clearAllQueue(String name) {
        ArrayBlockingQueue<Object> queue = mQueueHashMap.get(name);
        if (queue != null) {
            queue.clear();
        }
    }

    public static void closeAll() {
        Set<String> keySet = mThreadHashMap.keySet();
        ArrayList<String> keyList = new ArrayList<>(keySet);
        for (int i = 0; i < keyList.size(); i++) {
            closeThread(keyList.get(i));
        }
        mThreadHashMap.clear();

        Set<String> queueList = mQueueHashMap.keySet();
        ArrayList<String> queueKeyList = new ArrayList<>(queueList);
        for (int i = 0; i < queueKeyList.size(); i++) {
            clearAllQueue(queueKeyList.get(i));
        }
        mQueueHashMap.clear();

    }
}
