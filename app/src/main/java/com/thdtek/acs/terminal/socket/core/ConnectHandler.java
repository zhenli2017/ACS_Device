package com.thdtek.acs.terminal.socket.core;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;

import com.google.protobuf.ByteString;
import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.Msg.Package;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.base.ThreadPool;
import com.thdtek.acs.terminal.bean.ConfigBean;
import com.thdtek.acs.terminal.http.clientpull.PullAllAuthority;
import com.thdtek.acs.terminal.http.upload.UploadRecord;
import com.thdtek.acs.terminal.imp.person.persondownload.PersonDownLoadImp;
import com.thdtek.acs.terminal.socket.command.CommandPool2;
import com.thdtek.acs.terminal.util.AESUtils;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.AppUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DeviceSnUtil;
import com.thdtek.acs.terminal.util.DownloadApk;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SPUtils;
import com.thdtek.acs.terminal.util.SwitchConst;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

public class ConnectHandler {
    private static final String TAG = ConnectHandler.class.getSimpleName();
    //服务端ip 域名--测试使用
//    private static final String ADDR_DEFAULT = "47.74.130.48";
    private static String ADDR = "";
    //服务端端口--测试使用
//    private static final int PORT_DEFAULT = 16000;
    private static int PORT = 0;
    //重新连接服务器延时时间 毫秒
    private static final int HEARTBEAT_TIMEOUT_MS = 10 * 1000;
    private static final int HEARTBEAT_PERIOD_MS = 15 * 1000;
    //连接服务器超时时间 毫秒
    private static final int CONN_TIMEOUT_DEFAULT_MS = 5000;
    //socket核心实例
    private static ConnectCore connectCore;
    //消息头(proto的包第一个字节size的长度)
    private static final int len_head = 5;

    private static boolean isReconnectAfterDisconnect = true;

    //心跳handler
    private static final int HEARTBEAT_HANDLER_WAHT = 0;

    private static Handler heartbeatHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            LogUtils.d(TAG, "发送心跳 start... ... ");
            sendHeart();
            LogUtils.d(TAG, "发送心跳 end... ...");

