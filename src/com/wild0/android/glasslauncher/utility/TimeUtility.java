package com.wild0.android.glasslauncher.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by roy on 2015/2/27.
 */
public class TimeUtility {
    public static Date convertStrToDate(String str) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parsedDate;
        try {
            parsedDate = dateFormat.parse(str);
            return parsedDate;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new Date();
        }

    }
    public static String convertDateToStr(long time) {
        //Date d = new Date(time);

        //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //return dateFormat.format(d);
        return convertDateToStr(time, "yyyy-MM-dd HH:mm:ss");
    }
    public static String convertDateToStr(long time, String customFormat) {
        Date d = new Date(time);

        DateFormat dateFormat = new SimpleDateFormat(customFormat);
        return dateFormat.format(d);
    }

    public static String convertDateToStr(Date d) {

        //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //return dateFormat.format(d);
        return convertDateToStr(d, "yyyy-MM-dd HH:mm:ss");
    }
    public static String convertDateToStr(Date d, String customFormat) {

        DateFormat dateFormat = new SimpleDateFormat(customFormat);
        return dateFormat.format(d);
    }

    public static int getHour(long time) {
        Date d = new Date(time);
        return d.getHours();

    }
}
