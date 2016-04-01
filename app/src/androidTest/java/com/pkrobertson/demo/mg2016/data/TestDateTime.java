package com.pkrobertson.demo.mg2016.data;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by Phil Robertson on 3/31/2016.
 */
public class TestDateTime extends AndroidTestCase {
    private static final String LOG_TAG = TestUriBuilders.class.getSimpleName();

    public void testDates () {
        long date;
        date = DatabaseContract.toDate ("2015/12/03");
        assertEquals(date, 20151203);
        date = DatabaseContract.toDate ("2015/12/13");
        assertEquals(date, 20151213);
        date = DatabaseContract.toDate ("2015/1/3");
        assertEquals(date, -1);
    }

    public void testTimes () {
        long time;
        time = DatabaseContract.toTime ("12:10");
        assertEquals(time, 1210);
        time = DatabaseContract.toTime ("09:09");
        assertEquals(time, 909);
        time = DatabaseContract.toTime ("09:9");
        assertEquals(time, -1);
    }
}
