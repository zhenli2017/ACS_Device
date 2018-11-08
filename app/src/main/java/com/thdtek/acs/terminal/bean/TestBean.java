package com.thdtek.acs.terminal.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Time:2018/6/20
 * User:lizhen
 * Description:
 */

@Entity
public class TestBean {

    @Id
    private Long id;
    /**
     * cameraDetectType : 0
     * faceFeaturePairNumber : 0.0
     * faceFeaturePairSuccessOrFailWaitTime : 0
     * openDoorType : 0
     * openDoorContinueTime : 0
     * doorType : 0
     * deviceName :
     * deviceSerialNumber :
     * deviceDefendStartTime : 0
     * deviceDefendEndTime : 0
     * deviceIntoOrOut : 0
     * deviceMusicSize : 0
     * appWelcomeMsg :
     * appWelcomeMusic :
     * serverIp :
     * serverPort : 0
     * deviceNetworkType : 0
     * deviceNetworkIpType : 0
     * deviceIpAddress :
     * deviceSn : 22222222
     * deviceServiceTime : 0
     * deviceRegisterTime :
     * deviceRomSize :
     * deviceRomAvailableSize :
     * deviceRamMaxSize :
     * deviceRamTotalSize :
     * deviceRamUseSize :
     * deviceCpuTemperature : 0
     * deviceTemperature : 0
     * deviceSystemVersion :
     * deviceAppVersion :
     * deviceCameraSdkVersion :
     * deviceHardwareSdkVersion :
     * deviceElapsedRealtime : 0
     */

    private int cameraDetectType;
    private double faceFeaturePairNumber;
    private int faceFeaturePairSuccessOrFailWaitTime;
    private int openDoorType;
    private int openDoorContinueTime;
    private int doorType;
    private String deviceName;
    private String deviceSerialNumber;
    private String deviceDefendStartTime;
    private String deviceDefendEndTime;
    private int deviceIntoOrOut;
    private int deviceMusicSize;
    private String appWelcomeMsg;
    private String appWelcomeMusic;
    private String serverIp;
    private int serverPort;
    private int deviceNetworkType;
    private int deviceNetworkIpType;
    private String deviceIpAddress;
    private String deviceSn;
    private int deviceServiceTime;
    private String deviceRegisterTime;
    private String deviceRomSize;
    private String deviceRomAvailableSize;
    private String deviceRamMaxSize;
    private String deviceRamTotalSize;
    private String deviceRamUseSize;
    private int deviceCpuTemperature;
    private int deviceTemperature;
    private String deviceSystemVersion;
    private String deviceAppVersion;
    private String deviceCameraSdkVersion;
    private String deviceHardwareSdkVersion;
    private String deviceElapsedRealtime;

    public String getDeviceElapsedRealtime() {
        return this.deviceElapsedRealtime;
    }

    public void setDeviceElapsedRealtime(String deviceElapsedRealtime) {
        this.deviceElapsedRealtime = deviceElapsedRealtime;
    }

    public String getDeviceHardwareSdkVersion() {
        return this.deviceHardwareSdkVersion;
    }

    public void setDeviceHardwareSdkVersion(String deviceHardwareSdkVersion) {
        this.deviceHardwareSdkVersion = deviceHardwareSdkVersion;
    }

    public String getDeviceCameraSdkVersion() {
        return this.deviceCameraSdkVersion;
    }

    public void setDeviceCameraSdkVersion(String deviceCameraSdkVersion) {
        this.deviceCameraSdkVersion = deviceCameraSdkVersion;
    }

    public String getDeviceAppVersion() {
        return this.deviceAppVersion;
    }

    public void setDeviceAppVersion(String deviceAppVersion) {
        this.deviceAppVersion = deviceAppVersion;
    }

    public String getDeviceSystemVersion() {
        return this.deviceSystemVersion;
    }

    public void setDeviceSystemVersion(String deviceSystemVersion) {
        this.deviceSystemVersion = deviceSystemVersion;
    }

    public int getDeviceTemperature() {
        return this.deviceTemperature;
    }

    public void setDeviceTemperature(int deviceTemperature) {
        this.deviceTemperature = deviceTemperature;
    }

    public int getDeviceCpuTemperature() {
        return this.deviceCpuTemperature;
    }

    public void setDeviceCpuTemperature(int deviceCpuTemperature) {
        this.deviceCpuTemperature = deviceCpuTemperature;
    }

    public String getDeviceRamUseSize() {
        return this.deviceRamUseSize;
    }

    public void setDeviceRamUseSize(String deviceRamUseSize) {
        this.deviceRamUseSize = deviceRamUseSize;
    }

    public String getDeviceRamTotalSize() {
        return this.deviceRamTotalSize;
    }

    public void setDeviceRamTotalSize(String deviceRamTotalSize) {
        this.deviceRamTotalSize = deviceRamTotalSize;
    }

    public String getDeviceRamMaxSize() {
        return this.deviceRamMaxSize;
    }

    public void setDeviceRamMaxSize(String deviceRamMaxSize) {
        this.deviceRamMaxSize = deviceRamMaxSize;
    }

