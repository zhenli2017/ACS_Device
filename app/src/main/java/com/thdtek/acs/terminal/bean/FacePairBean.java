package com.thdtek.acs.terminal.bean;

/**
 * Time:2018/12/3
 * User:lizhen
 * Description:
 */

public class FacePairBean {
    private PersonBean personBean;
    private long accessTime;


    public PersonBean getPersonBean() {
        return personBean;
    }

    public void setPersonBean(PersonBean personBean) {
        this.personBean = personBean;
    }

    public long getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(long accessTime) {
        this.accessTime = accessTime;
    }
}
