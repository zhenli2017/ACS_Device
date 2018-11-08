package com.thdtek.acs.terminal.yzface.Message;

public class DataIPInfo {
    private   byte[] MAC = new byte[6];
    private   byte[] IP = new byte[4];
    private   byte[] MASK = new byte[4];
    private   byte[] gatewayIP = new byte[4];
    private   byte[] DNS = new byte[4];
    private   byte[] secondDNS = new byte[4];
    private   byte[] TCPMode = new byte[1];
    private   byte[] localTCPListenPort =  new byte[2];
    private   byte[] localUDPListenPort = new byte[2];
    private   byte[] targetPort = new byte[2];
    private   byte[] targetIP = new byte[4];
    private   byte[] autoGetIP = new byte[1];
    private   byte[] domainName = new byte[99];

    public  String toHexString()
    {
        return  ByteUtil.bytesToHexString(this.toBytes());
    }

    public  int getLength()
    {
        int iLength = 0;
        iLength += this.MAC.length;
        iLength +=  this.IP.length;
        iLength +=  this.MASK.length;
        iLength +=  this.gatewayIP.length;
        iLength +=  this.DNS.length;
        iLength +=  this.secondDNS.length;
        iLength +=  this.TCPMode.length;
        iLength +=  this.localTCPListenPort.length;
        iLength +=  this.localUDPListenPort.length;
        iLength +=  this.targetPort.length;
        iLength +=  this.targetIP.length;
        iLength +=  this.autoGetIP.length;
        iLength +=  this.domainName.length;
        return iLength;
    }
    public byte[] toBytes(){

        int index =0;
        int iLength  = getLength();
        byte[] data = new byte[iLength]; //137

        System.arraycopy(this.MAC, 0, data, index, MAC.length);
        index += MAC.length;

        System.arraycopy(this.IP, 0, data, index, IP.length);
        index += IP.length;

        System.arraycopy(this.MASK, 0, data, index, MASK.length);
        index += MASK.length;

        System.arraycopy(this.gatewayIP, 0, data, index, gatewayIP.length);
        index += gatewayIP.length;

        System.arraycopy(this.DNS, 0, data, index, DNS.length);
        index += DNS.length;

        System.arraycopy(this.secondDNS, 0, data, index, secondDNS.length);
        index += secondDNS.length;

        System.arraycopy(this.TCPMode, 0, data, index, TCPMode.length);
        index += TCPMode.length;

        System.arraycopy(this.localTCPListenPort, 0, data, index, localTCPListenPort.length);
        index += localTCPListenPort.length;

        System.arraycopy(this.localUDPListenPort, 0, data, index, localUDPListenPort.length);
        index += localUDPListenPort.length;

        System.arraycopy(this.targetPort, 0, data, index, targetPort.length);
        index += targetPort.length;

        System.arraycopy(this.targetIP, 0, data, index, targetIP.length);
        index += targetIP.length;

        System.arraycopy(this.autoGetIP, 0, data, index, autoGetIP.length);
        index += autoGetIP.length;

        System.arraycopy(this.domainName, 0, data, index, domainName.length);
        index += domainName.length;

        return  data;

    }

    public DataIPInfo()
    {

    }
    public DataIPInfo(byte[] data)
    {
        int index =0;
        if(data.length != 137){
            throw new IllegalArgumentException("data.length != 137,DataIPInfo length must == 137 !!!");
        }

        //组装其他部件
        System.arraycopy(data,index, this.MAC,0,this.MAC.length );
        index += this.MAC.length;

        System.arraycopy(data,index, this.IP,0,this.IP.length );
        index += this.IP.length;

        System.arraycopy(data,index, this.MASK,0,this.MASK.length );
        index += this.MASK.length;

        System.arraycopy(data,index, this.gatewayIP,0,this.gatewayIP.length );
        index += this.gatewayIP.length;

        System.arraycopy(data,index, this.DNS,0,this.DNS.length );
        index += this.DNS.length;

        System.arraycopy(data,index, this.secondDNS,0,this.secondDNS.length );
        index += this.secondDNS.length;

        System.arraycopy(data,index, this.TCPMode,0,this.TCPMode.length );
        index += this.TCPMode.length;

        System.arraycopy(data,index, this.localTCPListenPort,0,this.localTCPListenPort.length );
        index += this.localTCPListenPort.length;

        System.arraycopy(data,index, this.localUDPListenPort,0,this.localUDPListenPort.length );
        index += this.localUDPListenPort.length;

        System.arraycopy(data,index, this.targetPort,0,this.targetPort.length );
        index += this.targetPort.length;

        System.arraycopy(data,index, this.targetIP,0,this.targetIP.length );
        index += this.targetIP.length;

        System.arraycopy(data,index, this.autoGetIP,0,this.autoGetIP.length );
        index += this.autoGetIP.length;

        System.arraycopy(data,index, this.domainName,0,this.domainName.length );
        index += this.domainName.length;
    }

