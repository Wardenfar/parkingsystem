package com.parkit.parkingsystem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestUtils {

    /**
     * Helper Function that parse a date time with the format : yyyy/MM/dd HH:mm
     *
     * @param timeString string with format : yyyy/MM/dd HH:mm
     * @return the parsed date
     */
    public static Date parseTime(String timeString) {
        try {
            DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            return format.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
