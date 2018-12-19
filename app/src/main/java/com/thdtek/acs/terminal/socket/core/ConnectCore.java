package com.thdtek.acs.terminal.socket.core;


import android.content.Context;
import android.os.SystemClock;

import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.base.ThreadPool;
import com.thdtek.acs.terminal.thread.ThreadManager;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SwitchConst;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public abstract class ConnectCore {

    private static final String TAG = ConnectCore.class.getSimpleName();
    //网络输入流
    private InputStream mIs;
    //网络输出流
    private OutputStream mOs;
    private Socket mSocket;
    private ReadThread readThread;
    private WriteThread writeThread;
    //连接超时
    private static final int connTimeout = 10 * 1000;
    //读写超时
    private static final long readTimeout = 61 * 1000;
    private static final long writeTimeout = 61 * 1000;
    //ssl
    private SSLContext sslContext = null;
    private static final String KEYSTOREPASSWORD = "8435116";    //密钥库密码
    private static final String KEYSTOREPATH_TRUST = "tclient.bks";        //信任密钥库


    //当连接有结果时调用
    public abstract void onConnect(int status);//0:连接成功    1:连接失败

    //连接断开
    public abstract void onDisConnect();

    //当收到服务器消息时调用
    public abstract void onMsgReceiver(LinkedList<Byte> byteList);

    //输入流超时
    public abstract void onReadTimeout();


    //发起连接
    public void connect(final Context context, final String addr, final int port) {
        LogUtils.i(TAG, "connect " + addr + ":" + port + " ......");

        sendHeartQueue.clear();
        sendQueue.clear();
        ThreadPool.getThread().getQueue().clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (SwitchConst.IS_OPEN_SSL) {
                        //取得TLS协议的SSLContext实例
                        sslContext = SSLContext.getInstance("TLSv1");
                        //取得BKS类型的本地密钥库实例，这里特别注意：手机只支持BKS密钥库，不支持Java默认的JKS密钥库
                        //初始化
//                    KeyStore clientkeyStore = KeyStore.getInstance("BKS");
//                    clientkeyStore.load(context.getResources().getAssets().open(KEYSTOREPATH_CLIENT),KEYSTOREPASSWORD.toCharArray());
                        KeyStore trustkeyStore = KeyStore.getInstance("BKS");
                        trustkeyStore.load(context.getResources().getAssets().open(KEYSTOREPATH_TRUST), KEYSTOREPASSWORD.toCharArray());
                        //获得X509密钥库管理实例
                        KeyManagerFactory keyManagerFactory = KeyManagerFactory
                                .getInstance("X509");
                        keyManagerFactory.init(null, KEYSTOREPASSWORD.toCharArray());

                        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                                .getInstance("X509");
                        trustManagerFactory.init(trustkeyStore);
                        //初始化SSLContext实例
                        sslContext.init(keyManagerFactory.getKeyManagers(),
                                trustManagerFactory.getTrustManagers(), null);
                        LogUtils.i(TAG, "SSLContext初始化完毕...");
                        //以下两步获得SSLSocket实例
                        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


//                    mSSLSocket = (SSLSocket) sslSocketFactory.createSocket(addr,port);
                        LogUtils.i(TAG, "获得SSLSocket成功...");


                        mSocket = (SSLSocket) sslSocketFactory.createSocket();
                        LogUtils.i(TAG, "获得未连接SSLSocket成功...");

                        SocketAddress address = new InetSocketAddress(addr, port);
                        LogUtils.i(TAG, "connect start");
                        mSocket.connect(address, connTimeout);
                        LogUtils.i(TAG, "connect end");
                    } else {
                        InetSocketAddress inetSocketAddress = new InetSocketAddress(addr, port);
                        mSocket = new Socket();
                        mSocket.connect(inetSocketAddress, connTimeout);
                    }


                    mIs = mSocket.getInputStream();
                    LogUtils.i(TAG, "获取InputStream成功...");
                    mOs = mSocket.getOutputStream();
                    LogUtils.i(TAG, "获取OutputStream成功...");



                    //读线程实例化
                    if (readThread == null || !readThread.isAlive()) {
                        LogUtils.d(TAG, "读线程开启");
                        readThread = new ReadThread();
                        readThread.start();
                    }


                    //写线程实例化
                    if (writeThread == null) {
                        LogUtils.d(TAG, "写线程开启");
                        writeThread = new WriteThread("socket输出流线程");
                        writeThread.start();
                    }


                    //监听空闲状态
                    if (idleStateThread == null) {
                        LogUtils.d(TAG, "监控空闲状态线程开启");
                        idleStateThread = new IdleStateThread();
                        idleStateThread.start();
                    }


                    onConnect(0);


                } catch (Exception e) {
                    e.printStackTrace();

                    onConnect(1);
                }
            }
        }).start();

    }

    //关闭连接
    public void close() {
        LogUtils.e(TAG, "关闭socket连接 ... ...");

        startRead = false;
        startWrite = false;
        startControl = false;

        try {
            if (writeThread != null) {
                writeThread.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (readThread != null) {
                readThread.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (idleStateThread != null) {
                idleStateThread.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ThreadPool.getThread().getQueue().clear();
        try {
            mSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            byteList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            sendQueue.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            sendHeartQueue.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //发送数据包
    public void send(Msg.Package pkg) {
        try {
            sendQueue.put(pkg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //发送数据包
    public void sendHeart(Msg.Package pkg) {
        try {
            sendHeartQueue.put(pkg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //写入网络流线程
    private boolean startWrite = true;
    private ArrayBlockingQueue<Msg.Package> sendQueue = new ArrayBlockingQueue<Msg.Package>(1024*10, true);
    private ArrayBlockingQueue<Msg.Package> sendHeartQueue = new ArrayBlockingQueue<Msg.Package>(1024*10, true);

    private class WriteThread extends Thread {
        public WriteThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            super.run();
            while (startWrite) {
                try {
                    Msg.Package pkg = null;
                    pkg = sendHeartQueue.poll();
                    if (pkg == null) {
                        pkg = sendQueue.poll();
                    }
                    if (pkg != null) {
                        pkg.writeTo(mOs);
                    } else {
                        SystemClock.sleep(50);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            LogUtils.e(TAG, "输出流线程结束");
        }
    }

    //读取网络输入流线程
    private boolean startRead = true;
    private LinkedList<Byte> byteList = new LinkedList<>();

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                LogUtils.d(TAG, "线程ReadThread=" + Thread.currentThread().getName());
                byte[] bytes = new byte[10 * 1024];
                int len;
                while (startRead && (len = mIs.read(bytes)) != -1) {
//                    LogUtils.d(TAG,"单次读取数据len="+len);

                    readLastTimestamp = System.currentTimeMillis();

                    List<Byte> lst = new ArrayList<>();
                    for (int i = 0; i < len; i++) {
                        lst.add(bytes[i]);
                    }
                    byteList.addAll(lst);
//                    LogUtils.d(TAG, "byteList.size="+byteList.size());

                    onMsgReceiver(byteList);
                }
            } catch (Exception e) {
                e.printStackTrace();

            } finally {

                try {
                    readThread = null;
                    LogUtils.e(TAG, "读取输入流线程结束");
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

            }


            //连接断开
            onDisConnect();

        }

    }


    /**
     * 返回毫秒
     *
     * @return 输入流空闲状态时间
     */
    public long getReadIdleStateTime() {
        return System.currentTimeMillis() - readLastTimestamp;
    }

    //监控输入输出流空闲状态

    //每次读取输入流后保存时间戳
    private IdleStateThread idleStateThread;
    private long readLastTimestamp;
    private boolean startControl = true;

    private class IdleStateThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (startControl) {
                try {

                    Thread.sleep(1000);

//                    LogUtils.d(TAG, "读取流空闲时间="+getReadIdleStateTime());

                    //读取超时
                    if (readLastTimestamp != 0 && getReadIdleStateTime() >= readTimeout) {



                        onReadTimeout();

                        readLastTimestamp = 0;
                        idleStateThread = null;
                        break;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            LogUtils.e(TAG, "监控流空隙状态线程结束");

        }
    }

}
