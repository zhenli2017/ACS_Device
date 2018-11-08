package com.thdtek.acs.terminal.socket.command;


import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.socket.core.RepeatBean;
import com.thdtek.acs.terminal.socket.core.RequestCallback;
import com.thdtek.acs.terminal.socket.core.RequestInfo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class CommandPool2 {
    private final String TAG = CommandPool2.class.getSimpleName();

    //缓存请求信息
    private ConcurrentMap<Long, RequestInfo> map_info = new ConcurrentHashMap();


    //单例
    private static CommandPool2 instance = new CommandPool2();
    public static CommandPool2 getInstance(){
        if(instance == null){
            instance = new CommandPool2();
        }
        return instance;
    }
    private CommandPool2() {


    }


    //检查
    public void check(){
        try {
            if(map_info.size() > 0){
                for(Long key : map_info.keySet()){
                    getRequestInfo(key).getListener().onTimeout();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
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


        //缓存请求信息
        RequestInfo info = new RequestInfo();
        info.setSeq(key);
        info.setClazz(clazz);
        info.setListener(listener);
        info.setPkg(pkg);
        info.setTimeout(time);
        info.setRepeatBean(repeatBean);
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

