package com.thdtek.acs.terminal.ui.manage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.anruxe.downloadlicense.HttpDownload;
import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.BaseActivity;
import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.base.ThreadPool;
import com.thdtek.acs.terminal.dao.FaceFeatureDao;
import com.thdtek.acs.terminal.dao.NowPicFeatureDao;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.ui.network.NetworkActivity;
import com.thdtek.acs.terminal.ui.server.ServerSettingActivity;
import com.thdtek.acs.terminal.haogonge.SettingForHaogongeActivity;
import com.thdtek.acs.terminal.ui.system.SystemActivity;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.FileUtil;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SPUtils;
import com.thdtek.acs.terminal.util.SwitchConst;
import com.thdtek.acs.terminal.util.ToastUtil;
import com.thdtek.acs.terminal.util.WeakHandler;

import java.io.File;

public class ManagerActivity extends BaseActivity implements View.OnClickListener, WeakHandler.WeakHandlerCallBack {


    private Button mBtnServer;
    private Button mBtnNetwork;
    private Button mBtnSystem;
    private Button mBtnOpen;
    private WeakHandler mWeakHandler;
    //    private Button mBtnClose;

    @Override
    public int getLayout() {
        return R.layout.activity_manager;
    }

    @Override
    public void init() {
        mToolbar.setTitle(R.string.manage_title);
        mWeakHandler = new WeakHandler(this);
    }

    @Override
    public void initView() {
        mBtnServer = findViewById(R.id.btn_server_setting);
//        mBtnNetwork = findViewById(R.id.btn_network_setting);
        mBtnSystem = findViewById(R.id.btn_system_setting);
        mBtnOpen = findViewById(R.id.btn_open);
//        mBtnClose = findViewById(R.id.btn_close);

        Button buttonOld = findViewById(R.id.btn_old);
        buttonOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Const.SDK_HONG_RUAN.equals(Const.SDK)) {
                    ToastUtil.showToast(ManagerActivity.this, getString(R.string.toast_hongtuan));
                    return;
                }
                SPUtils.put(MyApplication.getContext(), Const.SDK_FACE, Const.SDK_HONG_RUAN);
                //删除所有人员信息
                PersonDao.getDao().deleteAll();
                FaceFeatureDao.getDao().deleteAll();
                NowPicFeatureDao.getDao().deleteAll();
                FileUtil.deleteFile(Const.DIR_IMAGE_EMPLOYEE);
                HWUtil.reboot("切换成算法1.0,重启机器");

            }
        });
        Button buttonyuntianlifei = findViewById(R.id.btn_yuntianlifei);
        buttonyuntianlifei.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Const.SDK_YUN_TIAN_LI_FEI.equals(Const.SDK)) {
                    ToastUtil.showToast(ManagerActivity.this, getString(R.string.toast_yuntianlifei));
                    return;
                }
                SPUtils.put(MyApplication.getContext(), Const.SDK_FACE, Const.SDK_YUN_TIAN_LI_FEI);
                PersonDao.getDao().deleteAll();
                FaceFeatureDao.getDao().deleteAll();
                NowPicFeatureDao.getDao().deleteAll();
                FileUtil.deleteFile(Const.DIR_IMAGE_EMPLOYEE);
                HWUtil.reboot("切换成算法1.1,重启机器");
            }
        });
        Button mBtnLicense = findViewById(R.id.btn_license);
        mBtnLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoadLicense();
            }
        });

        mBtnServer.setOnClickListener(this);
//        mBtnNetwork.setOnClickListener(this);
        mBtnSystem.setOnClickListener(this);
        mBtnOpen.setOnClickListener(this);
//        mBtnClose.setOnClickListener(this);

    }

    private void downLoadLicense() {
        if (Const.SDK.equals(Const.SDK_HONG_RUAN)) {
            LogUtils.d(TAG, "当前算法是旧算法,不检测license");
            return;
        }
        File file = new File(Const.DIR_LICENSE + File.separator + "license");
        if (file.exists()) {
            LogUtils.d(TAG, "license 文件存在,不去网络下载");
            return;
        }
        LogUtils.e(TAG, "license 文件 不 存在,去网络下载");
        final android.support.v7.app.AlertDialog show = new android.support.v7.app.AlertDialog.Builder(ManagerActivity.this).setTitle(R.string.less_license).setCancelable(true).setMessage(R.string.license_down_loading).show();
        ThreadPool.getThread().execute(new Runnable() {
            @Override
            public void run() {
                int licenseFileCode = HttpDownload.getInstance().getLisenseFile(Const.SDK_YUN_TIAN_LI_FEI_KEY, Const.SDK_YUN_TIAN_LI_FEI_SECRET, MyApplication.getContext(), HttpDownload.DETECT_TYPE);
                if (licenseFileCode == 200) {
                    if (show != null) {
                        show.dismiss();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new android.support.v7.app.AlertDialog.Builder(ManagerActivity.this).setTitle(R.string.license_down_success).setCancelable(true).setMessage(R.string.license_down_success).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                        }
                    });
                } else {
                    if (show != null) {
                        show.dismiss();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            new android.support.v7.app.AlertDialog.Builder(ManagerActivity.this).setTitle(R.string.license_down_fail).setCancelable(true).setMessage(R.string.license_down_fail).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void firstResume() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_server_setting:
                handleServer();
                break;
//            case R.id.btn_network_setting:
//                handleNetwork();
//                break;
            case R.id.btn_system_setting:
                handleSystem();
                break;
            case R.id.btn_open:
                handleOpen();
                break;
//            case R.id.btn_close:
//                handleClose();
//                break;
            default:
                break;
        }
    }

    private void handleServer() {
        if(SwitchConst.IS_OPEN_SOCKET_MODE || SwitchConst.IS_OPEN_HTTP_MODE){
            startActivity(new Intent(this, ServerSettingActivity.class));
        }
        else if(SwitchConst.IS_OPEN_HAOGONGE_CLOUD){
            startActivity(new Intent(this, SettingForHaogongeActivity.class));
        }
    }

    private void handleNetwork() {
        startActivity(new Intent(this, NetworkActivity.class));
    }

    private void handleSystem() {
        startActivity(new Intent(this, SystemActivity.class));
    }

    private void handleOpen() {
        HWUtil.openDoorRelay();
        mWeakHandler.sendEmptyMessageDelayed(1, Const.HANDLER_DELAY_TIME_1000);
    }

    private void handleClose() {
        HWUtil.closeDoor();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void handleMessage(Message message) {
        HWUtil.closeDoor();
    }
}
