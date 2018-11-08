package com.thdtek.acs.terminal.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.math.BigDecimal;

@Entity
public class AccessRecordBean {
    @Id(autoincrement = true)
    private Long id;
    //通过时间
    private long time;
    //0:通过失败   1:通过成功
    private int accessSuccessful;
    //0:刷脸 1:工卡 2:刷身份证
    private int type;
    //通过抓拍图片
    private String accessImage;
    //服务器发送的personId
    private long personId;
    private long authorityId;

    private String cardNum;
    private String idNum;

    private String personImage;
    private String personName;

    private float defaultFaceFeatureNumber;
    private float currentFaceFeatureNumber;
    private long count;
    private float personRate;
    private float accordRate;
    //false-没有进过http上传   true-已经由http上传
    private boolean uploadToHttp;
    //对应http的personId
    private String fid;
    public String getFid() {
        return this.fid;
    }
    public void setFid(String fid) {
        this.fid = fid;
    }
    public boolean getUploadToHttp() {
        return this.uploadToHttp;
    }
    public void setUploadToHttp(boolean uploadToHttp) {
        this.uploadToHttp = uploadToHttp;
    }
    public float getAccordRate() {
        return this.accordRate;
    }
    public void setAccordRate(float accordRate) {
        this.accordRate = accordRate;
    }
    public float getPersonRate() {
        return this.personRate;
    }
    public void setPersonRate(float personRate) {
        this.personRate = personRate;
    }
    public long getCount() {
        return this.count;
    }
    public void setCount(long count) {
        this.count = count;
    }
    public float getCurrentFaceFeatureNumber() {
        return this.currentFaceFeatureNumber;
    }
    public void setCurrentFaceFeatureNumber(float currentFaceFeatureNumber) {
        this.currentFaceFeatureNumber = currentFaceFeatureNumber;
    }
    public float getDefaultFaceFeatureNumber() {
        return this.defaultFaceFeatureNumber;
    }
    public void setDefaultFaceFeatureNumber(float defaultFaceFeatureNumber) {
        this.defaultFaceFeatureNumber = defaultFaceFeatureNumber;
    }
    public String getPersonName() {
        return this.personName;
    }
    public void setPersonName(String personName) {
        this.personName = personName;
    }
    public String getPersonImage() {
        return this.personImage;
    }
    public void setPersonImage(String personImage) {
        this.personImage = personImage;
    }
    public String getIdNum() {
        return this.idNum;
    }
    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }
    public String getCardNum() {
        return this.cardNum;
    }
    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }
    public long getAuthorityId() {
        return this.authorityId;
    }
    public void setAuthorityId(long authorityId) {
        this.authorityId = authorityId;
    }
    public long getPersonId() {
        return this.personId;
    }
    public void setPersonId(long personId) {
        this.personId = personId;
    }
    public String getAccessImage() {
        return this.accessImage;
    }
    public void setAccessImage(String accessImage) {
        this.accessImage = accessImage;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getAccessSuccessful() {
        return this.accessSuccessful;
    }
    public void setAccessSuccessful(int accessSuccessful) {
        this.accessSuccessful = accessSuccessful;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 717574129)
    public AccessRecordBean(Long id, long time, int accessSuccessful, int type,
            String accessImage, long personId, long authorityId, String cardNum,
            String idNum, String personImage, String personName,
            float defaultFaceFeatureNumber, float currentFaceFeatureNumber,
            long count, float personRate, float accordRate, boolean uploadToHttp,
            String fid) {
        this.id = id;
        this.time = time;
        this.accessSuccessful = accessSuccessful;
        this.type = type;
        this.accessImage = accessImage;
        this.personId = personId;
        this.authorityId = authorityId;
        this.cardNum = cardNum;
        this.idNum = idNum;
        this.personImage = personImage;
        this.personName = personName;
        this.defaultFaceFeatureNumber = defaultFaceFeatureNumber;
        this.currentFaceFeatureNumber = currentFaceFeatureNumber;
        this.count = count;
        this.personRate = personRate;
        this.accordRate = accordRate;
        this.uploadToHttp = uploadToHttp;
        this.fid = fid;
    }
    @Generated(hash = 1738883123)
    public AccessRecordBean() {
    }

}
