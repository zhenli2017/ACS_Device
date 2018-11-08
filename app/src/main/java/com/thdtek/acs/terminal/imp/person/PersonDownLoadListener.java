package com.thdtek.acs.terminal.imp.person;

/**
 * Time:2018/9/26
 * User:lizhen
 * Description:
 */

public interface PersonDownLoadListener {
    void personDownLoadStart(String msg,long delay);

    void personDownLoadEnd(String msg,long delay);
}
