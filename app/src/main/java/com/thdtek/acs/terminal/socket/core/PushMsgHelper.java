package com.thdtek.acs.terminal.socket.core;

import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.http.serverpush.PushContext;

public class PushMsgHelper {

    private static final String TAG = PushMsgHelper.class.getSimpleName();

    public void hand(Msg.Message message, int seq) {
        Msg.Message message1 = new PushContext().onResponse(message, seq);
        new SendMsgHelper().response(message1, seq);
    }
}
