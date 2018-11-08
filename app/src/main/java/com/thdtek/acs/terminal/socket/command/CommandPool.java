package com.thdtek.acs.terminal.socket.command;


import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.base.ThreadPool;
import com.thdtek.acs.terminal.socket.core.RepeatBean;
import com.thdtek.acs.terminal.socket.core.RequestCallback;
import com.thdtek.acs.terminal.socket.core.RequestInfo;
import com.thdtek.acs.terminal.socket.core.SendMsgHelper;
import com.thdtek.acs.terminal.util.LogUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

public final class CommandPool {
    private final String TAG = CommandPool.class.getSimpleName();

    //缓存请求信息
    private ConcurrentMap<Long, RequestInfo> map_info = new ConcurrentHashMap();
    //缓存延时对象
    private DelayQueue<DelayItem<Long>> queue = new DelayQueue();
    //轮询线程
    private Thread daemonThread;


    //单例
    private static CommandPool instance = new CommandPool("CommandPool");
    public static CommandPool getInstance(){
        if(instance == null){
            instance = new CommandPool("CommandPool");
        }
        return instance;
    }
    private CommandPool(String name) {

        Runnable daemonTask = new Runnable() {
            public void run() {
                daemonCheck();
            }
        };

        daemonThread = new Thread(daemonTask);
        daemonThread.setDaemon(true);
        daemonThread.setName(name);
        daemonThread.start();


    }


    /**
     * 不断循环遍历
     * 1.发现超时则调用超时回调并移除响应请求序列
     */
    private void daemonCheck() {
        for (;;) {
            try {
                DelayItem<Long> delayItem = queue.take();
                if (delayItem != null) {
                    // 超时对象处理
                    final long key = delayItem.getItem();
                    final RequestCallback cmd = map_info.get(key).getListener();

                    ThreadPool.getThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            cmd.onTimeout();
                        }
                    });


                    //移除对应缓存
                    LogUtils.e(TAG, "请求码超时   key="+key);


                    //判断是否需要重新发起请求
                    int needRepeatNum = map_info.get(key).getRepeatBean().getNeedRepeatNum();
                    int repeatNum = map_info.get(key).getRepeatBean().getRepeatNum();
                    if(needRepeatNum > repeatNum){

                        LogUtils.e(TAG, "请求码超时 重新发起请求   key="+key);
                        map_info.get(key).getRepeatBean().setRepeatNum(repeatNum + 1);



                        //重新请求
                        RequestInfo info = map_info.get(key);

                        new SendMsgHelper().request(
                                info.getPkg(),
                                (int)info.getSeq(),
                                info.getTimeout(),
                                info.getRepeatBean(),
                                info.getListener());
                    }else{
                        remove(key);
                    }



                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 缓存整个请求信息
     * @param key
     * @param pkg
     * @param listener
     * @param time
     * @param repeatBean
     * @param clazz
     */
    public void put(Long key, Msg.Package pkg, RequestCallback listener, long time, RepeatBean repeatBean, Class clazz) {

        //如果存在 先移除
        remove(key);


        //缓存延时对象
        long nanoTime = TimeUnit.NANOSECONDS.convert(time, TimeUnit.MILLISECONDS);
        DelayItem<Long> item = new DelayItem(key, nanoTime);
        queue.put(item);

        //缓存请求信息
        RequestInfo info = new RequestInfo();
        info.setSeq(key);
        info.setClazz(clazz);
        info.setListener(listener);
        info.setPkg(pkg);
        info.setTimeout(time);
        info.setRepeatBean(repeatBean);
        info.setDelayItem(item);
        map_info.put(key, info);


    }


    public RequestInfo getRequestInfo(Long key){
        if(map_info.containsKey(key)){
            return map_info.get(key);
        }
        return null;
    }


    public void remove(Long key) {
        try{
            if(map_info.containsKey(key)){
                if(queue.contains(map_info.get(key).getDelayItem())){
                    queue.remove(map_info.get(key).getDelayItem());
                }
                map_info.remove(key);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void removeAll() {
        for(Long key : map_info.keySet()){
            remove(key);
        }
    }


}

