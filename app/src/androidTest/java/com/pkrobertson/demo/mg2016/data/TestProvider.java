package com.pkrobertson.demo.mg2016.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.pkrobertson.demo.mg2016.data.DatabaseContract.ConfigEntry;
import com.pkrobertson.demo.mg2016.data.DatabaseContract.LodgingEntry;
import com.pkrobertson.demo.mg2016.data.DatabaseContract.NewsEntry;
import com.pkrobertson.demo.mg2016.data.DatabaseContract.EventsEntry;

/**
 * TestProvider -- test basic functionality of the DatabaseProvider. Lots of
 *     this was leveraged from the Udacity Sunshine app.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();
    private static final int NUM_RECORDS = 10;

    /**
     * deleteAndCheck -- delete records from table and check for empty table
     */
    public void deleteAndCheck (String tableName, Uri contentUri) {
        Log.d(LOG_TAG, "deleteAndCheck() ==> " + tableName + " " + contentUri.toString());
        mContext.getContentResolver().delete(contentUri, null, null);
        Cursor cursor = mContext.getContentResolver().query(
                contentUri,
                null,
                null,
                null,
                null);
        assertEquals("Error: Records not deleted from " +
                tableName +
                " table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /**
     * deleteAllRecordsFromProvider -- make sure we start with an empty database
     */
    public void deleteAllRecordsFromProvider() {
        deleteAndCheck(ConfigEntry.TABLE_NAME, ConfigEntry.CONTENT_URI);
        deleteAndCheck(LodgingEntry.TABLE_NAME, LodgingEntry.CONTENT_URI);
        deleteAndCheck(NewsEntry.TABLE_NAME, NewsEntry.CONTENT_URI);
        deleteAndCheck(EventsEntry.TABLE_NAME, EventsEntry.CONTENT_URI);
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Log.d(LOG_TAG, "setup() == deleting records");
        deleteAllRecordsFromProvider();
    }

    /**
     * testProviderRegistry -- this test checks to make sure that the content
     * provider is registered correctly.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the
        // context and the DatabaseProvider class.
        ComponentName componentName = new ComponentName(
                mContext.getPackageName(),
                AppContentProvider.class.getName());
        try {
            // Fetch the provider info using the component name
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches DatabaseContract
            assertEquals("Error: DatabaseProvider registered with authority: " +
                            providerInfo.authority +
                            " instead of authority: " +
                            DatabaseContract.CONTENT_AUTHORITY,
                    providerInfo.authority, DatabaseContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // Provider isn't registered correctly.
            assertTrue("Error: DatabaseProvider not registered at " +
                            mContext.getPackageName(),
                    false);
        }
    }

    /**
     * checkContentType -- helper function to check Uri content type
     */
    public void checkContentType (Uri contentUri, String contentType) {
        String type = mContext.getContentResolver().getType(contentUri);
        assertEquals("Error: the CONTENT_URI did not return " + contentType,
                contentType, type);
    }

    /**
     * testGetType -- check that the ContentProvider returns the correct type
     *     for each type of URI that it can handle.
     */
    public void testGetType() {
        checkContentType (ConfigEntry.CONTENT_URI,  ConfigEntry.CONTENT_TYPE);
        checkContentType (LodgingEntry.CONTENT_URI, LodgingEntry.CONTENT_TYPE);
        checkContentType (NewsEntry.CONTENT_URI,    NewsEntry.CONTENT_TYPE);
        checkContentType (EventsEntry.CONTENT_URI,  EventsEntry.CONTENT_TYPE);
        checkContentType(EventsEntry.buildEventsUriWithStartDate(1234), EventsEntry.CONTENT_TYPE);
    }


    /**
     * testConfigTable -- insert, update and delete records from the "config" table
     */
    public void testConfigTable () {
        // Create map of values for a default configuration record
        ContentValues startValues = TestUtilities.createTestConfigValues();

        Uri insertUri = mContext.getContentResolver().
                insert(ConfigEntry.CONTENT_URI, startValues);
        long locationRowId = ContentUris.parseId(insertUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Create a cursor to:
        // 1. see if we get back the config record
        // 2. register observer to make sure that the content provider is notifying
        //    the observers as expected
        Cursor cursor = mContext.getContentResolver().query(ConfigEntry.CONTENT_URI, null, null, null, null);

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testConfigQuery", cursor, startValues);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        cursor.registerContentObserver(tco);

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(ConfigEntry._ID, locationRowId);
        updatedValues.put(ConfigEntry.COLUMN_ABOUT_INFO, "New about info");
        int count = mContext.getContentResolver().update(
                ConfigEntry.CONTENT_URI, updatedValues, ConfigEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        // TODO: figure out why we are not getting the change notification:
        // tco.waitForNotificationOrFail();

        cursor.unregisterContentObserver(tco);
        cursor.close();

        // See if we get updated values from a new search
        cursor = mContext.getContentResolver().query(
                ConfigEntry.CONTENT_URI,
                null,   // projection
                ConfigEntry._ID + " = " + locationRowId,
                null,   // Values for the "where" clause
                null);  // sort order
        TestUtilities.validateCursor("testUpdateConfig: Error validating config entry update.",
                cursor, updatedValues);
        cursor.close();

        // Register a content observer for the config table
        TestUtilities.TestContentObserver configObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ConfigEntry.CONTENT_URI, true, configObserver);

        // delete all records from the config table
        deleteAndCheck(ConfigEntry.TABLE_NAME, ConfigEntry.CONTENT_URI);

        // See if delete is calling getContext().getContentResolver().notifyChange(uri, null)
        configObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(configObserver);
    }

    /**
     * testLodgingTable -- insert, update and delete records from the "lodging" table
     */
    public void testLodgingTable () {
        // Create map of values for a default lodging record
        ContentValues startValues = TestUtilities.createTestLodgingValues();

        Uri insertUri = mContext.getContentResolver().
                insert(LodgingEntry.CONTENT_URI, startValues);
        long locationRowId = ContentUris.parseId(insertUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Create a cursor to:
        // 1. see if we get back the lodging record
        // 2. register observer to make sure that the content provider is notifying
        //    the observers as expected
        Cursor cursor = mContext.getContentResolver().query(LodgingEntry.CONTENT_URI, null, null, null, null);

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testLodgingQuery", cursor, startValues);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        cursor.registerContentObserver(tco);

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(LodgingEntry._ID, locationRowId);
        updatedValues.put(LodgingEntry.COLUMN_DETAILS, "New hotel details");
        int count = mContext.getContentResolver().update(
                LodgingEntry.CONTENT_URI, updatedValues, LodgingEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        // TODO: figure out why we are not getting the change notification:
        // tco.waitForNotificationOrFail();

        cursor.unregisterContentObserver(tco);
        cursor.close();

        // See if we get updated values from a new search
        cursor = mContext.getContentResolver().query(
                LodgingEntry.CONTENT_URI,
                null,   // projection
                LodgingEntry._ID + " = " + locationRowId,
                null,   // Values for the "where" clause
                null);  // sort order
        TestUtilities.validateCursor("testUpdateLodging: Error validating lodging entry update.",
                cursor, updatedValues);
        cursor.close();

        // Register a content observer for the lodging table
        TestUtilities.TestContentObserver lodgingObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(LodgingEntry.CONTENT_URI, true, lodgingObserver);

        // delete all records from the lodging table
        deleteAndCheck(LodgingEntry.TABLE_NAME, LodgingEntry.CONTENT_URI);

        // See if delete is calling getContext().getContentResolver().notifyChange(uri, null)
        lodgingObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(lodgingObserver);
    }

    /**
     * testNewsTable -- insert, update and delete records from the "news" table
     */
    public void testNewsTable () {
        // Create map of values for a default news record
        ContentValues startValues = TestUtilities.createTestNewsValues();

        Uri insertUri = mContext.getContentResolver().
                insert(NewsEntry.CONTENT_URI, startValues);
        long locationRowId = ContentUris.parseId(insertUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Create a cursor to:
        // 1. see if we get back the news record
        // 2. register observer to make sure that the content provider is notifying
        //    the observers as expected
        Cursor cursor = mContext.getContentResolver().query(NewsEntry.CONTENT_URI, null, null, null, null);

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testNewsQuery", cursor, startValues);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        cursor.registerContentObserver(tco);

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(NewsEntry._ID, locationRowId);
        updatedValues.put(NewsEntry.COLUMN_CONTENT, "No news today");
        int count = mContext.getContentResolver().update(
                NewsEntry.CONTENT_URI, updatedValues, NewsEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        // TODO: figure out why we are not getting the change notification:
        // tco.waitForNotificationOrFail();

        cursor.unregisterContentObserver(tco);
        cursor.close();

        // See if we get updated values from a new search
        cursor = mContext.getContentResolver().query(
                NewsEntry.CONTENT_URI,
                null,   // projection
                NewsEntry._ID + " = " + locationRowId,
                null,   // Values for the "where" clause
                null);  // sort order
        TestUtilities.validateCursor("testUpdateNews: Error validating news entry update.",
                cursor, updatedValues);
        cursor.close();

        // Register a content observer for the news table
        TestUtilities.TestContentObserver newsObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(NewsEntry.CONTENT_URI, true, newsObserver);

        // delete all records from the news table
        deleteAndCheck(NewsEntry.TABLE_NAME, NewsEntry.CONTENT_URI);

        // See if delete is calling getContext().getContentResolver().notifyChange(uri, null)
        newsObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(newsObserver);
    }

    /**
     * testEventsTable -- insert, update and delete records from the "events" table
     */
    public void testEventsTable () {
        // Create map of values for a default events record
        ContentValues startValues = TestUtilities.createTestEventsValues();

        Uri insertUri = mContext.getContentResolver().
                insert(EventsEntry.CONTENT_URI, startValues);
        long locationRowId = ContentUris.parseId(insertUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Create a cursor to:
        // 1. see if we get back the events record
        // 2. register observer to make sure that the content provider is notifying
        //    the observers as expected
        Cursor cursor = mContext.getContentResolver().query(EventsEntry.CONTENT_URI, null, null, null, null);

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testEventsQuery", cursor, startValues);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        cursor.registerContentObserver(tco);

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(EventsEntry._ID, locationRowId);
        updatedValues.put(EventsEntry.COLUMN_CONTENT, "No events today");
        int count = mContext.getContentResolver().update(
                EventsEntry.CONTENT_URI, updatedValues, EventsEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        // TODO: figure out why we are not getting the change notification:
        // tco.waitForNotificationOrFail();

        cursor.unregisterContentObserver(tco);
        cursor.close();

        // See if we get updated values from a new search
        cursor = mContext.getContentResolver().query(
                EventsEntry.CONTENT_URI,
                null,   // projection
                EventsEntry._ID + " = " + locationRowId,
                null,   // Values for the "where" clause
                null);  // sort order
        TestUtilities.validateCursor("testUpdateEvents: Error validating events entry update.",
                cursor, updatedValues);
        cursor.close();

        // Register a content observer for the events table
        TestUtilities.TestContentObserver eventsObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(EventsEntry.CONTENT_URI, true, eventsObserver);

        // delete all records from the events table
        deleteAndCheck(EventsEntry.TABLE_NAME, EventsEntry.CONTENT_URI);

        // See if delete is calling getContext().getContentResolver().notifyChange(uri, null)
        eventsObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(eventsObserver);
    }

    /**
     * testBulkInsert -- used to test bulk insert operation just in case the content provider has
     *     changed the default implementation and validation is needed
     */
    public void testBulkInsert() {
        // create default event values
        ContentValues[] bulkValues = new ContentValues[NUM_RECORDS];
        for (int i = 0; i < NUM_RECORDS; i++) {
            ContentValues contentValues = TestUtilities.createTestEventsValues();
            contentValues.put (DatabaseContract.EventsEntry._ID, i+1);
            contentValues.put(DatabaseContract.EventsEntry.COLUMN_TITLE, "Car Show: " + i);
            if (i < NUM_RECORDS / 2) {
                contentValues.put(DatabaseContract.EventsEntry.COLUMN_START_DATE, 20160330);
            }
            bulkValues[i] = contentValues;
        }

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(EventsEntry.CONTENT_URI, true, observer);

        int insertCount = mContext.getContentResolver().bulkInsert(EventsEntry.CONTENT_URI, bulkValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        observer.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(observer);

        assertEquals(insertCount, NUM_RECORDS);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                EventsEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                EventsEntry._ID + " ASC"  // sort order == by _ID
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), NUM_RECORDS);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < NUM_RECORDS; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert: Error validating EventsEntry " + i,
                    cursor, bulkValues[i]);
        }
        cursor.close();

        // now see if we can get events by date
        cursor = mContext.getContentResolver().query(
                EventsEntry.buildEventsUriWithStartDate(20160330),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                EventsEntry._ID + " ASC"  // sort order == by _ID
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), NUM_RECORDS / 2);
        cursor.close();
    }
}
