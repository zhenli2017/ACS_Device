package com.thdtek.acs.terminal.socket.core;
/**
 * Created by ygb on 2016/9/5.
 */



/**
 * <p>
 * 网络请求接口
 * </p>
 *
 * @author : guobin.yang
 * @date : 2016/9/5 18:00
 * @email : ygbokay@163.com
 */
public interface ConnectCallback {
    //连接成功
    void onSuccess();
    //连接失败
    void onFailure();


}