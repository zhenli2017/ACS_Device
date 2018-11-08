package com.thdtek.acs.terminal.ui.server;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.BaseActivity;
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
import com.thdtek.acs.terminal.util.ToastUtil;
import com.thdtek.acs.terminal.util.WeakHandler;
import com.thdtek.acs.terminal.view.CustomEditText;

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
        ConfigBean config = AppSettingUtil.getConfig();
        //47.74.130.48
        if (TextUtils.isEmpty(config.getServerIp()) || TextUtils.isEmpty(AppSettingUtil.getDeviceAesKey())) {
            mEtIP.setText("132.232.4.69");
            mEtIP.setSelection("132.232.4.69".length());
        } else {
            mEtIP.setText(config.getServerIp());
            mEtIP.setSelection(config.getServerIp().length());
        }
        mEtPort = findViewById(R.id.et_port);
        mEtPort.setText("16005");
        mEtPort.setSelection("16005".length());
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
        if(!Const.IS_OPEN_SOCKET_MODE){
            layout_socket.setVisibility(View.INVISIBLE);
        }
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
            ToastUtil.showToast(this, "端口设置错误,请重新设置");
            return;
        }
        String deviceName = mEtDeviceName.getText().toString();
        if (TextUtils.isEmpty(deviceName)) {
            ToastUtil.showToast(this, "设备名称不能为空");
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
            mProgressDialog.setMessage("正在连接,请稍后...");
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();

            AppSettingUtil.getConfig().setDeviceName(deviceName);

            final int finalPortI = portI;
            ThreadPool.getThread().execute(new Runnable() {
                @Override
                public void run() {

                    ConnectHandler.closeAndNotReconnect();

                    LogUtils.i(TAG, "connect ip="+ip);
                    LogUtils.i(TAG, "connect port="+finalPortI);
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mProgressDialog != null) {
                                        mProgressDialog.dismiss();
                                    }
                                    //连接失败
                                    LogUtils.i(TAG, "连接失败 dialog 1");
                                    mBuilder = new AlertDialog.Builder(ServerSettingActivity.this)
                                            .setTitle("连接状态")
                                            .setMessage("连接失败")
                                            .setCancelable(true)
                                            .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
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
            ToastUtil.showToast(this, "IP 和 端口 已经存在");
        }

    }

    private void clientTitle() {
        String trim = mEtMainTitle.getText().toString().trim();
        if (TextUtils.isEmpty(trim)) {
            ToastUtil.showToast(ServerSettingActivity.this, "输入不能为空");
            return;
        }
        SPUtils.put(this, Const.MAIN_TITLE, trim);
        ToastUtil.showToast(this, "设置成功");
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

                break;
            default:
                break;
        }
    }

    private class SendKeyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(Const.CONNECT_STATE, -1);
            if (status == 0) {
                LogUtils.i(TAG, "连接成功 dialog 2");
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mWeakHandler.removeMessages(2);
                }
                //连接成功
                mBuilder = new AlertDialog.Builder(ServerSettingActivity.this)
                        .setTitle("连接状态")
                        .setMessage("连接成功")
                        .setCancelable(false)
                        .create();
                mBuilder.show();
                mWeakHandler.sendEmptyMessageDelayed(1, Const.HANDLER_DELAY_TIME_1000);
            } else {
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
                        .setTitle("连接状态")
                        .setMessage("连接失败")
                        .setCancelable(true)
                        .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
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
        }
    }

}
