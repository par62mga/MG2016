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
    static final int CONFIG  = 100;
    static final int LODGING = 200;
    static final int NEWS    = 300;
    static final int EVENTS  = 400;

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
        matcher.addURI(authority, DatabaseContract.PATH_LODGING, LODGING);
        matcher.addURI(authority, DatabaseContract.PATH_NEWS, NEWS);
        matcher.addURI(authority, DatabaseContract.PATH_EVENTS, EVENTS);
        return matcher;
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

            case LODGING:
                return DatabaseContract.LodgingEntry.CONTENT_TYPE;

            case NEWS:
                return DatabaseContract.NewsEntry.CONTENT_TYPE;

            case EVENTS:
                return DatabaseContract.EventsEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;

        // Find request type from the uri
        switch (sUriMatcher.match(uri)) {
            case EVENTS:
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
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.EventsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case NEWS:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.NewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case LODGING:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.LodgingEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CONFIG:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.ConfigEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

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
            case EVENTS:
                _id = db.insert(DatabaseContract.EventsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DatabaseContract.EventsEntry.buildEventsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            case NEWS:
                _id = db.insert(DatabaseContract.NewsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = DatabaseContract.NewsEntry.buildNewsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            case LODGING:
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
            case EVENTS:
                tableName = DatabaseContract.EventsEntry.TABLE_NAME;
                break;

            case NEWS:
                tableName = DatabaseContract.NewsEntry.TABLE_NAME;
                break;

            case LODGING:
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
            case EVENTS:
                tableName = DatabaseContract.EventsEntry.TABLE_NAME;
                break;

            case NEWS:
                tableName = DatabaseContract.NewsEntry.TABLE_NAME;
                break;

            case LODGING:
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