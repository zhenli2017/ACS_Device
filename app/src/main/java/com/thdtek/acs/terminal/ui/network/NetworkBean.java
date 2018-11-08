package com.thdtek.acs.terminal.ui.network;

/**
 * Time:2018/6/22
 * User:lizhen
 * Description:
 */

public class NetworkBean {
    private String title;
    private String leftMsg;
    private String rightMsg;
    private int type;//0:标题,1:内容

    public NetworkBean() {
    }

    public NetworkBean(String title, String leftMsg, String rightMsg, int type) {
        this.title = title;
        this.leftMsg = leftMsg;
        this.rightMsg = rightMsg;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLeftMsg() {
        return leftMsg;
    }

    public void setLeftMsg(String leftMsg) {
        this.leftMsg = leftMsg;
    }

    public String getRightMsg() {
        return rightMsg;
    }

    public void setRightMsg(String rightMsg) {
        this.rightMsg = rightMsg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