    public byte[] getMAC() {
        return MAC;
    }

    public void setMAC(byte[] MAC) {
        if(MAC.length != 6){
            throw new IllegalArgumentException("MAC.length != 6  !!!");
        }
        this.MAC = MAC;
    }

    public byte[] getIP() {
        return IP;
    }

    public void setIP(byte[] IP) {
        if(IP.length != 4){
            throw new IllegalArgumentException("IP.length != 4  !!!");
        }
        this.IP = IP;
    }

    public byte[] getMASK() {
        return MASK;
    }

    public void setMASK(byte[] MASK) {
        if(MASK.length != 4){
            throw new IllegalArgumentException("MASK.length != 4  !!!");
        }
        this.MASK = MASK;
    }

    public byte[] getGatewayIP() {
        return gatewayIP;
    }

    public void setGatewayIP(byte[] gatewayIP) {
        if(gatewayIP.length != 4){
            throw new IllegalArgumentException("gatewayIP.length != 4  !!!");
        }
        this.gatewayIP = gatewayIP;
    }

    public byte[] getDNS() {
        return DNS;
    }

    public void setDNS(byte[] DNS) {
        if(DNS.length != 4){
            throw new IllegalArgumentException("DNS.length != 4  !!!");
        }
        this.DNS = DNS;
    }

    public byte[] getSecondDNS() {
        return secondDNS;
    }

    public void setSecondDNS(byte[] secondDNS) {
        if(secondDNS.length != 4){
            throw new IllegalArgumentException("secondDNS.length != 4  !!!");
        }
        this.secondDNS = secondDNS;
    }

    public byte[] getLocalTCPListenPort() {
        return localTCPListenPort;
    }

    public void setLocalTCPListenPort(byte[] localTCPListenPort) {
        if(localTCPListenPort.length != 2){
            throw new IllegalArgumentException("localTCPListenPort.length != 2  !!!");
        }
        this.localTCPListenPort = localTCPListenPort;
    }

    public byte[] getLocalUDPListenPort() {
        return localUDPListenPort;
    }

    public void setLocalUDPListenPort(byte[] localUDPListenPort) {
        if(localUDPListenPort.length != 2){
            throw new IllegalArgumentException("localUDPListenPort.length != 2  !!!");
        }
        this.localUDPListenPort = localUDPListenPort;
    }

    public byte[] getTargetPort() {
        return targetPort;
    }

    public void setTargetPort(byte[] targetPort) {
        if(targetPort.length != 2){
            throw new IllegalArgumentException("targetPort.length != 2  !!!");
        }
        this.targetPort = targetPort;
    }

    public byte[] getTargetIP() {
        return targetIP;
    }

    public void setTargetIP(byte[] targetIP) {
        if(targetIP.length != 4){
            throw new IllegalArgumentException("targetIP.length != 4  !!!");
        }
        this.targetIP = targetIP;
    }

    public byte[] getDomainName() {
        return domainName;
    }

    public void setDomainName(byte[] domainName) {
        if(domainName.length != 99){
            throw new IllegalArgumentException("domainName.length != 99  !!!");
        }
        this.domainName = domainName;
    }

    public byte[] getTCPMode() {
        return TCPMode;
    }

    public void setTCPMode(byte[] TCPMode) {
        if(TCPMode.length != 1){
            throw new IllegalArgumentException("TCPMode.length != 1  !!!");
        }
        this.TCPMode = TCPMode;
    }

    public byte[] getAutoGetIP() {
        return autoGetIP;
    }

    public void setAutoGetIP(byte[] autoGetIP) {
        if(autoGetIP.length != 1){
            throw new IllegalArgumentException("autoGetIP.length != 1  !!!");
        }
        this.autoGetIP = autoGetIP;
    }
}
