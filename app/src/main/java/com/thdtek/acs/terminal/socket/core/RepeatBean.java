package com.thdtek.acs.terminal.socket.core;


/**
 * 用于网络请求时保存需要重复请求的次数和已经重复请求的次数
 */
public class RepeatBean {
    //需要重复请求的次数
    private int needRepeatNum;
    //已经重复请求的次数
    private int repeatNum;

    public RepeatBean() {
    }

    public RepeatBean(int needRepeatNum, int repeatNum) {
        this.needRepeatNum = needRepeatNum;
        this.repeatNum = repeatNum;
    }

    public int getNeedRepeatNum() {
        return needRepeatNum;
    }

    public void setNeedRepeatNum(int needRepeatNum) {
        this.needRepeatNum = needRepeatNum;
    }

    public int getRepeatNum() {
        return repeatNum;
    }

    public void setRepeatNum(int repeatNum) {
        this.repeatNum = repeatNum;
    }

    @Override
    public String toString() {
        return "RepeatBean{" +
                "needRepeatNum=" + needRepeatNum +
                ", repeatNum=" + repeatNum +
                '}';
    }
}
