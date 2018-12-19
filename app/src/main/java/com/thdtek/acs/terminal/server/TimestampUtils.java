package com.thdtek.acs.terminal.server;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class TimestampUtils {

    public double second_double_to_millisecond_double_45(double timestamp){


        String re = new DecimalFormat("#").format(timestamp * 1000);

        return Double.parseDouble(re);

    }



}
