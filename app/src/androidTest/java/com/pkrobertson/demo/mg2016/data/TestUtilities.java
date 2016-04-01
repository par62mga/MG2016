package com.pkrobertson.demo.mg2016.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.pkrobertson.demo.mg2016.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * TestUtilities -- basic test utilities used for content provider. Taken from Udacity Sunshine
 *     app and modified for the MG 2016 Content Provider.
 */
public class TestUtilities extends AndroidTestCase {

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /**
     * createTestConfigValues -- fill in default values for a config record
     */
    static ContentValues createTestConfigValues() {
        ContentValues configValues = new ContentValues();
        configValues.put(DatabaseContract.ConfigEntry._ID, 1);
        configValues.put(DatabaseContract.ConfigEntry.COLUMN_TZ_OFFSET, -5);
        configValues.put(DatabaseContract.ConfigEntry.COLUMN_SYNC_MINUTES, 1);
        configValues.put(DatabaseContract.ConfigEntry.COLUMN_FLEX_MINUTES, 1);
        configValues.put(DatabaseContract.ConfigEntry.COLUMN_PROD_URL, "prod");
        configValues.put(DatabaseContract.ConfigEntry.COLUMN_TEST_URL, "test");
        configValues.put(DatabaseContract.ConfigEntry.COLUMN_IMAGE_FOLDER, "/images");
        configValues.put(DatabaseContract.ConfigEntry.COLUMN_CONTACT_IMAGE, "contact.png");
        configValues.put(DatabaseContract.ConfigEntry.COLUMN_CONTACT_EMAIL, "goaway@dontcare.com");
        configValues.put(DatabaseContract.ConfigEntry.COLUMN_DEFAULT_LOCATION, "Paradise Lakes");
        configValues.put(DatabaseContract.ConfigEntry.COLUMN_DEFAULT_MAP, "12345678,-12345678");
        configValues.put(DatabaseContract.ConfigEntry.COLUMN_ABOUT_INFO, "About us");

        return configValues;
    }

    /**
     * createTestLodgingValues -- fill in default values for a lodging record
     */
    static ContentValues createTestLodgingValues() {
        ContentValues lodgingValues = new ContentValues();
        lodgingValues.put(DatabaseContract.LodgingEntry._ID, 1);
        lodgingValues.put(DatabaseContract.LodgingEntry.COLUMN_IMAGE, "hotel.gif");
        lodgingValues.put(DatabaseContract.LodgingEntry.COLUMN_NAME, "Roach Hotel");
        lodgingValues.put(DatabaseContract.LodgingEntry.COLUMN_ADDRESS1, "123 Bad Part of Town");
        lodgingValues.put(DatabaseContract.LodgingEntry.COLUMN_ADDRESS2, "Plano, TX 12345");
        lodgingValues.put(DatabaseContract.LodgingEntry.COLUMN_PHONE, "972-727-2727");
        lodgingValues.put(DatabaseContract.LodgingEntry.COLUMN_DETAILS, "Lots of cool insects");

        return lodgingValues;
    }

    /**
     * createTestNewsValues -- fill in default values for a news record
     */
    static ContentValues createTestNewsValues() {
        ContentValues newsValues = new ContentValues();
        newsValues.put(DatabaseContract.NewsEntry._ID, 1);
        newsValues.put(DatabaseContract.NewsEntry.COLUMN_DATE, 20160329);
        newsValues.put(DatabaseContract.NewsEntry.COLUMN_TIME, 1200);
        newsValues.put(DatabaseContract.NewsEntry.COLUMN_IMAGE, "news.gif");
        newsValues.put(DatabaseContract.NewsEntry.COLUMN_TITLE, "Welcome");
        newsValues.put(DatabaseContract.NewsEntry.COLUMN_BYLINE1, "MG 2016");
        newsValues.put(DatabaseContract.NewsEntry.COLUMN_BYLINE2, "Hello");
        newsValues.put(DatabaseContract.NewsEntry.COLUMN_CONTENT, "Yada Yada Yada");

        return newsValues;
    }

    /**
     * createTestEventsValues -- fill in default values for a events record
     */
    static ContentValues createTestEventsValues() {
        ContentValues eventsValues = new ContentValues();
        eventsValues.put(DatabaseContract.EventsEntry._ID, 1);
        eventsValues.put(DatabaseContract.EventsEntry.COLUMN_START_DATE, 20160329);
        eventsValues.put(DatabaseContract.EventsEntry.COLUMN_START_TIME, 1200);
        eventsValues.put(DatabaseContract.EventsEntry.COLUMN_END_TIME, 1400);
        eventsValues.put(DatabaseContract.EventsEntry.COLUMN_TITLE, "Car Show");
        eventsValues.put(DatabaseContract.EventsEntry.COLUMN_LOCATION, "Hotel");
        eventsValues.put(DatabaseContract.EventsEntry.COLUMN_MAP_LOCATION, "12345678,-12345678");
        eventsValues.put(DatabaseContract.EventsEntry.COLUMN_CONTENT, "Lots of fun");

        return eventsValues;
    }

    /**
     * TestContentObserver -- functions we provide inside of TestProvider use this utility class to test
     *     the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
     *     CTS tests.
     *
     * Note that this only tests that the onChange function is called; it does not test that the
     *     correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
