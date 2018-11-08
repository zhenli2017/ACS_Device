package com.example.lib;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MyClass {
    public static void main(String[] args) {

        String dayForWeek = getDayForWeek();
        System.out.println(dayForWeek);
        String format = String.format(Locale.getDefault(), "%tR", System.currentTimeMillis());
        System.out.println(format);
        String[] split = format.split(":");


    }

    public static String getDayForWeek() {
        Calendar instance = Calendar.getInstance();
        int i = instance.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return "";
        }

    }

}
