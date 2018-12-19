package com.thdtek.acs.terminal.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Time:2018/7/3
 * User:lizhen
 * Description:
 */

@Entity
public class ConfigBean {
    @Id(autoincrement = true)
    private Long id;

    /**
     * cameraDetectType : 0
     * faceFeaturePairNumber : 0.0
     * faceFeaturePairSuccessOrFailWaitTime : 0
     * openDoorType : 5
     * openDoorContinueTime : 0
     * doorType : 0
     * deviceName :
     * deviceSerialNumber : 2222222222222222
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
    private float faceFeaturePairNumber;
    private long faceFeaturePairSuccessOrFailWaitTime;
    private int openDoorType;
    private long openDoorContinueTime;
    private int doorType;
    private String deviceName;
    private String deviceSerialNumber;
    private String deviceDefendTime;
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
    private long deviceServiceTime;
    private long deviceRegisterTime;
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
    private long deviceElapsedRealtime;
    private String appFailMsg;

    private String fillLightTimes;
    private float beginRecoDistance; //识别距离，终端框出人脸正方形的边长像素值长于此值，则进行识别
    private float picQualityRate; //图片质量参数，0~1.0，质量越高，识别越不易出错

    private float idFeaturePairNumber;
    private int guestOpenDoorType;
    private String guestOpenDoorNumber;
    private int pairSuccessOpenDoor;
    public String getGuestOpenDoorNumber() {
        return this.guestOpenDoorNumber;
    }
    public void setGuestOpenDoorNumber(String guestOpenDoorNumber) {
        this.guestOpenDoorNumber = guestOpenDoorNumber;
    }
    public int getGuestOpenDoorType() {
        return this.guestOpenDoorType;
    }
    public void setGuestOpenDoorType(int guestOpenDoorType) {
        this.guestOpenDoorType = guestOpenDoorType;
    }
    public float getIdFeaturePairNumber() {
        return this.idFeaturePairNumber;
    }
    public void setIdFeaturePairNumber(float idFeaturePairNumber) {
        this.idFeaturePairNumber = idFeaturePairNumber;
    }
    public float getPicQualityRate() {
        return this.picQualityRate;
    }
    public void setPicQualityRate(float picQualityRate) {
        this.picQualityRate = picQualityRate;
    }
    public float getBeginRecoDistance() {
        return this.beginRecoDistance;
    }
    public void setBeginRecoDistance(float beginRecoDistance) {
        this.beginRecoDistance = beginRecoDistance;
    }
    public String getFillLightTimes() {
        return this.fillLightTimes;
    }
    public void setFillLightTimes(String fillLightTimes) {
        this.fillLightTimes = fillLightTimes;
    }
    public String getAppFailMsg() {
        return this.appFailMsg;
    }
    public void setAppFailMsg(String appFailMsg) {
        this.appFailMsg = appFailMsg;
    }
    public long getDeviceElapsedRealtime() {
        return this.deviceElapsedRealtime;
    }
    public void setDeviceElapsedRealtime(long deviceElapsedRealtime) {
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
    public long getDeviceRegisterTime() {
        return this.deviceRegisterTime;
    }
    public void setDeviceRegisterTime(long deviceRegisterTime) {
        this.deviceRegisterTime = deviceRegisterTime;
    }
    public long getDeviceServiceTime() {
        return this.deviceServiceTime;
    }
    public void setDeviceServiceTime(long deviceServiceTime) {
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
    public String getDeviceDefendTime() {
        return this.deviceDefendTime;
    }
    public void setDeviceDefendTime(String deviceDefendTime) {
        this.deviceDefendTime = deviceDefendTime;
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
    public long getOpenDoorContinueTime() {
        return this.openDoorContinueTime;
    }
    public void setOpenDoorContinueTime(long openDoorContinueTime) {
        this.openDoorContinueTime = openDoorContinueTime;
    }
    public int getOpenDoorType() {
        return this.openDoorType;
    }
    public void setOpenDoorType(int openDoorType) {
        this.openDoorType = openDoorType;
    }
    public long getFaceFeaturePairSuccessOrFailWaitTime() {
        return this.faceFeaturePairSuccessOrFailWaitTime;
    }
    public void setFaceFeaturePairSuccessOrFailWaitTime(
            long faceFeaturePairSuccessOrFailWaitTime) {
        this.faceFeaturePairSuccessOrFailWaitTime = faceFeaturePairSuccessOrFailWaitTime;
    }
    public float getFaceFeaturePairNumber() {
        return this.faceFeaturePairNumber;
    }
    public void setFaceFeaturePairNumber(float faceFeaturePairNumber) {
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
    public int getPairSuccessOpenDoor() {
        return this.pairSuccessOpenDoor;
    }
    public void setPairSuccessOpenDoor(int pairSuccessOpenDoor) {
        this.pairSuccessOpenDoor = pairSuccessOpenDoor;
    }
    @Generated(hash = 1077978437)
    public ConfigBean(Long id, int cameraDetectType, float faceFeaturePairNumber,
            long faceFeaturePairSuccessOrFailWaitTime, int openDoorType,
            long openDoorContinueTime, int doorType, String deviceName,
            String deviceSerialNumber, String deviceDefendTime, int deviceIntoOrOut,
            int deviceMusicSize, String appWelcomeMsg, String appWelcomeMusic,
            String serverIp, int serverPort, int deviceNetworkType, int deviceNetworkIpType,
            String deviceIpAddress, String deviceSn, long deviceServiceTime,
            long deviceRegisterTime, String deviceRomSize, String deviceRomAvailableSize,
            String deviceRamMaxSize, String deviceRamTotalSize, String deviceRamUseSize,
            int deviceCpuTemperature, int deviceTemperature, String deviceSystemVersion,
            String deviceAppVersion, String deviceCameraSdkVersion,
            String deviceHardwareSdkVersion, long deviceElapsedRealtime, String appFailMsg,
            String fillLightTimes, float beginRecoDistance, float picQualityRate,
            float idFeaturePairNumber, int guestOpenDoorType, String guestOpenDoorNumber,
            int pairSuccessOpenDoor) {
        this.id = id;
        this.cameraDetectType = cameraDetectType;
        this.faceFeaturePairNumber = faceFeaturePairNumber;
        this.faceFeaturePairSuccessOrFailWaitTime = faceFeaturePairSuccessOrFailWaitTime;
        this.openDoorType = openDoorType;
        this.openDoorContinueTime = openDoorContinueTime;
        this.doorType = doorType;
        this.deviceName = deviceName;
        this.deviceSerialNumber = deviceSerialNumber;
        this.deviceDefendTime = deviceDefendTime;
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
        this.appFailMsg = appFailMsg;
        this.fillLightTimes = fillLightTimes;
        this.beginRecoDistance = beginRecoDistance;
        this.picQualityRate = picQualityRate;
        this.idFeaturePairNumber = idFeaturePairNumber;
        this.guestOpenDoorType = guestOpenDoorType;
        this.guestOpenDoorNumber = guestOpenDoorNumber;
        this.pairSuccessOpenDoor = pairSuccessOpenDoor;
    }
    @Generated(hash = 1548494737)
    public ConfigBean() {
    }

}
