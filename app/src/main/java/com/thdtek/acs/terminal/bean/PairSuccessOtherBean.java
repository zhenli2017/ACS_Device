package com.thdtek.acs.terminal.bean;

/**
 * Time:2018/11/29
 * User:lizhen
 * Description:
 */

public class PairSuccessOtherBean {
    private String gender;
    private String birthday;
    private String location;
    private String validityTime;
    private String signingOrganization;
    private String nation;
    private String idNumber;
    private long accessTime;

    public long getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(long accessTime) {
        this.accessTime = accessTime;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getValidityTime() {
        return validityTime;
    }

    public void setValidityTime(String validityTime) {
        this.validityTime = validityTime;
    }

    public String getSigningOrganization() {
        return signingOrganization;
    }

    public void setSigningOrganization(String signingOrganization) {
        this.signingOrganization = signingOrganization;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }
}
