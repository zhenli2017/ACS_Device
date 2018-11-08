package com.thdtek.acs.terminal.bean;

/**
 * Time:2018/11/2
 * User:lizhen
 * Description:
 */

public class WeeklyBean {

    /**
     * Monday : 00:00-24:00
     * Tuesday : 00:00-24:00
     * Wednesday : 00:00-24:00
     * Thursday : 00:00-24:00
     * Friday : 00:00-24:00
     * Saturday : 00:00-24:00
     * Sunday : 00:00-24:00
     */

    private String Monday;
    private String Tuesday;
    private String Wednesday;
    private String Thursday;
    private String Friday;
    private String Saturday;
    private String Sunday;

    public String getMonday() {
        return Monday;
    }

    public void setMonday(String Monday) {
        this.Monday = Monday;
    }

    public String getTuesday() {
        return Tuesday;
    }

    public void setTuesday(String Tuesday) {
        this.Tuesday = Tuesday;
    }

    public String getWednesday() {
        return Wednesday;
    }

    public void setWednesday(String Wednesday) {
        this.Wednesday = Wednesday;
    }

    public String getThursday() {
        return Thursday;
    }

    public void setThursday(String Thursday) {
        this.Thursday = Thursday;
    }

    public String getFriday() {
        return Friday;
    }

    public void setFriday(String Friday) {
        this.Friday = Friday;
    }

    public String getSaturday() {
        return Saturday;
    }

    public void setSaturday(String Saturday) {
        this.Saturday = Saturday;
    }

    public String getSunday() {
        return Sunday;
    }

    public void setSunday(String Sunday) {
        this.Sunday = Sunday;
    }
}
