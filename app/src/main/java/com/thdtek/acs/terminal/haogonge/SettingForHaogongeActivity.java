package com.thdtek.acs.terminal.haogonge;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.BaseActivity;
import com.thdtek.acs.terminal.bean.ConfigBean;
import com.thdtek.acs.terminal.util.AppSettingUtil;
import com.thdtek.acs.terminal.util.AppUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.DeviceSnUtil;
import com.thdtek.acs.terminal.util.HWUtil;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SPUtils;

public class SettingForHaogongeActivity extends BaseActivity {
    private final String TAG = SettingForHaogongeActivity.class.getSimpleName();

    private TextView tv_save;
    private Button bt_1;
    private Button bt_2;
    private LinearLayout page_1;
    private LinearLayout page_2;
    private EditText et_sn;
    private EditText et_ip;
    private EditText et_url;
    private EditText et_model;
    private EditText et_code;
    private EditText et_mac;
    private EditText et_interface_version;
    private EditText et_card_id;
    private EditText et_compare_threshold;
    private EditText et_huoti;

    private String sn ;
    private String ip ;
    private String url ;
    private String model;
    private String code ;

    private String mac ;
    private String interface_version ;
    private String card_id ;
    private String compare_threshold;
    private String huoti;

    @Override
    public int getLayout() {
        return R.layout.activity_setting_for_haogonge;
    }

    @Override
    public void init() {
        mToolbar.setTitle(R.string.server_title);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        sn = DeviceSnUtil.getDeviceSn();
        ip = HWUtil.getIPAddress();
        url = (String) SPUtils.get(this, Const.haogonge_url, "");
        model = (String) SPUtils.get(this, Const.haogonge_model, "");
        code = (String) SPUtils.get(this, Const.haogonge_code, "");

        mac = HWUtil.getEthernetMac();
        interface_version = (String) SPUtils.get(this, Const.haogonge_interface_version, "");
        card_id = (String) SPUtils.get(this, Const.haogonge_card_id, "");
        compare_threshold = String.valueOf(AppSettingUtil.getConfig().getFaceFeaturePairNumber());
        huoti = String.valueOf(AppSettingUtil.getConfig().getCameraDetectType());



    }

    private void saveData(){
        sn = et_sn.getText().toString().trim();
        ip = et_ip.getText().toString().trim();
        url = et_url.getText().toString().trim();
        model = et_model.getText().toString().trim();
        code = et_code.getText().toString().trim();

        mac = et_mac.getText().toString().trim();
        interface_version = et_interface_version.getText().toString().trim();
        card_id = et_card_id.getText().toString().trim();
        compare_threshold = et_compare_threshold.getText().toString().trim();
        huoti = et_huoti.getText().toString().trim();

        LogUtils.d(TAG, "url="+url);


        SPUtils.put(SettingForHaogongeActivity.this, Const.haogonge_sn, sn);
        SPUtils.put(SettingForHaogongeActivity.this, Const.haogonge_ip, ip);
        SPUtils.put(SettingForHaogongeActivity.this, Const.haogonge_url, url);
        SPUtils.put(SettingForHaogongeActivity.this, Const.haogonge_model, model);
        SPUtils.put(SettingForHaogongeActivity.this, Const.haogonge_code, code);

        SPUtils.put(SettingForHaogongeActivity.this, Const.haogonge_mac, mac);
        SPUtils.put(SettingForHaogongeActivity.this, Const.haogonge_interface_version, interface_version);
        SPUtils.put(SettingForHaogongeActivity.this, Const.haogonge_card_id, card_id);
        SPUtils.put(SettingForHaogongeActivity.this, Const.haogonge_compare_threshold, compare_threshold);
        SPUtils.put(SettingForHaogongeActivity.this, Const.haogonge_huoti, huoti);

        Float faceFeaturePairNumber = Float.parseFloat(compare_threshold);
        ConfigBean configBean = AppSettingUtil.getConfig();
        configBean.setFaceFeaturePairNumber(faceFeaturePairNumber);
        configBean.setCameraDetectType(Integer.parseInt(huoti));
        AppSettingUtil.saveConfig(configBean);
    }

    @Override
    public void initView() {
        tv_save = findViewById(R.id.tv_save);

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();

                Toast.makeText(SettingForHaogongeActivity.this, R.string.setting_for_haogonge_save_success, Toast.LENGTH_LONG).show();
            }
        });

        bt_1 = findViewById(R.id.bt_1);
        bt_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page_1.setVisibility(View.VISIBLE);
                page_2.setVisibility(View.GONE);
            }
        });

        bt_2 = findViewById(R.id.bt_2);
        bt_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page_1.setVisibility(View.GONE);
                page_2.setVisibility(View.VISIBLE);
            }
        });

        page_1 = findViewById(R.id.page_1);
        page_2 = findViewById(R.id.page_2);

        page_1.setVisibility(View.VISIBLE);
        page_2.setVisibility(View.GONE);

        et_sn = findViewById(R.id.et_sn);
        et_ip = findViewById(R.id.et_ip);
        et_url = findViewById(R.id.et_url);
        et_model = findViewById(R.id.et_model);
        et_code = findViewById(R.id.et_code);
        et_mac = findViewById(R.id.et_mac);
        et_interface_version = findViewById(R.id.et_interface_version);
        et_card_id = findViewById(R.id.et_card_id);
        et_compare_threshold = findViewById(R.id.et_compare_threshold);
        et_huoti = findViewById(R.id.et_huoti);

        et_sn.setText(sn);
        et_ip.setText(ip);
        et_url.setText(url);
        et_model.setText(model);
        et_code.setText(code);
        et_mac.setText(mac);
        et_interface_version.setText(interface_version);
        et_card_id.setText(card_id);
        et_compare_threshold.setText(compare_threshold);
        et_huoti.setText(huoti);

        et_sn.setEnabled(false);
        et_sn.setFocusable(false);
        et_ip.setEnabled(false);
        et_ip.setFocusable(false);
        et_mac.setEnabled(false);
        et_mac.setFocusable(false);

    }

    @Override
    public void firstResume() {

    }

    @Override
    public void resume() {

    }

}