            this.sendEmptyMessageDelayed(HEARTBEAT_HANDLER_WAHT, HEARTBEAT_PERIOD_MS);
        }
    };

    private static ConnectCallback connectCallback;


    public static void connect(final String addr, final int port, final boolean conn_repeat, final ConnectCallback connectCallback) {

        //防止同一时间重复请求
        if (isConnecting) {
            LogUtils.e(TAG, "正在连接服务器，请勿重复连接 ...");
            return;
        }
        isConnecting = true;
        isReconnectAfterDisconnect = true;

        ADDR = addr;
        PORT = port;


        //移除延时请求
        handler_conn.removeMessages(0);

        //清楚上一次连接缓存
        CommandPool2.getInstance().removeAll();

        //发起连接
        connectCore = new ConnectCore() {
            @Override
            public void onMsgReceiver(LinkedList<Byte> byteList) {
                try {
//                    LogUtils.i(TAG, "onMsgReceiver 线程=" + Thread.currentThread().getName());
                    int len_byteList;
                    while ((len_byteList = byteList.size()) > 0) {
//                        LogUtils.d(TAG, "byteList.size=" + len_byteList);


                        if (len_byteList < len_head) {
                            return;
                        }

                        //解析proto的size字段
                        byte[] head_buffer = new byte[len_head];
                        for (int i = 0; i < head_buffer.length; i++) {
                            head_buffer[i] = byteList.get(i);
                        }

                        Package pkg_size = Package.parseFrom(head_buffer);
                        int len_pkg = pkg_size.getSize();

                        //解析pkg
                        if (len_byteList < len_pkg) {
//                            LogUtils.d(TAG, "整个package长度=" + pkg_size.toString() + " 继续读取此包 ... ...");
                            return;
                        }
//                        LogUtils.d(TAG, "整个package长度=" + pkg_size.toString() + " 此包读取结束");
                        byte[] pkg_buffer = new byte[len_pkg];

                        for (int i = 0; i < pkg_buffer.length; i++) {
                            pkg_buffer[i] = byteList.getFirst();
                            byteList.removeFirst();
                        }
//                        LogUtils.d(TAG, "解析package start ...");
                        final Package pkg = Package.parseFrom(pkg_buffer);
//                        LogUtils.d(TAG, "解析package结果:" + pkg.toString());

                        //解析data
//                        LogUtils.d(TAG, "aes dec start ...");
                        byte[] dec_re = AESUtils.dec(pkg.getData().toByteArray(), AES.getDecKey(pkg.getSeq()).getBytes());
//                        LogUtils.d(TAG, "aes dec end");

                        //解密后重新组装向下传递
//                        LogUtils.d(TAG, "解析message start ......");
                        final Msg.Message message = Msg.Message.parseFrom(dec_re);
//                        LogUtils.d(TAG, "解析message结果:" + message.toString());

                        //回调
                        if (Math.abs(pkg.getSeq() % 2) == 1) {
                            ThreadPool.getThread().execute(new Runnable() {
                                @Override
                                public void run() {
//                                    LogUtils.d(TAG, "回调 seq=" + pkg.getSeq());

                                    RequestInfo requestInfo = CommandPool2.getInstance().getRequestInfo((long) pkg.getSeq());

                                    if (requestInfo != null) {
                                        requestInfo.getListener().onResponse(message);
                                        CommandPool2.getInstance().remove((long) pkg.getSeq());
                                    } else {
//                                        LogUtils.e(TAG, "回调seq不存在，可能因超时被移除 seq=" + pkg.getSeq());
                                    }

                                }
                            });
                        }

                        //服务器推送消息
                        if (pkg.getSeq() % 2 == 0) {
                            ThreadPool.getThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    new PushMsgHelper().hand(message, pkg.getSeq());
                                }
                            });

                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onReadTimeout() {


                ThreadPool.getThread().execute(new Runnable() {
                    @Override
                    public void run() {

                        LogUtils.e(TAG, "onReadTimeout");

                        sendBroadCast("", "", Const.VIEW_STATUS_OFF_LINE);

                        CommandPool2.getInstance().check();

                        closeConnect();

                        connectRepeat();
                    }
                });


            }


            @Override
            public void onConnect(int status) {
                LogUtils.i(TAG, "onConnect  status=" + status);

                isConnecting = false;


                if (status == 0) {
                    LogUtils.i(TAG, "连接成功");
                    if (connectCallback != null) {
                        connectCallback.onSuccess();
                    }


                    if (Const.IS_OPEN_DYNAMIC_AESKEY) {
                        if (AppSettingUtil.checkDeviceKeyIsEmpty()) {
                            LogUtils.i(TAG, "发送key");
                            sendKey(addr, port);
                        } else if (!addr.equals(AppSettingUtil.getConfig().getServerIp())
                                || port != AppSettingUtil.getConfig().getServerPort()) {
                            LogUtils.i(TAG, "更换服务器 重新注册 发送key");
                            sendKey(addr, port);
                        } else {
                            LogUtils.i(TAG, "开始登录");
                            login();
                        }
                    } else {
                        login();
                    }


                } else if (status == 1) {
                    LogUtils.e(TAG, "连接服务器失败, 可能是网络环境差，或者服务器异常");

                    //延时重新连接
                    if (conn_repeat) {
                        toConnectDelay(5000);
                    }

                    if (connectCallback != null) {
                        connectCallback.onFailure();
                    }


                }
            }

            @Override
            public void onDisConnect() {

                sendBroadCast("", "", Const.VIEW_STATUS_OFF_LINE);

                CommandPool2.getInstance().check();

                //关闭
                closeConnect();

                //延时重新连接
                if (isReconnectAfterDisconnect) {
                    toConnectDelay(5000);
                }
            }
        };

        Context context = MyApplication.getContext();
        connectCore.connect(context, ADDR, PORT);

    }


    private static boolean isConnecting = false;

    private static void connectRepeat() {

        connect(ADDR, PORT, true, connectCallback);

    }


    private static Handler handler_conn = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

            connectRepeat();

            super.handleMessage(msg);
        }
    };

    private static void toConnectDelay(int delay_ms) {
        LogUtils.i(TAG, "延时" + delay_ms + "毫秒后再次连接服务器");
        handler_conn.removeMessages(0);
        handler_conn.sendEmptyMessageDelayed(0, delay_ms);
    }

    private static void removeConnectDelay() {
        handler_conn.removeMessages(0);
    }


    /**
     * 关闭连接
     */
    public static void closeConnect() {

        removeConnectDelay();

        isConnecting = false;

        if (connectCore != null) {
            connectCore.close();
        }

        CommandPool2.getInstance().removeAll();
        if (heartbeatHandler != null) {
            heartbeatHandler.removeCallbacksAndMessages(null);
        }
        if (handler_conn != null) {
            handler_conn.removeCallbacksAndMessages(null);
        }

    }

    /**
     * 关闭连接
     */
    public static void closeAndNotReconnect() {

        isReconnectAfterDisconnect = false;
        closeConnect();


    }


    /**
     * 连接中
     *
     * @return
     */
    public static boolean isConnecting() {
        return isConnecting;
    }


    public static ConnectCore getConnectCore() {
        return connectCore;
    }


    public static void login() {
        LogUtils.d(TAG, "login ...");
        Msg.Message.LoginReq LogUtilsinReq = Msg.Message.LoginReq.newBuilder()
                .setVer(1)
                .setSn(DeviceSnUtil.getDeviceSn())
                .build();
        Msg.Message message = Msg.Message.newBuilder()
                .setLoginReq(LogUtilsinReq)
                .build();

        new SendMsgHelper().request(message, new RequestCallback() {
            @Override
            public void onResponse(Msg.Message message) {
                LogUtils.d(TAG, "登录 响应");
                Msg.Message.LoginRsp loginRsp = message.getLoginRsp();
                LogUtils.d(TAG, "登录 success = " + loginRsp.getStatus());
                if (loginRsp.getStatus() == 0) {
                    LogUtils.d(TAG, "登录 成功 config = " + message.toString());
                    if (message.hasSetConfigReq()) {
                        AppSettingUtil.updateConfig(message);
                    }
//                checkPersonNoFaceFeature();/
                    //下载未完成的apk
                    DownloadApk.getInstance().downLoad(MyApplication.getContext());
                    //检查未上传的通行数据
                    UploadRecord.setUploadingFalse();
                    UploadRecord.upload();

                    heartbeatHandler.sendEmptyMessageDelayed(0, 5 * 1000);
                    //每次登陆成功后拉取全量数据
                    heartbeatHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new PullAllAuthority().getServerData();
                        }
                    }, Const.HANDLER_DELAY_TIME_50000);


                    if (!Const.IS_OPEN_DYNAMIC_AESKEY) {
                        ConfigBean config = AppSettingUtil.getConfig();
                        config.setServerPort(PORT);
                        config.setServerIp(ADDR);
                        config.setDeviceName(DeviceSnUtil.getDeviceSn());
                        if (config.getDeviceRegisterTime() == 0) {
                            config.setDeviceRegisterTime(System.currentTimeMillis() / 1000);
                        }
                        AppSettingUtil.saveConfig(config);
                    }
                } else if (loginRsp.getStatus() == 5) {
                    sendBroadCast("", "", Const.VIEW_STATUS_NOT_ALIVE);
                    //login
                    loginDelay();
                } else {
                    //login
                    loginDelay();
                }


                if (!Const.IS_OPEN_DYNAMIC_AESKEY) {
                    Intent intent = new Intent(Const.ACTION_CONNECT);
                    intent.putExtra(Const.CONNECT_STATE, loginRsp.getStatus());
                    LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);
                }


            }

            @Override
            public void onTimeout() {
                System.out.println("登录 timeout");
                closeConnect();
                connectRepeat();
            }
        });
    }


    private static void loginDelay() {
        ThreadPool.getThread().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                login();
            }
        });
    }


    //发起请求
    private static void sendHeart() {
        final long lastActionId = (long) SPUtils.get(MyApplication.getContext(), Const.DATABASE_LAST_ACTION_ID, 0L);
        String[] romSize = AppSettingUtil.getRomSize();
        String watchDogVersion = AppUtil.getWatchDogVersion(MyApplication.getContext());
        LogUtils.d(TAG, "====== 看门狗版本 = " + watchDogVersion);
        final Msg.Message.HeartBeatReq heartBeatReq = Msg.Message.HeartBeatReq.newBuilder()
                .setLastActionId(lastActionId)
                .setDeviceElapsedRealtime(SystemClock.elapsedRealtime() / 1000)
                .setDeviceSystemVersion(android.os.Build.VERSION.RELEASE)
                .setDeviceAppVersion(AppUtil.getAppVersionName(MyApplication.getContext()))
                .setDeviceSn(DeviceSnUtil.getDeviceSn())
                .setDeviceRegisterTime(AppSettingUtil.getConfig().getDeviceRegisterTime())
                .setDeviceRomAvailableSize(romSize[0] + " MB")
                .setDeviceRomSize(romSize[1] + " MB")
                .setDeviceCpuTemperature(HWUtil.getCpuTemp())
                .setDeviceIpAddress(HWUtil.getIPAddress())
                .setWatchdogVersion(watchDogVersion)
                .build();
        Msg.Message message = Msg.Message.newBuilder()
                .setHeartBeatReq(heartBeatReq)
                .build();
        new SendMsgHelper().request_custom_timeout(message, HEARTBEAT_TIMEOUT_MS, new RequestCallback() {
            @Override
            public void onResponse(Msg.Message message) {
                LogUtils.i(TAG, "心跳发送成功 ");

                Msg.Message.HeartBeatRsp heartBeatRsp = message.getHeartBeatRsp();
                String code = "";
                String type = "";
                if (heartBeatRsp.hasTemperature()) {
                    code = "  / " + heartBeatRsp.getTemperature() + Const.weather_Celsius;
                }
                if (heartBeatRsp.hasDayPicIndex()) {
                    type = heartBeatRsp.getDayPicIndex() + "";
                }
                if (heartBeatRsp.hasNightPicIndex()) {
                    type = heartBeatRsp.getNightPicIndex() + "";
                }

                sendBroadCast(type, code, Const.VIEW_STATUS_ON_LINE);
            }

            @Override
            public void onTimeout() {
                LogUtils.e(TAG, "心跳发送超时");

                sendBroadCast("", "", Const.VIEW_STATUS_OFF_LINE);

            }
        });
    }


    private static void sendKey(final String serverIp, final int port) {
        final String aesKey = String.format(Locale.getDefault(), "%016.0f", Math.random() * Math.pow(10, 16));
        final Msg.Message.RegisterDeviceReq registerDeviceReq = Msg.Message.RegisterDeviceReq.newBuilder()
                .setDeviceSn(DeviceSnUtil.getDeviceSn())
                .setDeviceKey(ByteString.copyFrom(aesKey.getBytes()))
                .setDeviceName(AppSettingUtil.getConfig().getDeviceName())
                .build();

        Msg.Message message = Msg.Message.newBuilder()
                .setRegisterDeviceReq(registerDeviceReq)
                .build();

        new SendMsgHelper().request_custom_timeout(message, Const.SOCKET_WAIT_TIME, new RequestCallback() {
            @Override
            public void onResponse(Msg.Message message) {
                //0:成功,1:失败
                Msg.Message.RegisterDeviceRsp registerDeviceRsp = message.getRegisterDeviceRsp();
                LogUtils.d(TAG, "设置key = " + registerDeviceRsp.getStatus());
                if (registerDeviceRsp.getStatus() == 0) {
                    AppSettingUtil.setDeviceAesKey(aesKey);
                    ConnectHandler.login();

                    ConfigBean config = AppSettingUtil.getConfig();
                    config.setServerPort(port);
                    config.setServerIp(serverIp);
                    config.setDeviceName(DeviceSnUtil.getDeviceSn());
                    if (config.getDeviceRegisterTime() == 0) {
                        config.setDeviceRegisterTime(System.currentTimeMillis() / 1000);
                    }
                    AppSettingUtil.saveConfig(config);
                    System.out.println(config.toString());
                }

                Intent intent = new Intent(Const.ACTION_CONNECT);
                intent.putExtra(Const.CONNECT_STATE, registerDeviceRsp.getStatus());
                LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);
            }

            @Override
            public void onTimeout() {
                Intent intent = new Intent(Const.ACTION_CONNECT);
                intent.putExtra(Const.CONNECT_STATE, -1);
                LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);
            }
        });
    }


    //开启下线view
    private static void sendBroadCast(String weatherType, String weatherCode, int status) {
        Intent intent = new Intent();
        intent.setAction(Const.WEATHER);
        intent.putExtra(Const.WEATHER_TYPE, weatherType);
        intent.putExtra(Const.WEATHER_CODE, weatherCode);
        intent.putExtra(Const.DEVICE_ON_LINE, status);
        LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(intent);

    }

}
