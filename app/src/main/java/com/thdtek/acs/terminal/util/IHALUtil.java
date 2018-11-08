package com.thdtek.acs.terminal.util;

import com.thdtek.hal.IHALInterface;

public class IHALUtil {

    private static IHALInterface mIhalInterface;

    public static void setIHALInterface(IHALInterface ihalInterface) {
        mIhalInterface = ihalInterface;

    }

    public static IHALInterface getmIhalInterface() throws NullPointerException{
        return mIhalInterface;
    }


}
