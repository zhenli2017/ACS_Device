package com.thdtek.acs.terminal.imp.person;

import com.thdtek.acs.terminal.bean.PersonBean;

/**
 * Time:2018/7/2
 * User:lizhen
 * Description:
 */

public interface PersonCheckInterface {
    void success();

    void fail(String msg, PersonBean peopleBean);
}
