package com.thdtek.acs.terminal.imp.person.persondownload;

import com.thdtek.acs.terminal.imp.person.PersonDownLoadListener;

/**
 * Time:2018/9/26
 * User:lizhen
 * Description:
 */

public class PersonDownLoadImp {

    private PersonDownLoadListener mListener;

    private static PersonDownLoadImp mPersonDownLoadImp = new PersonDownLoadImp();

    public static PersonDownLoadImp getInstance() {
        return mPersonDownLoadImp;
    }

    public PersonDownLoadListener getListener() {
        return mListener;
    }

    public void setListener(PersonDownLoadListener listener) {
        mListener = listener;
    }

    public void personDownLoadStart(String msg,long delay) {
        if (mListener != null) {
            mListener.personDownLoadStart(msg,delay);
        }
    }

    public void personDownLoadEnd(String msg,long delay) {
        if (mListener != null) {
            mListener.personDownLoadEnd(msg, delay);
        }
    }

    public void clear() {
        mListener = null;
    }

}
