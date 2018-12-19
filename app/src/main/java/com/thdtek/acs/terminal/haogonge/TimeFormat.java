package com.thdtek.acs.terminal.haogonge;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TimeFormat {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    public String format(long time){
        return sdf.format(time);
    }

    public long parse(String timeStr) throws ParseException {
        return  sdf.parse(timeStr).getTime();
    }
}
