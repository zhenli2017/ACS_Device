package com.thdtek.acs.terminal.http.serverpush;

import com.thdtek.acs.terminal.Msg;

/**
 * Time:2018/7/2
 * User:lizhen
 * Description:
 */

public interface PushInterface {
    Msg.Message onResponse(Msg.Message message, int seq);
}
