package com.thdtek.acs.terminal.ui.system;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.BaseActivity;
import com.thdtek.acs.terminal.dao.PersonDao;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.AppUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DeviceSnUtil;

import java.util.ArrayList;
import java.util.Locale;

public class SystemActivity extends BaseActivity {

    private ListView mListView;

    @Override
    public int getLayout() {
        return R.layout.activity_system;
    }

    @Override
    public void init() {
        mToolbar.setTitle(R.string.system_title);
    }

    @Override
    public void initView() {
        mListView = findViewById(R.id.listView);
        ArrayList<SystemBean> list = new ArrayList<>();
        list.add(new SystemBean("", getString(R.string.device_sn), DeviceSnUtil.getDeviceSn(), 1));
        list.add(new SystemBean("", getString(R.string.server_ip), AppSettingUtil.getConfig().getServerIp(), 1));
        list.add(new SystemBean("", getString(R.string.server_port_), AppSettingUtil.getConfig().getServerPort() + "", 1));
        list.add(new SystemBean("", getString(R.string.face_pair_number), String.format(Locale.getDefault(), "%.2f", AppSettingUtil.getConfig().getFaceFeaturePairNumber()), 1));
        list.add(new SystemBean("", getString(R.string.face_pair_type), parseCameraDetectType(), 1));
        list.add(new SystemBean("", getString(R.string.pair_success_wait_time), AppSettingUtil.getConfig().getFaceFeaturePairSuccessOrFailWaitTime() + "", 1));
        list.add(new SystemBean("", getString(R.string.open_door_type), parseOpenDoor(), 1));
        list.add(new SystemBean("", getString(R.string.open_door_conitue_time), AppSettingUtil.getConfig().getOpenDoorContinueTime() + "", 1));
        list.add(new SystemBean("", getString(R.string.open_door), AppSettingUtil.getConfig().getDoorType() == 0 ? getString(R.string.ji_dian_qi) : getString(R.string.wg), 1));
        list.add(new SystemBean("", getString(R.string.device_support_time), AppSettingUtil.getConfig().getDeviceDefendTime(), 1));
        list.add(new SystemBean("", getString(R.string.device_in_or_out), AppSettingUtil.getConfig().getDeviceIntoOrOut() == 0 ? getString(R.string.in) : getString(R.string.out), 1));
        list.add(new SystemBean("", getString(R.string.welcome_message), AppSettingUtil.getConfig().getAppWelcomeMsg(), 1));
        list.add(new SystemBean("", getString(R.string.app_max_memory), (float) (Runtime.getRuntime().maxMemory() * 1.0 / (1024 * 1024)) + " MB", 1));
        list.add(new SystemBean("", getString(R.string.app_use_memory), (float) (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024)) + "MB", 1));
        list.add(new SystemBean("", getString(R.string.system_version), android.os.Build.VERSION.RELEASE, 1));
        list.add(new SystemBean("", getString(R.string.app_version), AppUtil.getAppVersionName(this), 1));
        list.add(new SystemBean("", getString(R.string.device_register_time), String.format(Locale.getDefault(), "%tF %<tT", AppSettingUtil.getConfig().getDeviceRegisterTime() * 1000), 1));
        list.add(new SystemBean("", getString(R.string.device_open_conitue_time), String.format(Locale.getDefault(), "%.2f", SystemClock.elapsedRealtime() / 1000f / 60f / 60f) + getString(R.string.hour), 1));
        try {
            list.add(new SystemBean("", getString(R.string.device_people_size), PersonDao.getDao().queryBuilder().list().size() + "/8000", 1));
        } catch (Exception e) {
            list.add(new SystemBean("", getString(R.string.device_people_size), getString(R.string.read_person_size_fail), 1));
        }
        SystemAdapter systemAdapter = new SystemAdapter(this, list);
        mListView.setAdapter(systemAdapter);
    }

    @Override
    public void firstResume() {

    }

    @Override
    public void resume() {

    }

    public String parseCameraDetectType() {
        int cameraDetectType = AppSettingUtil.getConfig().getCameraDetectType();
        if (cameraDetectType == 0) {
            return getString(R.string.alive);
        } else {
            return getString(R.string.not_alive);
        }
    }

    public String parseOpenDoor() {
        int doorType = AppSettingUtil.getConfig().getOpenDoorType();
        if (doorType == Const.OPEN_DOOR_TYPE_FACE_ID) {
            return getString(R.string.face_and_id);
        } else if (doorType == Const.OPEN_DOOR_TYPE_I_C) {
            return getString(R.string.worke_id);
        } else if (doorType == Const.OPEN_DOOR_TYPE_FACE_IC) {
            return getString(R.string.face_and_ic);
        } else {
            return getString(R.string.face);
        }
    }

}
