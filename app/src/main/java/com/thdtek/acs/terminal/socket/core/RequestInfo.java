package com.thdtek.acs.terminal.socket.core;

import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.socket.command.DelayItem;

public class RequestInfo {
    private long seq;
    private Msg.Package pkg;
    private long timeout;
    private RepeatBean repeatBean;
    private Class clazz;
    private RequestCallback listener;
    private DelayItem<Long> delayItem;

    public RequestInfo() {

    }

    @Override
    public String toString() {
        return "RequestInfo{" +
                "seq=" + seq +
                ", pkg=" + pkg +
                ", timeout=" + timeout +
                ", repeatBean=" + repeatBean +
                ", clazz=" + clazz +
                ", listener=" + listener +
                ", delayItem=" + delayItem +
                '}';
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public Msg.Package getPkg() {
        return pkg;
    }

    public void setPkg(Msg.Package pkg) {
        this.pkg = pkg;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public RepeatBean getRepeatBean() {
        return repeatBean;
    }

    public void setRepeatBean(RepeatBean repeatBean) {
        this.repeatBean = repeatBean;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public RequestCallback getListener() {
        return listener;
    }

    public void setListener(RequestCallback listener) {
        this.listener = listener;
    }

    public DelayItem<Long> getDelayItem() {
        return delayItem;
    }

    public void setDelayItem(DelayItem<Long> delayItem) {
        this.delayItem = delayItem;
    }
}