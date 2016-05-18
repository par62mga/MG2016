package com.pkrobertson.demo.mg2016.data;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * DateTimeHelper -- define helper functions to manage dates and times.
 *     Dates: are longs stored in YYYYMMDD (decimal) format.
 *     Times: are longs stored in HHMM (decimal) format.
 */
public class DateTimeHelper {
    private static final int MILLIS_IN_DAY  = 1000 * 60 * 60 * 24;
    private static final int MILLIS_IN_HOUR = 1000 * 60 * 60;

    /**
     * getCalendar -- get Calendar from long date
     * @param date
     * @return Calendar
     */
    public static Calendar getCalendar (long date) {
        return new GregorianCalendar(
                (int)(date / 10000), (int)((date / 100) % 100) - 1, (int)date %100);
    }

    /**
     * formatDate -- using sdf string, return formatted string from long date
     * @param formatString
     * @param date
     * @return formatted String
     */
    public static String formatDate (String formatString, long date) {
        SimpleDateFormat sdf = new SimpleDateFormat (formatString);
        Calendar calendarDate = getCalendar(date);
        sdf.setCalendar (calendarDate);
        return sdf.format(calendarDate.getTime());
    }

    /**
     * getNumberDays -- return number of days between two long dates
     * @param endDate
     * @param startDate
     * @return int number of days
     */
    public static int getNumberDays (long endDate, long startDate) {
        Calendar endCalendar   = getCalendar (endDate);
        Calendar startCalendar = getCalendar (startDate);
        long millis = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
        return ((int)((millis + 1) / MILLIS_IN_DAY));
    }

    /**
     * addNumberDays -- return long date that is numberDays different than given date
     * @param date
     * @param numberDays
     * @return long date
     */
    public static long addNumberDays (long date, int numberDays) {
        Calendar thisDate = getCalendar(date);
        thisDate.add(Calendar.DAY_OF_MONTH, numberDays);
        return toDate (
                thisDate.get(Calendar.YEAR),
                thisDate.get(Calendar.MONTH) + 1,
                thisDate.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * toDate -- return long date given year, month, day
     * @param year
     * @param month
     * @param day
     * @return long date
     */
    public static long toDate (long year, long month, long day) {
        return year * 10000 + month * 100 + day;
    }

    /**
     * toDate -- return long date given String date from Server
     * @param dateFromServer
     * @return long date
     */
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

    /**
     * formatTime -- return hh:ss string or hh:ss am/pm string given long time
     * @param time
     * @param use24HourClock
     * @return String formatted time
     */
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

    /**
     * toTime -- return long time given hour and minute
     * @param hour
     * @param minute
     * @return long time
     */
    public static long toTime (long hour, long minute) {
        return hour * 100 + minute;
    }

    /**
     * toTime -- return long time given String time from server
     * @param timeFromServer
     * @return long time
     */
    public static long toTime (String timeFromServer) {
        if (timeFromServer.length() == 5 && timeFromServer.charAt(2) == ':') {
            long hour   = Long.parseLong(timeFromServer.substring(0, 2));
            long minute = Long.parseLong(timeFromServer.substring(3));
            return toTime (hour, minute);
        } else {
            return -1;
        }
    }

    // determine time zone adjustment in milliseconds based on current device settings

    /**
     * getTimeZoneAdjustment -- given time zone offset in milliseconds for dates/times on server,
     *     return time zone difference in milliseconds based on local device settings
     * @param tzOffset
     * @return long adjustment in mmilliseconds
     */
    public static long getTimeZoneAdjustment (long tzOffset) {
        Calendar calendar = Calendar.getInstance ();
        return tzOffset - calendar.getTimeZone().getRawOffset();
    }

    /**
     * getDateTimeInMillis -- get date and time in millisecond format (not adjusted)
     * @param date
     * @param time
     * @return
     */
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

    /**
     * getCurrentDate -- return current long date, adjusting for difference between server and
     *     device settings
     * @param serverTzAdjustment
     * @return long date
     */
    public static long getCurrentDate (long serverTzAdjustment) {
        Calendar thisDate = new GregorianCalendar ();
        // adjust date based on current time zone vs time zone from server
        long adjustment = getTimeZoneAdjustment(serverTzAdjustment);
        if (adjustment != 0) {
            thisDate.add (Calendar.MILLISECOND, (int)adjustment);
        }

        return toDate(
                thisDate.get(Calendar.YEAR),
                thisDate.get(Calendar.MONTH) + 1,
                thisDate.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * getCurrentTime -- return current long time, adjusting for difference between server and
     *     device settings
     * @param serverTzAdjustment
     * @return long time
     */
    public static long getCurrentTime (long serverTzAdjustment) {
        Calendar thisDate = new GregorianCalendar ();
        // adjust time based on current time zone vs time zone from server
        long adjustment = getTimeZoneAdjustment(serverTzAdjustment);
        if (adjustment != 0) {
            thisDate.add (Calendar.MILLISECOND, (int)adjustment);
        }

        return toTime(
                thisDate.get(Calendar.HOUR_OF_DAY),
                thisDate.get(Calendar.MINUTE));
    }
}
