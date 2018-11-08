package com.thdtek.acs.terminal.ui.welcome;

import android.content.Intent;
import android.text.TextUtils;

import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.BaseActivity;
import com.thdtek.acs.terminal.bean.ConfigBean;
import com.thdtek.acs.terminal.ui.cameraphoto.CameraPhotoActivity;
import com.thdtek.acs.terminal.ui.login.LoginActivity;
import com.thdtek.acs.terminal.ui.main.MainActivity;
import com.thdtek.acs.terminal.ui.main.MainActivity2;
import com.thdtek.acs.terminal.ui.main.MainActivity3;
import com.thdtek.acs.terminal.ui.server.ServerSettingActivity;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DBUtil;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SPUtils;

import greendao.ConfigBeanDao;

public class WelcomeActivity extends BaseActivity {

    private static final String TAG = WelcomeActivity.class.getSimpleName();


    @Override
    public int getLayout() {
        return R.layout.activity_welcome;
    }

    @Override
    public void init() {
        if (Const.IS_OPEN_HTTP_MODE) {
            ConfigBean config = AppSettingUtil.getConfig(true);
            if (config.getDeviceRegisterTime() == 0) {
                config.setDeviceRegisterTime(System.currentTimeMillis());
                ConfigBeanDao configBeanDao = DBUtil.getDaoSession().getConfigBeanDao();
                configBeanDao.update(config);
            }
        }
    }

    @Override
    public void initView() {
//        startActivityForResult(new Intent(this, LoginActivity.class), 0);
//        startActivityForResult(new Intent(this, CameraPhotoActivity.class), 0);
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        LogUtils.d(TAG, "========== 屏幕宽度 = " + widthPixels);
        if (!Const.IS_OPEN_SOCKET_MODE) {
            startActivityForResult(new Intent(this, MainActivity.class),0);
        } else {
            if (Const.IS_OPEN_DYNAMIC_AESKEY) {
                if (AppSettingUtil.checkServerIpAndPortIsEmpty() && AppSettingUtil.checkDeviceKeyIsEmpty()) {
                    HWUtil.showStatusBarAndNaviBar(this);
                    startActivityForResult(new Intent(this, ServerSettingActivity.class), 0);
                } else {
                    if (widthPixels == Const.WINDOW_SIZE_WIDTH_1920) {
                        //13.3存机器
                        startActivityForResult(new Intent(this, MainActivity3.class), 0);
                    } else {
                        startActivityForResult(new Intent(this, MainActivity.class), 0);
                    }
                }
            } else {
                Long count = (Long) SPUtils.get(WelcomeActivity.this, Const.START_COUNT, 0L);
                LogUtils.i(TAG, "本次是第几" + count + "次启动");
                if (count == 1) {
                    HWUtil.showStatusBarAndNaviBar(this);
                    startActivityForResult(new Intent(this, ServerSettingActivity.class), 0);
                } else {
                    if (widthPixels == Const.WINDOW_SIZE_WIDTH_1920) {
                        //13.3存机器
                        startActivityForResult(new Intent(this, MainActivity3.class), 0);
                    } else {
                        startActivityForResult(new Intent(this, MainActivity.class), 0);
                    }
                }
            }
        }
    }

    @Override
    public void firstResume() {
        LogUtils.d(TAG, "======================== firstResume ========================");
    }

    @Override
    public void resume() {
        LogUtils.d(TAG, "======================== resume ========================");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d(TAG, "======================== onStop ========================");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        if (resultCode == RESULT_OK) {
            if (widthPixels == Const.WINDOW_SIZE_WIDTH_1920) {
                //13.3存机器
                startActivity(new Intent(this, MainActivity3.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
        }
        finish();
    }
}
