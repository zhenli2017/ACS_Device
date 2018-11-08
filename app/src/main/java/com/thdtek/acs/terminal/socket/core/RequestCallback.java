package com.thdtek.acs.terminal.socket.core; /**
 * Created by ygb on 2016/9/5.
 */


import com.thdtek.acs.terminal.Msg;

/**
 * <p>
 * 网络请求接口
 * </p>
 *
 * @author : guobin.yang
 * @date : 2016/9/5 18:00
 * @email : ygbokay@163.com
 */
public interface RequestCallback {
    //请求返回
    void onResponse(Msg.Message message);
    //请求超时
    void onTimeout();

}