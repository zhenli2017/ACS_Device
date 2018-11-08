package com.thdtek.acs.terminal.yzface.Entity;


import com.thdtek.acs.terminal.yzface.Message.DataIPInfo;
import com.thdtek.acs.terminal.yzface.Message.DataVersion;

public class Node {
    byte[] SN = new byte[16];
    byte[] NodeToken = new byte[2];

    DataIPInfo ipInfo = new DataIPInfo();
    DataVersion version = new DataVersion();

    public Node() {
        byte[] IP   = DeviceUtil.getLocalIPAddressByte();
        byte[] MASK = {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0x00};
        byte[] GatewayIP = new byte[4];

        System.arraycopy(IP, 0, GatewayIP, 0, GatewayIP.length);

        GatewayIP[3] = 0x01;

        ipInfo.setMAC(DeviceUtil.getMacAddressByte());
        ipInfo.setIP(IP);
        ipInfo.setMASK(MASK);
        ipInfo.setGatewayIP(GatewayIP);

        byte[] TCPMode = new byte[1];
        TCPMode[0] = 2;

        byte[] Port = new byte[2];
        Port[0] = 0x1F;
        Port[1] = 0x40;

        ipInfo.setTCPMode(TCPMode);
        ipInfo.setLocalTCPListenPort(Port);

       Port = new byte[2];
        Port[0] = 0x1F;
        Port[1] = (byte)0xA5;
        ipInfo.setLocalUDPListenPort(Port);

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
