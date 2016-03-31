package com.pkrobertson.demo.mg2016.data;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by Phil Robertson on 3/29/2016.
 */
public class TestUriBuilders extends AndroidTestCase {
    private static final String LOG_TAG = TestUriBuilders.class.getSimpleName();

    public void testConfigUri () {
        Uri testUri = DatabaseContract.ConfigEntry.buildConfigUri(0);
        String testPath = testUri.getPath();
        Log.d(LOG_TAG, "testConfigUri() ==> " + testPath);
        assertEquals(testPath, "/config/0");
    }

    public void testLodgingUri () {
        Uri testUri = DatabaseContract.LodgingEntry.buildLodgingUri(0);
        String testPath = testUri.getPath();
        Log.d(LOG_TAG, "testLodgingUri() ==> " + testPath);
        assertEquals(testPath, "/lodging/0");
    }

    public void testNewsUri () {
        Uri testUri = DatabaseContract.NewsEntry.buildNewsUri(0);
        String testPath = testUri.getPath();
        Log.d (LOG_TAG, "testNewsUri() ==> " + testPath);
        assertEquals(testPath, "/news/0");
    }

    public void testEventsUri () {
        Uri testUri = DatabaseContract.EventsEntry.buildEventsUri(0);
        String testPath = testUri.getPath();
        Log.d (LOG_TAG, "testEventsUri() ==> " + testUri.toString() + " path ==> " + testPath);
        assertEquals(testPath, "/events/0");

        testUri = DatabaseContract.EventsEntry.buildEventsUriWithStartDate(1234);
        testPath = testUri.getPath();
        Log.d(LOG_TAG, "testEventsUri() ==> " + testUri.toString() + " path ==> " + testPath);
        assertEquals (testPath, "/events");

        assertEquals (DatabaseContract.EventsEntry.getStartDateFromUri(testUri), 1234);
    }
}
