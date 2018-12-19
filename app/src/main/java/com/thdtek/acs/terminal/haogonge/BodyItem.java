package com.thdtek.acs.terminal.haogonge;

public class BodyItem {
    private String cmd;
    private long id;
    private int code;
    private String msg;

    public BodyItem() {
    }

    public BodyItem(String cmd, long id, int code, String msg) {
        this.cmd = cmd;
        this.id = id;
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "BodyItem{" +
                "cmd='" + cmd + '\'' +
                ", id=" + id +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
