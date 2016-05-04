package com.pkrobertson.demo.mg2016.data;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Phil Robertson on 4/21/2016.
 */
public class DateTimeHelper {
    private static final int MILLIS_IN_DAY  = 1000 * 60 * 60 * 24;
    private static final int MILLIS_IN_HOUR = 1000 * 60 * 60;

    public static Calendar getCalendar (long date) {
        return new GregorianCalendar(
                (int)(date / 10000), (int)((date / 100) % 100) - 1, (int)date %100);
    }

    public static String formatDate (String formatString, long date) {
        SimpleDateFormat sdf = new SimpleDateFormat (formatString);
        Calendar calendarDate = getCalendar(date);
        sdf.setCalendar (calendarDate);
        return sdf.format(calendarDate.getTime());
    }

    public static int getNumberDays (long endDate, long startDate) {
        Calendar endCalendar   = getCalendar (endDate);
        Calendar startCalendar = getCalendar (startDate);
        long millis = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
        return ((int)((millis + 1) / MILLIS_IN_DAY));
    }

    public static long addNumberDays (long date, int numberDays) {
        Calendar thisDate = getCalendar(date);
        thisDate.add(Calendar.DAY_OF_MONTH, numberDays);
        return toDate (
                thisDate.get(Calendar.YEAR),
                thisDate.get(Calendar.MONTH) + 1,
                thisDate.get(Calendar.DAY_OF_MONTH));
    }

    public static long toDate (long year, long month, long day) {
        return year * 10000 + month * 100 + day;
    }

    public static long toDate (String dateFromServer) {
        if (dateFromServer.length()  == 10  &&
                dateFromServer.charAt(4) == '/' &&
                dateFromServer.charAt(7) == '/'    ) {
            long year  = Long.parseLong(dateFromServer.substring(0, 4));
            long month = Long.parseLong(dateFromServer.substring(5, 7));
            long day   = Long.parseLong(dateFromServer.substring(8));
            return toDate (year, month, day);
        } else {
            return -1;
        }
    }

    public static String formatTime (long time, boolean use24HourClock) {
        String minutes = String.valueOf(time % 100);
        if (minutes.length() < 2) {
            minutes = "0" + minutes;
        }
        if (use24HourClock) {
            String hours = String.valueOf(time / 100);
            if (hours.length() < 2) {
                hours = " " + hours;
            }
            return hours + ":" + minutes;
        } else {
            String modifier = " am";
            time = time / 100;
            if (time >= 12) {
                time -= 12;
                modifier = " pm";
            }
            if (time == 0) {
                time = 12;
            }
            String hours = String.valueOf(time);
            if (hours.length() < 2) {
                hours = " " + hours;
            }
            return hours + ":" + minutes + modifier;
        }

    }

    public static long toTime (long hour, long minute) {
        return hour * 100 + minute;
    }
    public static long toTime (String timeFromServer) {
        if (timeFromServer.length() == 5 && timeFromServer.charAt(2) == ':') {
            long hour   = Long.parseLong(timeFromServer.substring(0, 2));
            long minute = Long.parseLong(timeFromServer.substring(3));
            return toTime (hour, minute);
        } else {
            return -1;
        }
    }

    public static long getTimeZoneAdjustment (long tzOffset) {
        Calendar calendar = Calendar.getInstance ();
        return tzOffset - calendar.getTimeZone().getRawOffset();
    }

    public static long getDateTimeInMillis (long date, long time) {
        Calendar dateTime = Calendar.getInstance();
        dateTime.set(
                (int) (date / 10000),
                (int)(((date / 100) % 100) - 1),
                (int)(date % 100),
                (int)(time / 100),
                (int)(time % 100));
        return dateTime.getTimeInMillis();
    }

    public static long getCurrentDate () {
        Calendar thisDate = new GregorianCalendar ();
        //TODO: need to reflect tz adjustment
        return toDate(
                thisDate.get(Calendar.YEAR),
                thisDate.get(Calendar.MONTH) + 1,
                thisDate.get(Calendar.DAY_OF_MONTH));
    }

    public static long getCurrentTime () {
        Calendar thisDate = new GregorianCalendar ();
        //TODO: need to reflect tz adjustment
        return toTime(
                thisDate.get(Calendar.HOUR_OF_DAY),
                thisDate.get(Calendar.MINUTE));
    }
}
