package com.pkrobertson.demo.mg2016.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * AppContentProvider -- Define Uri handling and methods to store and retrieve data
 *
 */
public class AppContentProvider extends ContentProvider {
    private static final String LOG_TAG = AppContentProvider.class.getSimpleName();

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // define values return for the different URI matches
    static final int CONFIG       = 100;
    static final int LODGING_LIST = 200;
    static final int LODGING_ID   = 201;
    static final int NEWS_LIST    = 300;
    static final int NEWS_ID      = 301;
    static final int EVENTS_LIST  = 400;
    static final int EVENTS_ID    = 401;

    private DatabaseHelper mOpenHelper;


    /**
     * builtUriMatcher -- return UriMatcher that matches each expected URI with the
     *     result.
     */
    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.CONTENT_AUTHORITY;

        // Add each path to match with the above codes
        matcher.addURI(authority, DatabaseContract.PATH_CONFIG, CONFIG);

        matcher.addURI(authority, DatabaseContract.PATH_LODGING, LODGING_LIST);
        matcher.addURI(authority,
                DatabaseContract.PATH_LODGING + DatabaseContract.PATH_ID, LODGING_ID);

        matcher.addURI(authority, DatabaseContract.PATH_NEWS, NEWS_LIST);
        matcher.addURI(authority,
                DatabaseContract.PATH_NEWS + DatabaseContract.PATH_ID, NEWS_ID);

        matcher.addURI(authority, DatabaseContract.PATH_EVENTS, EVENTS_LIST);
        matcher.addURI(authority,
                DatabaseContract.PATH_EVENTS + DatabaseContract.PATH_ID, EVENTS_ID);

        return matcher;
    }

    private String updateSelectionWithID (String selection, Uri uri) {
        if (selection != null) {
            selection = " AND " + selection;
        } else {
            selection = "";
        }
        selection = DatabaseContract.EventsEntry._ID + " = " + uri.getLastPathSegment() + selection;
        Log.d (LOG_TAG, "query() selection ==> " + selection);
        return selection;
    }


    /**
     * onCreate -- create a new DatabaseHelper in this case
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    /**
     * getType -- return content type based on Uri
     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case CONFIG:
                return DatabaseContract.ConfigEntry.CONTENT_TYPE;

            case LODGING_LIST:
                return DatabaseContract.LodgingEntry.CONTENT_TYPE;

            case LODGING_ID:
                return DatabaseContract.LodgingEntry.CONTENT_ITEM_TYPE;

            case NEWS_LIST:
                return DatabaseContract.NewsEntry.CONTENT_TYPE;

            case NEWS_ID:
                return DatabaseContract.NewsEntry.CONTENT_ITEM_TYPE;

            case EVENTS_LIST:
                return DatabaseContract.EventsEntry.CONTENT_TYPE;

            case EVENTS_ID:
                return DatabaseContract.EventsEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        String tableName = null;
        Cursor retCursor = null;

        // Find request type from the uri
        switch (sUriMatcher.match(uri)) {
            case EVENTS_LIST:
                tableName = DatabaseContract.EventsEntry.TABLE_NAME;
                long startDate = DatabaseContract.EventsEntry.getStartDateFromUri(uri);
                if ( startDate != 0 ) {
                    if (selection != null) {
                        selection = " AND " + selection;
                    } else {
                        selection = "";
                    }
                    selection = DatabaseContract.EventsEntry.COLUMN_START_DATE + " = " + startDate + selection;
                    Log.d (LOG_TAG, "query() selection ==> " + selection);
                }
                break;

            case EVENTS_ID:
                tableName = DatabaseContract.EventsEntry.TABLE_NAME;
                selection = updateSelectionWithID(selection, uri);
                break;

            case NEWS_LIST:
                tableName = DatabaseContract.NewsEntry.TABLE_NAME;
                break;

            case NEWS_ID:
                tableName = DatabaseContract.NewsEntry.TABLE_NAME;
                selection = updateSelectionWithID(selection, uri);
                break;

            case LODGING_LIST:
                tableName = DatabaseContract.LodgingEntry.TABLE_NAME;
                break;

            case LODGING_ID:
                tableName = DatabaseContract.LodgingEntry.TABLE_NAME;
                selection = updateSelectionWithID(selection, uri);
                break;

            case CONFIG:
                tableName = DatabaseContract.ConfigEntry.TABLE_NAME;
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor = mOpenHelper.getReadableDatabase().query(
                tableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        Log.d (LOG_TAG, "setNotificationUri() ==> " + uri.toString());
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /**
     * insert -- support simple insert operations into the different tables
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        long _id;

        switch (sUriMatcher.match(uri)) {
            case EVENTS_LIST:
                _id = db.insert(DatabaseContract.EventsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DatabaseContract.EventsEntry.buildEventsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            case NEWS_LIST:
                _id = db.insert(DatabaseContract.NewsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DatabaseContract.NewsEntry.buildNewsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            case LODGING_LIST:
                _id = db.insert(DatabaseContract.LodgingEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DatabaseContract.LodgingEntry.buildLodgingUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            case CONFIG:
                _id = db.insert(DatabaseContract.ConfigEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DatabaseContract.ConfigEntry.buildConfigUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    /**
     * delete -- support delete operations with the different tables
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        String tableName = null;
        switch (sUriMatcher.match(uri)) {
            case EVENTS_LIST:
                tableName = DatabaseContract.EventsEntry.TABLE_NAME;
                break;

            case NEWS_LIST:
                tableName = DatabaseContract.NewsEntry.TABLE_NAME;
                break;

            case LODGING_LIST:
                tableName = DatabaseContract.LodgingEntry.TABLE_NAME;
                break;

            case CONFIG:
                tableName = DatabaseContract.ConfigEntry.TABLE_NAME;
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        int rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                tableName, selection, selectionArgs);
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        String tableName = null;
        switch (sUriMatcher.match(uri)) {
            case EVENTS_LIST:
                tableName = DatabaseContract.EventsEntry.TABLE_NAME;
                break;

            case NEWS_LIST:
                tableName = DatabaseContract.NewsEntry.TABLE_NAME;
                break;

            case LODGING_LIST:
                tableName = DatabaseContract.LodgingEntry.TABLE_NAME;
                break;

            case CONFIG:
                tableName = DatabaseContract.ConfigEntry.TABLE_NAME;
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        int rowsUpdated = mOpenHelper.getWritableDatabase().update(
                tableName, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            Log.d (LOG_TAG, "notifyChange() ==> " + uri.toString());
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    // TODO: implement more efficient bulk insert method to operate on lodging, news, events

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}