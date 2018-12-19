package com.thdtek.acs.terminal.bean;

import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.MyApplication;

/**
 * Time:2018/11/14
 * User:lizhen
 * Description:
 */

public class HardwareStatusEvent {

    public static final int HARDWARE_STATUS_NUMBER = 0;
    public static final int HARDWARE_STATUS_ID = 1;
    public static final String HARDWARE_STATUS_SUCCESS = MyApplication.getContext().getString(R.string.hard_ware_connect_success);
    public static final String HARDWARE_STATUS_FAIL = MyApplication.getContext().getString(R.string.hard_ware_connect_fail);
    public static final String HARDWARE_STATUS_READ_SUCCESS = MyApplication.getContext().getString(R.string.hard_ware_read_success);
    public static final String HARDWARE_STATUS_READ_FAIL = MyApplication.getContext().getString(R.string.hard_ware_read_fail);

    //0:数字小键盘,1:身份证模块
    private int type;
    private String codeConnect;
    private String codeRead;

    public HardwareStatusEvent(int type, String codeConnect, String codeRead) {
        this.type = type;
        this.codeConnect = codeConnect;
        this.codeRead = codeRead;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCodeConnect() {
        return codeConnect;
    }

    public void setCodeConnect(String codeConnect) {
        this.codeConnect = codeConnect;
    }

    public String getCodeRead() {
        return codeRead;
    }

    public void setCodeRead(String codeRead) {
        this.codeRead = codeRead;
    }
}
