package com.thdtek.acs.terminal.bean;

import java.util.Arrays;

/**
 * Time:2018/8/28
 * User:lizhen
 * Description:
 */

public class ImageSaveBean {


    private long id;
    private byte[] data;
    private PersonBean personBean;
    private boolean facePic;
    private boolean onlyForCheck;

    public ImageSaveBean(long id, byte[] data, PersonBean personBean, boolean facePic) {
        this.id = id;
        this.data = data;
        this.personBean = personBean;
        this.facePic = facePic;
    }

    public ImageSaveBean(
            long id,
            byte[] data,
            PersonBean personBean,
            boolean facePic,
            boolean onlyForCheck
    ) {
        this.id = id;
        this.data = data;
        this.personBean = personBean;
        this.facePic = facePic;
        this.onlyForCheck = onlyForCheck;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public PersonBean getPersonBean() {
        return personBean;
    }

    public void setPersonBean(PersonBean personBean) {
        this.personBean = personBean;
    }

    public boolean isFacePic() {
        return facePic;
    }

    public void setFacePic(boolean facePic) {
        this.facePic = facePic;
    }

    public boolean isOnlyForCheck() {
        return onlyForCheck;
    }

    public void setOnlyForCheck(boolean onlyForCheck) {
        this.onlyForCheck = onlyForCheck;
    }

    @Override
    public String toString() {
        return "ImageSaveBean{" +
                "id=" + id +
                ", data=" + Arrays.toString(data) +
                ", personBean=" + personBean +
                ", facePic=" + facePic +
                ", onlyForCheck=" + onlyForCheck +
                '}';
    }
}
