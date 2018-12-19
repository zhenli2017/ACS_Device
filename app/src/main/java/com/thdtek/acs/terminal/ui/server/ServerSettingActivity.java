package com.thdtek.acs.terminal.ui.server;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.anruxe.downloadlicense.HttpDownload;
import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.BaseActivity;
import com.thdtek.acs.terminal.base.BaseCameraActivity;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.base.ThreadPool;
import com.thdtek.acs.terminal.bean.ConfigBean;
import com.thdtek.acs.terminal.socket.core.ConnectCallback;
import com.thdtek.acs.terminal.socket.core.ConnectHandler;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DeviceSnUtil;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SPUtils;
import com.thdtek.acs.terminal.util.SwitchConst;
import com.thdtek.acs.terminal.util.ToastUtil;
import com.thdtek.acs.terminal.util.WeakHandler;
import com.thdtek.acs.terminal.view.CustomEditText;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ServerSettingActivity extends BaseActivity implements View.OnClickListener, WeakHandler.WeakHandlerCallBack {

    private final String TAG = ServerSettingActivity.class.getSimpleName();
    private CustomEditText mEtIP;
    private CustomEditText mEtPort;
    private Button mBtnConnect;
    private CustomEditText mEtDeviceName;
    private WeakHandler mWeakHandler;
    private SendKeyReceiver mSendKeyReceiver;
    private ProgressDialog mProgressDialog;
    private EditText mEtMainTitle;
    private Button mBtnClientTitle;
    private LinearLayout layout_socket;
    private Button mBtnLicense;

    @Override
    public int getLayout() {
        return R.layout.activity_server_setting;
    }

    @Override
    public void init() {
        mToolbar.setTitle(R.string.server_title);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back(false);
            }
        });
    }

    @Override
    public void initView() {

        mEtIP = findViewById(R.id.et_ip_or);
        mEtPort = findViewById(R.id.et_port);
        final ConfigBean config = AppSettingUtil.getConfig();
        //47.74.130.48
        LogUtils.d(TAG, "==== config.getServerIp() = " + config.getServerIp());
        LogUtils.d(TAG, "==== getDeviceAesKey = " + AppSettingUtil.getDeviceAesKey());
        LogUtils.d(TAG, "==== config.getServerPort() = " + config.getServerPort());
        if (TextUtils.isEmpty(config.getServerIp())
                || TextUtils.isEmpty(AppSettingUtil.getDeviceAesKey())
                || config.getServerPort() == 0) {
//            mEtIP.setText("132.232.4.69");
//            mEtIP.setSelection("132.232.4.69".length());


            sendUdp();
        } else {
            mEtIP.setText(config.getServerIp());
            mEtIP.setSelection(config.getServerIp().length());
            mEtPort.setText(config.getServerPort() + "");
            mEtPort.setSelection((config.getServerPort() + "").length());
        }
        mBtnConnect = findViewById(R.id.btn_connect_test);
        mEtDeviceName = findViewById(R.id.et_device_name);
        mEtDeviceName.setText(DeviceSnUtil.getDeviceSn());
        mEtDeviceName.setSelection(DeviceSnUtil.getDeviceSn().length());

        mBtnConnect.setOnClickListener(this);

        mWeakHandler = new WeakHandler(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.ACTION_CONNECT);
        mSendKeyReceiver = new SendKeyReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mSendKeyReceiver, intentFilter);


        mEtMainTitle = findViewById(R.id.et_main_title);
        String title = (String) SPUtils.get(this, Const.MAIN_TITLE, Const.MAIN_TITLE_DEFAULT);
        mEtMainTitle.setText(title);
        mBtnClientTitle = findViewById(R.id.btn_set_name);
        mBtnClientTitle.setOnClickListener(this);


        layout_socket = findViewById(R.id.layout_socket);
        if (!SwitchConst.IS_OPEN_SOCKET_MODE) {
            layout_socket.setVisibility(View.INVISIBLE);
        }

        findViewById(R.id.btn_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigBean config1 = AppSettingUtil.getConfig();
                config1.setServerPort(0);
                config1.setServerIp("");
                AppSettingUtil.saveConfig(config1);
                AppSettingUtil.setDeviceAesKey("");
                mEtIP.setText("");
                mEtPort.setText("");
            }
        });
        findViewById(R.id.btn_auto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUdp();
            }
        });
    }

    public void sendUdp() {
        mProgressDialog = new ProgressDialog(ServerSettingActivity.this);
        mProgressDialog.setMessage(getString(R.string.server_get_server_ip));
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = 2;
                try {
                    SystemClock.sleep(500);
                    message.obj = udp();
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e(TAG, "UDP error  = " + e.getMessage());
                    message.obj = "";
                }
                mWeakHandler.sendMessageDelayed(message, Const.HANDLER_DELAY_TIME_1000);
            }
        }).start();
    }

    public String udp() throws IOException {

        String ipAddress = HWUtil.getIPAddress();
        if (ipAddress.equals("0.0.0.0")) {
            return "";
        }
        String[] split = ipAddress.split("\\.");
        split[3] = "255";
        ipAddress = split[0] + "." + split[1] + "." + split[2] + "." + split[3];
        LogUtils.d(TAG, "本机的ip网段的广播地址 = " + ipAddress);


        // 创建一个数据报套接字，并将其绑定到指定port上
        DatagramSocket datagramSocket = new DatagramSocket(null);
        datagramSocket.setReuseAddress(true);
//        InetAddress byName = InetAddress.getByName(ipAddress);
        InetSocketAddress byName = new InetSocketAddress(6000);
        datagramSocket.bind(byName);

        // DatagramPacket(byte buf[], int length),建立一个字节数组来接收UDP包
        datagramSocket.setSoTimeout(8000);
        byte[] buf = "THD".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, InetAddress.getByName(ipAddress), 15000);
        // 发送消息
        datagramSocket.send(sendPacket);
        byte[] bytes = new byte[1024];


        DatagramPacket receivePacket = new DatagramPacket(bytes, bytes.length);
        // receive()来等待接收UDP数据报
        datagramSocket.receive(receivePacket);
        String string = new String(receivePacket.getData(), 0, receivePacket.getLength());
        datagramSocket.close();
        return receivePacket.getAddress() + "&" + string;
    }

    @Override
    public void firstResume() {

    }

    @Override
    public void resume() {
//        checkTempSn();
        HWUtil.showStatusBarAndNaviBar(this);
    }

    private AlertDialog mBuilder;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_connect_test:
                connect();
                break;
            case R.id.btn_set_name:
                clientTitle();
                break;
            default:
                break;
        }
    }


    private void connect() {


        final String ip = mEtIP.getText().toString();
        String port = mEtPort.getText().toString();
        int portI = 0;
        try {
            portI = Integer.parseInt(port);
        } catch (Exception e) {
            ToastUtil.showToast(this, getString(R.string.server_port_error));
            return;
        }
        String deviceName = mEtDeviceName.getText().toString();
        if (TextUtils.isEmpty(deviceName)) {
            ToastUtil.showToast(this, getString(R.string.server_divece_name_null));
            return;
        }
        ConfigBean config = AppSettingUtil.getConfig();
        String serverIp = config.getServerIp();
        int serverPort = config.getServerPort();

        if (!serverIp.equals(ip) || serverPort != portI) {
            System.out.println("===============================================");
            //1.创建一个ProgressDialog的实例
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = new ProgressDialog(ServerSettingActivity.this);
            mProgressDialog.setMessage(getString(R.string.server_connecting));
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();

            AppSettingUtil.getConfig().setDeviceName(deviceName);

            final int finalPortI = portI;
            ThreadPool.getThread().execute(new Runnable() {
                @Override
                public void run() {

                    ConnectHandler.closeAndNotReconnect();

                    LogUtils.i(TAG, "connect ip=" + ip);
                    LogUtils.i(TAG, "connect port=" + finalPortI);
                    ConnectHandler.connect(ip, finalPortI, false, new ConnectCallback() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mProgressDialog != null) {
                                        mProgressDialog.dismiss();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure() {
                            ConnectHandler.closeConnect();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mProgressDialog != null) {
                                        mProgressDialog.dismiss();
                                    }
                                    //连接失败
                                    LogUtils.i(TAG, "连接失败 dialog 1");
                                    mBuilder = new AlertDialog.Builder(ServerSettingActivity.this)
                                            .setTitle(R.string.server_connect_fail)
                                            .setMessage("")
                                            .setCancelable(true)
                                            .setPositiveButton(R.string.server_close, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (mBuilder != null) {
                                                        mBuilder.dismiss();
                                                    }
                                                }
                                            })
                                            .create();
                                    mBuilder.show();
                                }
                            });
                        }
                    });
                }
            });
        } else {
            ToastUtil.showToast(this, getString(R.string.server_ip_port_exist));
        }
    }

    private void clientTitle() {
        String trim = mEtMainTitle.getText().toString().trim();
        if (TextUtils.isEmpty(trim)) {
            ToastUtil.showToast(ServerSettingActivity.this, getString(R.string.server_input_null));
            return;
        }
        SPUtils.put(this, Const.MAIN_TITLE, trim);
        ToastUtil.showToast(this, getString(R.string.server_set_success));
    }

    @Override
    public void onBackPressed() {
        back(false);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSendKeyReceiver);
        super.onDestroy();
    }

    private void back(boolean ok) {
        if (ok) {
            setResult(RESULT_OK);
        }
        finish();
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                if (mBuilder != null) {
                    mBuilder.dismiss();
                    back(true);
                }
                break;
            case 2:
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                String msg = (String) message.obj;
                String[] split = msg.split("&");
                if (split.length != 2) {
                    showUdpFail();
                    return;
                }
                mEtIP.setText(split[0].replaceAll("/", ""));
                mEtPort.setText(split[1]);
                connect();

                break;
            case 3:
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                mEtIP.setText("132.232.4.69");
                mEtIP.setSelection("132.232.4.69".length());
                mEtPort.setText("16005");
                mEtPort.setSelection("16005".length());
                break;
            case 4:
                LogUtils.i(TAG, "连接成功 dialog 2");
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mWeakHandler.removeMessages(2);
                }
                //连接成功
                mBuilder = new AlertDialog.Builder(ServerSettingActivity.this)
                        .setTitle(R.string.server_connet_success)
                        .setMessage("")
                        .setCancelable(false)
                        .create();
                mBuilder.show();
                mWeakHandler.sendEmptyMessageDelayed(1, Const.HANDLER_DELAY_TIME_1000);
                break;
            case 5:
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mWeakHandler.removeMessages(2);
                }
                if (mBuilder != null && mBuilder.isShowing()) {
                    mBuilder.dismiss();
                }
                //连接失败
                LogUtils.i(TAG, "连接失败 dialog 2");
                mBuilder = new AlertDialog.Builder(ServerSettingActivity.this)
                        .setTitle(R.string.server_connect_fail)
                        .setMessage("")
                        .setCancelable(true)
                        .setPositiveButton(R.string.server_close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mBuilder != null) {
                                    mBuilder.dismiss();
                                }
                            }
                        })
                        .create();
                mBuilder.show();
                ThreadPool.getThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        ConnectHandler.closeConnect();
                    }
                });
                break;
            default:
                break;
        }
    }

    private void showUdpFail() {
        mProgressDialog = new ProgressDialog(ServerSettingActivity.this);
        mProgressDialog.setMessage(getString(R.string.server_get_server_ip_fail));
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        mWeakHandler.sendEmptyMessageDelayed(3, Const.HANDLER_DELAY_TIME_2000);
    }

    private class SendKeyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(Const.CONNECT_STATE, -1);
            if (status == 0) {
                if (mWeakHandler != null) {
                    mWeakHandler.sendEmptyMessage(4);
                }
            } else {
                if (mWeakHandler != null) {
                    mWeakHandler.sendEmptyMessage(5);
                }
            }
        }
    }

}