    public String getDeviceRomAvailableSize() {
        return this.deviceRomAvailableSize;
    }

    public void setDeviceRomAvailableSize(String deviceRomAvailableSize) {
        this.deviceRomAvailableSize = deviceRomAvailableSize;
    }

    public String getDeviceRomSize() {
        return this.deviceRomSize;
    }

    public void setDeviceRomSize(String deviceRomSize) {
        this.deviceRomSize = deviceRomSize;
    }

    public String getDeviceRegisterTime() {
        return this.deviceRegisterTime;
    }

    public void setDeviceRegisterTime(String deviceRegisterTime) {
        this.deviceRegisterTime = deviceRegisterTime;
    }

    public int getDeviceServiceTime() {
        return this.deviceServiceTime;
    }

    public void setDeviceServiceTime(int deviceServiceTime) {
        this.deviceServiceTime = deviceServiceTime;
    }

    public String getDeviceSn() {
        return this.deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getDeviceIpAddress() {
        return this.deviceIpAddress;
    }

    public void setDeviceIpAddress(String deviceIpAddress) {
        this.deviceIpAddress = deviceIpAddress;
    }

    public int getDeviceNetworkIpType() {
        return this.deviceNetworkIpType;
    }

    public void setDeviceNetworkIpType(int deviceNetworkIpType) {
        this.deviceNetworkIpType = deviceNetworkIpType;
    }

    public int getDeviceNetworkType() {
        return this.deviceNetworkType;
    }

    public void setDeviceNetworkType(int deviceNetworkType) {
        this.deviceNetworkType = deviceNetworkType;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerIp() {
        return this.serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getAppWelcomeMusic() {
        return this.appWelcomeMusic;
    }

    public void setAppWelcomeMusic(String appWelcomeMusic) {
        this.appWelcomeMusic = appWelcomeMusic;
    }

    public String getAppWelcomeMsg() {
        return this.appWelcomeMsg;
    }

    public void setAppWelcomeMsg(String appWelcomeMsg) {
        this.appWelcomeMsg = appWelcomeMsg;
    }

    public int getDeviceMusicSize() {
        return this.deviceMusicSize;
    }

    public void setDeviceMusicSize(int deviceMusicSize) {
        this.deviceMusicSize = deviceMusicSize;
    }

    public int getDeviceIntoOrOut() {
        return this.deviceIntoOrOut;
    }

    public void setDeviceIntoOrOut(int deviceIntoOrOut) {
        this.deviceIntoOrOut = deviceIntoOrOut;
    }

    public String getDeviceDefendEndTime() {
        return this.deviceDefendEndTime;
    }

    public void setDeviceDefendEndTime(String deviceDefendEndTime) {
        this.deviceDefendEndTime = deviceDefendEndTime;
    }

    public String getDeviceDefendStartTime() {
        return this.deviceDefendStartTime;
    }

    public void setDeviceDefendStartTime(String deviceDefendStartTime) {
        this.deviceDefendStartTime = deviceDefendStartTime;
    }

    public String getDeviceSerialNumber() {
        return this.deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getDoorType() {
        return this.doorType;
    }

    public void setDoorType(int doorType) {
        this.doorType = doorType;
    }

    public int getOpenDoorContinueTime() {
        return this.openDoorContinueTime;
    }

    public void setOpenDoorContinueTime(int openDoorContinueTime) {
        this.openDoorContinueTime = openDoorContinueTime;
    }

    public int getOpenDoorType() {
        return this.openDoorType;
    }

    public void setOpenDoorType(int openDoorType) {
        this.openDoorType = openDoorType;
    }

    public int getFaceFeaturePairSuccessOrFailWaitTime() {
        return this.faceFeaturePairSuccessOrFailWaitTime;
    }

    public void setFaceFeaturePairSuccessOrFailWaitTime(
            int faceFeaturePairSuccessOrFailWaitTime) {
        this.faceFeaturePairSuccessOrFailWaitTime = faceFeaturePairSuccessOrFailWaitTime;
    }

    public double getFaceFeaturePairNumber() {
        return this.faceFeaturePairNumber;
    }

    public void setFaceFeaturePairNumber(double faceFeaturePairNumber) {
        this.faceFeaturePairNumber = faceFeaturePairNumber;
    }

    public int getCameraDetectType() {
        return this.cameraDetectType;
    }

    public void setCameraDetectType(int cameraDetectType) {
        this.cameraDetectType = cameraDetectType;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 1281106172)
    public TestBean(Long id, int cameraDetectType, double faceFeaturePairNumber,
                    int faceFeaturePairSuccessOrFailWaitTime, int openDoorType,
                    int openDoorContinueTime, int doorType, String deviceName,
                    String deviceSerialNumber, String deviceDefendStartTime,
                    String deviceDefendEndTime, int deviceIntoOrOut, int deviceMusicSize,
                    String appWelcomeMsg, String appWelcomeMusic, String serverIp,
                    int serverPort, int deviceNetworkType, int deviceNetworkIpType,
                    String deviceIpAddress, String deviceSn, int deviceServiceTime,
                    String deviceRegisterTime, String deviceRomSize,
                    String deviceRomAvailableSize, String deviceRamMaxSize,
                    String deviceRamTotalSize, String deviceRamUseSize,
                    int deviceCpuTemperature, int deviceTemperature,
                    String deviceSystemVersion, String deviceAppVersion,
                    String deviceCameraSdkVersion, String deviceHardwareSdkVersion,
                    String deviceElapsedRealtime) {
        this.id = id;
        this.cameraDetectType = cameraDetectType;
        this.faceFeaturePairNumber = faceFeaturePairNumber;
        this.faceFeaturePairSuccessOrFailWaitTime = faceFeaturePairSuccessOrFailWaitTime;
        this.openDoorType = openDoorType;
        this.openDoorContinueTime = openDoorContinueTime;
        this.doorType = doorType;
        this.deviceName = deviceName;
        this.deviceSerialNumber = deviceSerialNumber;
        this.deviceDefendStartTime = deviceDefendStartTime;
        this.deviceDefendEndTime = deviceDefendEndTime;
        this.deviceIntoOrOut = deviceIntoOrOut;
        this.deviceMusicSize = deviceMusicSize;
        this.appWelcomeMsg = appWelcomeMsg;
        this.appWelcomeMusic = appWelcomeMusic;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.deviceNetworkType = deviceNetworkType;
        this.deviceNetworkIpType = deviceNetworkIpType;
        this.deviceIpAddress = deviceIpAddress;
        this.deviceSn = deviceSn;
        this.deviceServiceTime = deviceServiceTime;
        this.deviceRegisterTime = deviceRegisterTime;
        this.deviceRomSize = deviceRomSize;
        this.deviceRomAvailableSize = deviceRomAvailableSize;
        this.deviceRamMaxSize = deviceRamMaxSize;
        this.deviceRamTotalSize = deviceRamTotalSize;
        this.deviceRamUseSize = deviceRamUseSize;
        this.deviceCpuTemperature = deviceCpuTemperature;
        this.deviceTemperature = deviceTemperature;
        this.deviceSystemVersion = deviceSystemVersion;
        this.deviceAppVersion = deviceAppVersion;
        this.deviceCameraSdkVersion = deviceCameraSdkVersion;
        this.deviceHardwareSdkVersion = deviceHardwareSdkVersion;
        this.deviceElapsedRealtime = deviceElapsedRealtime;
    }

    @Generated(hash = 2087637710)
    public TestBean() {
    }

    @Override
    public String toString() {
        return "TestBean{" +
                "id=" + id +
                ", cameraDetectType=" + cameraDetectType +
                ", faceFeaturePairNumber=" + faceFeaturePairNumber +
                ", faceFeaturePairSuccessOrFailWaitTime=" + faceFeaturePairSuccessOrFailWaitTime +
                ", openDoorType=" + openDoorType +
                ", openDoorContinueTime=" + openDoorContinueTime +
                ", doorType=" + doorType +
                ", deviceName='" + deviceName + '\'' +
                ", deviceSerialNumber='" + deviceSerialNumber + '\'' +
                ", deviceDefendStartTime='" + deviceDefendStartTime + '\'' +
                ", deviceDefendEndTime='" + deviceDefendEndTime + '\'' +
                ", deviceIntoOrOut=" + deviceIntoOrOut +
                ", deviceMusicSize=" + deviceMusicSize +
                ", appWelcomeMsg='" + appWelcomeMsg + '\'' +
                ", appWelcomeMusic='" + appWelcomeMusic + '\'' +
                ", serverIp='" + serverIp + '\'' +
                ", serverPort=" + serverPort +
                ", deviceNetworkType=" + deviceNetworkType +
                ", deviceNetworkIpType=" + deviceNetworkIpType +
                ", deviceIpAddress='" + deviceIpAddress + '\'' +
                ", deviceSn='" + deviceSn + '\'' +
                ", deviceServiceTime=" + deviceServiceTime +
                ", deviceRegisterTime='" + deviceRegisterTime + '\'' +
                ", deviceRomSize='" + deviceRomSize + '\'' +
                ", deviceRomAvailableSize='" + deviceRomAvailableSize + '\'' +
                ", deviceRamMaxSize='" + deviceRamMaxSize + '\'' +
                ", deviceRamTotalSize='" + deviceRamTotalSize + '\'' +
                ", deviceRamUseSize='" + deviceRamUseSize + '\'' +
                ", deviceCpuTemperature=" + deviceCpuTemperature +
                ", deviceTemperature=" + deviceTemperature +
                ", deviceSystemVersion='" + deviceSystemVersion + '\'' +
                ", deviceAppVersion='" + deviceAppVersion + '\'' +
                ", deviceCameraSdkVersion='" + deviceCameraSdkVersion + '\'' +
                ", deviceHardwareSdkVersion='" + deviceHardwareSdkVersion + '\'' +
                ", deviceElapsedRealtime='" + deviceElapsedRealtime + '\'' +
                '}';
    }
}
