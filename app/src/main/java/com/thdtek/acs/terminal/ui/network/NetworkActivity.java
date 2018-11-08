package com.thdtek.acs.terminal.ui.network;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.BaseActivity;
import com.thdtek.acs.terminal.util.AppUtil;
import com.thdtek.acs.terminal.util.Const;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.ShellUtils;
import com.thdtek.acs.terminal.view.CustomEditText;

import java.util.ArrayList;
import java.util.HashMap;

public class NetworkActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = NetworkActivity.class.getSimpleName();

    private LinearLayout mLineStatic;
    private Button mBtnSave;
    private CustomEditText mEtDns;
    private CustomEditText mEtGateWay;
    private CustomEditText mEtIpAddress;
    private Button mBtnCancel;
    private ListView mListView;
    private TextView mTvNetworkType;

    private boolean mNetworkTypeIsDhcp = true;
    private PopupWindow mPopupWindow;

    private HashMap<String, String> mHashMap = new HashMap<>();


    @Override
    public int getLayout() {
        return R.layout.activity_network;
    }

    @Override
    public void init() {
        mToolbar.setTitle(R.string.network_title);
        getNetworkMsg();
    }

    @Override
    public void initView() {
        mListView = findViewById(R.id.listView);

        mEtIpAddress = findViewById(R.id.et_ipAddress);
        mEtGateWay = findViewById(R.id.et_gateway);
        mEtDns = findViewById(R.id.et_dns);
        mBtnSave = findViewById(R.id.btn_save);
        mBtnCancel = findViewById(R.id.btn_cancel);
        mLineStatic = findViewById(R.id.line_static);
        ImageView mIvDown = findViewById(R.id.iv_popup);

        mTvNetworkType = findViewById(R.id.tv_network_type);
        System.out.println("=== " + mHashMap.get(Const.NETWORK_TYPE));
        mNetworkTypeIsDhcp = Const.NETWORK_TYPE_DHCP.equals(mHashMap.get(Const.NETWORK_TYPE));
        if (mNetworkTypeIsDhcp) {
            handleDHCP();
        } else {
            handleStatic();
        }
        mBtnSave.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mTvNetworkType.setOnClickListener(this);
        mIvDown.setOnClickListener(this);
    }

    @Override
    public void firstResume() {

    }

    @Override
    public void resume() {
    }


    private void handleStatic() {
        mTvNetworkType.setText(R.string.network_static);
        mLineStatic.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
        mEtIpAddress.setText(mHashMap.get(Const.NETWORK_IP_ADDRESS));
        mEtGateWay.setText(mHashMap.get(Const.NETWORK_GATEWAY));
        mEtDns.setText(mHashMap.get(Const.NETWORK_DNS));
        mNetworkTypeIsDhcp = false;
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    private void handleDHCP() {
        mTvNetworkType.setText(R.string.network_dhcp);
        ArrayList<NetworkBean> dhcpList = new ArrayList<>();
        dhcpList.add(new NetworkBean("", "IP地址", mHashMap.get(Const.NETWORK_IP_ADDRESS), 1));
        dhcpList.add(new NetworkBean("", "网关", mHashMap.get(Const.NETWORK_GATEWAY), 1));
        dhcpList.add(new NetworkBean("", "DNS", mHashMap.get(Const.NETWORK_DNS), 1));
        mListView.setAdapter(new NetworkAdapter(this, dhcpList));

        mListView.setVisibility(View.VISIBLE);
        mLineStatic.setVisibility(View.GONE);
        mNetworkTypeIsDhcp = true;
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                handleSave();
                break;
            case R.id.btn_cancel:
                handelCancel();
                break;
            case R.id.tv_network_type:
                handelChangeNetwork();
                break;
            case R.id.iv_popup:
                handleImagePopup();
                break;
            default:
                break;
        }

    }

    private void handleSave() {


        //静态ip控制
        Intent staticIPIntent = new Intent("rk.android.setEthernetStaticIp.action");
        staticIPIntent.putExtra("switch", false);//dhcp获取
//        staticIPIntent.putExtra("switch", true);//静态IP
//        staticIPIntent.putExtra("ip", "192.168.0.199");
//        staticIPIntent.putExtra("gateway", "192.168.0.1");
//        staticIPIntent.putExtra("netmask", "255.255.255.0");
//        staticIPIntent.putExtra("dns1", "8.86.8.8");
//        staticIPIntent.putExtra("dns2", "8.166.4.4");
        sendBroadcast(staticIPIntent);
//
//        try {
//            if (mNetworkTypeIsDhcp) {
//                StaticIpUtil.setNormalEthernetIPAddress();
//                getNetworkMsg();
//                handleStatic();
//            } else {
//                String ipAddress = mEtIpAddress.getText().toString().trim();
//                String gateway = mEtGateWay.getText().toString().trim();
//                String dns = mEtDns.getText().toString().trim();
//
//                StaticIpUtil.setStaticEthernetIPAddress(ipAddress, "255.255.255.0", gateway, dns, "8.8.8.8");
//                getNetworkMsg();
//                handleStatic();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            ToastUtil.showToast(this, "配置失败 = " + e.getMessage());
//        }

    }

    private void handelCancel() {
        finish();
    }

    private void handelChangeNetwork() {
        showPopupWindow();
    }

    private void handleImagePopup() {
        showPopupWindow();
    }

    private void getNetworkMsg() {
        ShellUtils.CommandResult ifconfig = ShellUtils.execCommand(Const.NETWORK_COMMAND_GET_ETH0, true);
        if (TextUtils.isEmpty(ifconfig.successMsg)) {
            LogUtils.e(TAG, "获取 eth0 信息失败");
            return;
        }
        System.out.println(ifconfig.successMsg);
        System.out.println(ifconfig.errorMsg);
        System.out.println(ifconfig.result);

        String[] split = ifconfig.successMsg.split("]\\[");
        for (int i = 0; i < split.length; i++) {
            if (split[i].contains(Const.NETWORK_IP_ADDRESS)) {
                mHashMap.put(Const.NETWORK_IP_ADDRESS, getValue(split[i]));
            } else if (split[i].contains(Const.NETWORK_GATEWAY)) {
                mHashMap.put(Const.NETWORK_GATEWAY, getValue(split[i]));
            } else if (split[i].contains(Const.NETWORK_DNS)) {
                mHashMap.put(Const.NETWORK_DNS, getValue(split[i]));
            } else if (split[i].contains(Const.NETWORK_TYPE)) {
                mHashMap.put(Const.NETWORK_TYPE, getValue(split[i]));
            }
        }
        System.out.println(mHashMap);
    }

    private String getValue(String split) {
        String[] split1 = split.split(":");
        if (split1.length == 2) {
            return split1[1].replaceAll("\\[", "").replaceAll("]", "").trim();
        }
        return "";
    }

    private void showPopupWindow() {
        AppUtil.closeKeyBord(mTvNetworkType, this);
        mPopupWindow = new PopupWindow(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(true);
        View inflate = LayoutInflater.from(this).inflate(R.layout.pop_network, null, false);
        TextView tvStatic = inflate.findViewById(R.id.tv_static);
        TextView tvDhcp = inflate.findViewById(R.id.tv_dhcp);
        TextView tvCancel = inflate.findViewById(R.id.tv_cancel);

        final RadioButton rbStatic = inflate.findViewById(R.id.rb_static);
        final RadioButton rbDhcp = inflate.findViewById(R.id.rb_dhcp);
        rbDhcp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rbStatic.setChecked(!isChecked);
                if (isChecked) {
                    handleDHCP();
                }
            }
        });
        rbStatic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rbDhcp.setChecked(!isChecked);
                if (isChecked) {
                    handleStatic();
                }
            }
        });
        rbStatic.setChecked(!mNetworkTypeIsDhcp);
        rbDhcp.setChecked(mNetworkTypeIsDhcp);


        tvStatic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbStatic.setChecked(true);
                rbDhcp.setChecked(false);
                handleStatic();

            }
        });
        tvDhcp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbStatic.setChecked(false);
                rbDhcp.setChecked(true);
                handleDHCP();

            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.setContentView(inflate);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupWindow.setAnimationStyle(R.style.anim_popup);
        mPopupWindow.showAtLocation(mTvNetworkType, Gravity.BOTTOM, 0, 0);
    }
}
