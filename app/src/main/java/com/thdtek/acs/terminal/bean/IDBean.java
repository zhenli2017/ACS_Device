package com.thdtek.acs.terminal.bean;

import com.google.gson.annotations.SerializedName;

public class IDBean {
    @SerializedName("姓名")
    private String name;
    @SerializedName("图片")
    private String image;
    @SerializedName("性别")
    private String sex;
    @SerializedName("民族")
    private String nation;
    @SerializedName("出生年月")
    private String birthday;
    @SerializedName("居住地")
    private String local;
    @SerializedName("身份证号")
    private String idNumber;
    @SerializedName("签发机关")
    private String signingOrganization;
    @SerializedName("有效期")
    private String validityTime;

    public IDBean() {
    }

    public IDBean(String name, String image, String sex, String nation, String birthday, String local, String idNumber, String signingOrganization, String validityTime) {
        this.name = name;
        this.image = image;
        this.sex = sex;
        this.nation = nation;
        this.birthday = birthday;
        this.local = local;
        this.idNumber = idNumber;
        this.signingOrganization = signingOrganization;
        this.validityTime = validityTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getSigningOrganization() {
        return signingOrganization;
    }

    public void setSigningOrganization(String signingOrganization) {
        this.signingOrganization = signingOrganization;
    }

    public String getValidityTime() {
        return validityTime;
    }

    public void setValidityTime(String validityTime) {
        this.validityTime = validityTime;
    }
}
