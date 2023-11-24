package com.Polarice3.Goety.utils;

import java.util.Calendar;

public class HolidayUtil {
    public static Calendar calendar = Calendar.getInstance();

    public static boolean isChristmasMonth(){
        return calendar.get(Calendar.MONTH) + 1 == 12;
    }

    public static boolean isChristmas(){
        return isChristmasMonth() && calendar.get(Calendar.DATE) == 24;
    }

    public static boolean isSpookyMonth(){
        return calendar.get(Calendar.MONTH) + 1 == 10;
    }

    public static boolean isHalloween(){
        return isSpookyMonth() && calendar.get(Calendar.DATE) == 31;
    }

    public static boolean isNewYear(){
        return calendar.get(Calendar.MONTH) + 1 == 1 && calendar.get(Calendar.DATE) == 1;
    }
}
