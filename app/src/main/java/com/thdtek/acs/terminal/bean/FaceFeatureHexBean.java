package com.thdtek.acs.terminal.bean;

import com.thdtek.acs.terminal.util.ByteFormatTransferUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Arrays;

/**
 * Time:2018/7/7
 * User:lizhen
 * Description:
 */

@Entity
public class FaceFeatureHexBean {

    @Id
    private Long id;
    private long authorityId;
    private long personId;
    private String faceFeatureHex;
    @Transient
    private byte[] faceFeatureByte;


    @Generated(hash = 1004538193)
    public FaceFeatureHexBean(Long id, long authorityId, long personId, String faceFeatureHex) {
        this.id = id;
        this.authorityId = authorityId;
        this.personId = personId;
        this.faceFeatureHex = faceFeatureHex;
    }

    @Generated(hash = 209235349)
    public FaceFeatureHexBean() {
    }


    public byte[] getFaceFeatureByte() {
        if (faceFeatureByte == null) {
            faceFeatureByte = ByteFormatTransferUtils.hexStringToBytes(faceFeatureHex);
        }
        return faceFeatureByte;
    }

    public void setFaceFeatureByte(byte[] faceFeatureByte) {
        this.faceFeatureByte = faceFeatureByte;
    }

    public String getFaceFeatureHex() {
        return this.faceFeatureHex;
    }

    public void setFaceFeatureHex(String faceFeatureHex) {
        this.faceFeatureHex = faceFeatureHex;
    }

    public long getPersonId() {
        return this.personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public long getAuthorityId() {
        return this.authorityId;
    }

    public void setAuthorityId(long authorityId) {
        this.authorityId = authorityId;
    }

    public Long getId() {
        return this.authorityId;
    }

    public void setId(Long id) {
        this.id = authorityId;
    }

    @Override
    public String toString() {
        return "FaceFeatureHexBean{" +
                "id=" + id +
                ", authorityId=" + authorityId +
                ", personId=" + personId +
                ", faceFeatureHex='" + faceFeatureHex + '\'' +
                ", faceFeatureByte=" + Arrays.toString(faceFeatureByte) +
                '}';
    }
}
