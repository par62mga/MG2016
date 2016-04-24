package com.pkrobertson.demo.mg2016.data;

import android.test.AndroidTestCase;

import java.util.Calendar;

/**
 * Created by Phil Robertson on 3/31/2016.
 */
public class TestDateTime extends AndroidTestCase {
    private static final String LOG_TAG = TestUriBuilders.class.getSimpleName();

    public void testDates () {
        long date;
        date = DateTimeHelper.toDate ("2015/12/03");
        assertEquals(date, 20151203);
        date = DateTimeHelper.toDate ("2015/12/13");
        assertEquals(date, 20151213);
        date = DateTimeHelper.toDate ("2015/1/3");
        assertEquals(date, -1);
        date = DateTimeHelper.toDate ("2016/06/13");
        assertEquals(date, 20160613);

        Calendar calendarDate = DateTimeHelper.getCalendar(date);
        assertEquals (calendarDate.get(Calendar.YEAR), 2016);
        assertEquals (calendarDate.get(Calendar.MONTH), 5);
        assertEquals (calendarDate.get(Calendar.DAY_OF_MONTH), 13);
        date = DateTimeHelper.toDate (
                calendarDate.get(Calendar.YEAR),
                calendarDate.get(Calendar.MONTH) + 1,
                calendarDate.get(Calendar.DAY_OF_MONTH));
        assertEquals(date, 20160613);
        date = DateTimeHelper.addNumberDays(date, 1);
        assertEquals(date, 20160614);
        date = DateTimeHelper.addNumberDays(date, 7);
        assertEquals(date, 20160621);
        date = DateTimeHelper.addNumberDays(date, 7);
        assertEquals(date, 20160628);
        date = DateTimeHelper.addNumberDays(date, 7);
        assertEquals(date, 20160705);

        int numberDays = DateTimeHelper.getNumberDays (20160714, 20160614);
        assertEquals(numberDays, 30);

        String stringDate = DateTimeHelper.formatDate ("EEE - dd MMMM", 20160714);
        assertEquals ("Thu - 14 July", stringDate);
    }

    public void testTimes () {
        long time;
        time = DateTimeHelper.toTime ("12:10");
        assertEquals(time, 1210);
        time = DateTimeHelper.toTime ("09:09");
        assertEquals(time, 909);
        time = DateTimeHelper.toTime ("09:9");
        assertEquals(time, -1);
    }
}
