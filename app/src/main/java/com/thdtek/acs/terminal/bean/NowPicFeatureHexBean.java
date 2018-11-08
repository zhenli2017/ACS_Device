package com.thdtek.acs.terminal.bean;

import com.thdtek.acs.terminal.util.ByteFormatTransferUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

/**
 * Time:2018/7/26
 * User:lizhen
 * Description:
 */

@Entity
public class NowPicFeatureHexBean {
    @Id
    private Long id;
    private long authorityId;
    private long personId;
    //上次更新特征值的index
    private int lastIndex;
    private String nowPicOneHex;
    @Transient
    private byte[] nowPicOneByte;

    private String nowPicTwoHex;
    @Transient
    private byte[] nowPicTwoByte;

    private String nowPicThreeHex;
    @Transient
    private byte[] nowPicThreeByte;

    @Generated(hash = 1304285269)
    public NowPicFeatureHexBean(Long id, long authorityId, long personId, int lastIndex,
            String nowPicOneHex, String nowPicTwoHex, String nowPicThreeHex) {
        this.id = id;
        this.authorityId = authorityId;
        this.personId = personId;
        this.lastIndex = lastIndex;
        this.nowPicOneHex = nowPicOneHex;
        this.nowPicTwoHex = nowPicTwoHex;
        this.nowPicThreeHex = nowPicThreeHex;
    }

    @Generated(hash = 1674335766)
    public NowPicFeatureHexBean() {
    }

    public byte[] getNowPicOneByte() {
        if (nowPicOneByte == null) {
            nowPicOneByte = ByteFormatTransferUtils.hexStringToBytes(nowPicOneHex);
        }
        return nowPicOneByte;
    }

    public byte[] getNowPicTwoByte() {
        if (nowPicTwoByte == null) {
            nowPicTwoByte = ByteFormatTransferUtils.hexStringToBytes(nowPicTwoHex);
        }
        return nowPicTwoByte;
    }

    public byte[] getNowPicThreeByte() {
        if (nowPicThreeByte == null) {
            nowPicThreeByte = ByteFormatTransferUtils.hexStringToBytes(nowPicThreeHex);
        }
        return nowPicThreeByte;
    }


    public void setNowPicOneByte(byte[] nowPicOneByte) {
        this.nowPicOneByte = nowPicOneByte;
    }

    public void setNowPicTwoByte(byte[] nowPicTwoByte) {
        this.nowPicTwoByte = nowPicTwoByte;
    }

    public void setNowPicThreeByte(byte[] nowPicThreeByte) {
        this.nowPicThreeByte = nowPicThreeByte;
    }

    public String getNowPicThreeHex() {
        return this.nowPicThreeHex;
    }

    public void setNowPicThreeHex(String nowPicThreeHex) {
        this.nowPicThreeHex = nowPicThreeHex;
    }

    public String getNowPicTwoHex() {
        return this.nowPicTwoHex;
    }

    public void setNowPicTwoHex(String nowPicTwoHex) {
        this.nowPicTwoHex = nowPicTwoHex;
    }

    public String getNowPicOneHex() {
        return this.nowPicOneHex;
    }

    public void setNowPicOneHex(String nowPicOneHex) {
        this.nowPicOneHex = nowPicOneHex;
    }

    public int getLastIndex() {
        return this.lastIndex;
    }

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
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
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
