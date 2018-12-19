package com.thdtek.acs.terminal.yzface.Entity;


import android.text.TextUtils;

import com.thdtek.acs.terminal.base.MyApplication;
import com.thdtek.acs.terminal.util.LogUtils;
import com.thdtek.acs.terminal.util.SPUtils;
import com.thdtek.acs.terminal.yzface.Message.ByteUtil;
import com.thdtek.acs.terminal.yzface.Message.DataIPInfo;
import com.thdtek.acs.terminal.yzface.Message.DataVersion;
import com.thdtek.acs.terminal.yzface.YZFaceUtil3;

public class Node {
    private static final String TAG = Node.class.getSimpleName();
    byte[] SN = new byte[16];
    byte[] NodeToken = new byte[2];

    DataIPInfo ipInfo = new DataIPInfo();
    DataVersion version = new DataVersion();

    public Node() {
        byte[] IP   = DeviceUtil.getLocalIPAddressByte();
        byte[] MASK = {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x00};
        byte[] GatewayIP = new byte[4];

        if (IP == null) {
            IP = new byte[]{0x00,0x00,0x00,0x00};
        }
        System.arraycopy(IP, 0, GatewayIP, 0, GatewayIP.length);

        GatewayIP[3] = 0x01;

        ipInfo.setMAC(DeviceUtil.getMacAddressByte());
        ipInfo.setIP(IP);
        ipInfo.setMASK(MASK);
        ipInfo.setGatewayIP(GatewayIP);

        byte[] TCPMode = new byte[1];
        TCPMode[0] = 2;

        byte[] Port = new byte[2];
        String tcpPort = (String) SPUtils.get(MyApplication.getContext(), YZFaceUtil3.SP_TCP_PORT, "");
        if (TextUtils.isEmpty(tcpPort)) {
            Port[0] = 0x1F;
            Port[1] = 0x40;
        } else {
            Port = ByteUtil.hexStringToBytes(tcpPort);
        }
        ipInfo.setTCPMode(TCPMode);
        ipInfo.setLocalTCPListenPort(Port);


        String udpPort = (String) SPUtils.get(MyApplication.getContext(), YZFaceUtil3.SP_UDP_PORT, "");
        if (TextUtils.isEmpty(udpPort)) {
            Port = new byte[2];
            Port[0] = 0x1F;
            Port[1] = (byte) 0xA5;
        } else {
            Port = ByteUtil.hexStringToBytes(udpPort);
        }
        ipInfo.setLocalUDPListenPort(Port);

        LogUtils.d(TAG, "udpPort = " + udpPort + " tcpPort = " + tcpPort);
        Port = new byte[2];
        Port[0] = 0x31;
        Port[1] = 0x32;
        version.setMajor(Port);

        Port = new byte[2];
        Port[0] = 0x33;
        Port[1] = 0x34;
        version.setRevision(Port);
    }

    public DataIPInfo getIpInfo() {
        return ipInfo;
    }

    public void setIpInfo(DataIPInfo ipInfo) {
        this.ipInfo = ipInfo;
    }

    public byte[] getSN() {
        return SN;
    }

    public void setSN(byte[] SN) {
        this.SN = SN;
    }

    public byte[] getNodeToken() {
        return NodeToken;
    }

    public void setNodeToken(byte[] nodeToken) {
        NodeToken = nodeToken;
    }

    public DataVersion getVersion() {
        return version;
    }

    public void setVersion(DataVersion version) {
        this.version = version;
    }
}
