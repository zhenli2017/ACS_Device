package com.thdtek.acs.terminal.bean;

public class FaceThreadBean {

    private long authoidtyId;
    private float pairMaxNumber;
    private float accordNumber;
    private float personNumber;
    private PersonBean personBean;

    public long getAuthoidtyId() {
        return authoidtyId;
    }

    public void setAuthoidtyId(long authoidtyId) {
        this.authoidtyId = authoidtyId;
    }

    public float getPairMaxNumber() {
        return pairMaxNumber;
    }

    public void setPairMaxNumber(float pairMaxNumber) {
        this.pairMaxNumber = pairMaxNumber;
    }

    public float getAccordNumber() {
        return accordNumber;
    }

    public void setAccordNumber(float accordNumber) {
        this.accordNumber = accordNumber;
    }

    public float getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(float personNumber) {
        this.personNumber = personNumber;
    }

    public PersonBean getPersonBean() {
        return personBean;
    }

    public void setPersonBean(PersonBean personBean) {
        this.personBean = personBean;
    }

    public FaceThreadBean() {
    }

    public FaceThreadBean(long authoidtyId, float pairMaxNumber, float accordNumber, float personNumber, PersonBean personBean) {
        this.authoidtyId = authoidtyId;
        this.pairMaxNumber = pairMaxNumber;
        this.accordNumber = accordNumber;
        this.personNumber = personNumber;
        this.personBean = personBean;
    }
}
