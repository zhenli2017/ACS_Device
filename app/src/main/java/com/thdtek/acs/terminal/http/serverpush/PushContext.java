package com.thdtek.acs.terminal.http.serverpush;

import android.text.TextUtils;

import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.http.serverpush.pushImp.PushAd;
import com.thdtek.acs.terminal.http.serverpush.pushImp.PushApk;
import com.thdtek.acs.terminal.http.serverpush.pushImp.PushCheckPerson;
import com.thdtek.acs.terminal.http.serverpush.pushImp.PushCommend;
import com.thdtek.acs.terminal.http.serverpush.pushImp.PushConfig;
import com.thdtek.acs.terminal.http.serverpush.pushImp.PushQueryDataList;
import com.thdtek.acs.terminal.http.serverpush.pushImp.PushQueryPassRecord;
import com.thdtek.acs.terminal.http.serverpush.pushImp.PushQueryPerson;
import com.thdtek.acs.terminal.http.serverpush.pushImp.PushRebootOrOpenAndCloseDoor;
import com.thdtek.acs.terminal.http.serverpush.pushImp.PushRsynNotify;
import com.thdtek.acs.terminal.http.serverpush.pushImp.PushSetTime;
import com.thdtek.acs.terminal.socket.core.SendMsgHelper;
import com.thdtek.acs.terminal.util.LogUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Time:2018/7/2
 * User:lizhen
 * Description:
 */

public class PushContext {
    private static final String TAG = PushContext.class.getSimpleName();

    private static HashMap<String, String> mHashMap = new HashMap<>();

    static {
        mHashMap.put("SET_CONFIG_REQ", PushConfig.class.getName());
        mHashMap.put("SET_TIME_REQ", PushSetTime.class.getName());
        mHashMap.put("RSYNC_ACTION_REQ", PushRsynNotify.class.getName());
        mHashMap.put("RSYNC_PERSON_REQ", PushQueryPerson.class.getName());
        mHashMap.put("RSYNC_DATA_LIST_REQ", PushQueryDataList.class.getName());
        mHashMap.put("FETCH_PASS_RECORD_REQ", PushQueryPassRecord.class.getName());
        mHashMap.put("DEVICE_CTRL_REQ", PushRebootOrOpenAndCloseDoor.class.getName());
        mHashMap.put("UPDATE_APK_REQ", PushApk.class.getName());
        mHashMap.put("NOTIFY_AD_UPDATE_REQ", PushAd.class.getName());
        mHashMap.put("SEND_CMD_REQ", PushCommend.class.getName());
        mHashMap.put("RSYNC_CHECK_PERSON_REQ", PushCheckPerson.class.getName());
    }

    public Msg.Message onResponse(Msg.Message message, int seq) {
        String interfaceName = message.getBodyCase().name();
        LogUtils.d(TAG, "interfaceName="+interfaceName);
        if (TextUtils.isEmpty(interfaceName)) {
            LogUtils.e(TAG, "onResponse interfaceName = null,return");
            return null;
        }
        String className = mHashMap.get(interfaceName);
        if (TextUtils.isEmpty(className)) {
            LogUtils.e(TAG, "onResponse className = null,return");
            return null;
        }
        LogUtils.d(TAG,"============ 收到推送 ============" );
        try {
            Class<?> login = Class.forName(className);
            Object o = login.newInstance();
            Method getLocalData = login.getMethod("onResponse", Msg.Message.class, int.class);

            //响应服务器
            Msg.Message message1 = (Msg.Message) getLocalData.invoke(o, message, seq);

            return message1;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
