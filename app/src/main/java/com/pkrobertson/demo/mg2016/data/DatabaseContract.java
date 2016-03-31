package com.pkrobertson.demo.mg2016.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * DatabaseContract -- Defines URIs, table and column names for the MG 2016 database. *
 */
public class DatabaseContract {

    // Define name for the content provider
    public static final String CONTENT_AUTHORITY = "com.pkrobertson.demo.mg2016";

    // Define base URI which apps will use to contact the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Define paths appended to base content URI
    public static final String PATH_CONFIG  = "config";
    public static final String PATH_LODGING = "lodging";
    public static final String PATH_NEWS    = "news";
    public static final String PATH_EVENTS  = "events";

    // TODO: normalize dates and times as longs

    /**
     * ConfigEntry -- Inner class that defines content of the app config table
     */
    public static final class ConfigEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONFIG).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONFIG;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONFIG;

        public static final String TABLE_NAME = PATH_CONFIG;

        // long: time zone offset
        public static final String COLUMN_TZ_OFFSET = "tz_offset";

        // long: app sync interval (in hours)
        public static final String COLUMN_SYNC_INTERVAL = "sync_interval";

        // string: production URL of web server
        public static final String COLUMN_PROD_URL = "prod_url";

        // string: test URL of web server
        public static final String COLUMN_TEST_URL = "test_url";

        // string: folder holding images under test/prod URL
        public static final String COLUMN_IMAGE_FOLDER = "image_folder";

        // string: image to show on the contact us page
        public static final String COLUMN_CONTACT_IMAGE = "contact_image";

        // string: contact us email address
        public static final String COLUMN_CONTACT_EMAIL = "contact_email";

        // string: default location name for events
        public static final String COLUMN_DEFAULT_LOCATION = "default_location";

        // string: default location map coordinates for events
        public static final String COLUMN_DEFAULT_MAP = "default_map";

        // string: amount information shown on the contact us page
        public static final String COLUMN_ABOUT_INFO = "about_info";

        // define SQL statement used to create the config table
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_TZ_OFFSET + " TEXT NOT NULL, " +
                COLUMN_SYNC_INTERVAL + " INTEGER NOT NULL, " +
                COLUMN_PROD_URL + " TEXT NOT NULL, " +
                COLUMN_TEST_URL + " TEXT NOT NULL, " +
                COLUMN_IMAGE_FOLDER + " TEXT NOT NULL, " +
                COLUMN_CONTACT_IMAGE + " TEXT NOT NULL, " +
                COLUMN_CONTACT_EMAIL + " TEXT NOT NULL, " +
                COLUMN_DEFAULT_LOCATION + " TEXT NOT NULL, " +
                COLUMN_DEFAULT_MAP + " TEXT NOT NULL, " +
                COLUMN_ABOUT_INFO + " TEXT NOT NULL );";

        public static Uri buildConfigUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /**
     * LodgingEntry -- Inner class that defines content of the lodging table
     */
    public static final class LodgingEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LODGING).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LODGING;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LODGING;

        public static final String TABLE_NAME = PATH_LODGING;

        // string: image to show next to the hotel name
        public static final String COLUMN_IMAGE = "image";

        // string: hotel name
        public static final String COLUMN_NAME = "name";

        // string: hotel address line 1
        public static final String COLUMN_ADDRESS1 = "address1";

        // string: hotel address line 2
        public static final String COLUMN_ADDRESS2 = "address2";

        // string: hotel phone number
        public static final String COLUMN_PHONE = "phone";

        // string: hotel details
        public static final String COLUMN_DETAILS = "details";

        // define SQL statement used to create lodging information
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_IMAGE + " TEXT NOT NULL, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_ADDRESS1 + " TEXT NOT NULL, " +
                COLUMN_ADDRESS2 + " TEXT NOT NULL, " +
                COLUMN_PHONE + " TEXT NOT NULL, " +
                COLUMN_DETAILS + " TEXT NOT NULL );";

        public static Uri buildLodgingUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /**
     * NewsEntry -- Inner class that defines content of the news feed
     */
    public static final class NewsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NEWS;

        public static final String TABLE_NAME = PATH_NEWS;

        // long: publish date
        public static final String COLUMN_DATE = "date";

        // long: publish time
        public static final String COLUMN_TIME = "time";

        // string: news image
        public static final String COLUMN_IMAGE = "image";

        // string: news title
        public static final String COLUMN_TITLE = "title";

        // string: news byline 1
        public static final String COLUMN_BYLINE1 = "byline1";

        // string: news byline 2
        public static final String COLUMN_BYLINE2 = "byline2";

        // string: news content
        public static final String COLUMN_CONTENT = "content";

        // define SQL statement used to create the news feed
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_DATE + " INTEGER NOT NULL, " +
                COLUMN_TIME + " INTEGER NOT NULL, " +
                COLUMN_IMAGE + " TEXT NOT NULL, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_BYLINE1 + " TEXT NOT NULL, " +
                COLUMN_BYLINE2 + " TEXT NOT NULL, " +
                COLUMN_CONTENT + " TEXT NOT NULL );";

        public static Uri buildNewsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /**
     * EventsEntry -- Inner class that defines content of the Event Diary
     */
    public static final class EventsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENTS;

        public static final String TABLE_NAME = PATH_EVENTS;

        // long: start date
        public static final String COLUMN_START_DATE = "start_date";

        // long: start time
        public static final String COLUMN_START_TIME = "start_time";

        // long: end time
        public static final String COLUMN_END_TIME = "end_time";

        // string: event title
        public static final String COLUMN_TITLE = "title";

        // string: event location (or default)
        public static final String COLUMN_LOCATION = "location";

        // string: event map location (or none)
        public static final String COLUMN_MAP_LOCATION = "map_location";

        // string: event content
        public static final String COLUMN_CONTENT = "content";

        // define SQL statement used to create the events diary
        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_START_DATE + " INTEGER NOT NULL, " +
                COLUMN_START_TIME + " INTEGER NOT NULL, " +
                COLUMN_END_TIME + " INTEGER, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_MAP_LOCATION + " TEXT, " +
                COLUMN_CONTENT + " TEXT );";

        public static Uri buildEventsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildEventsUriWithStartDate(long startDate) {
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_START_DATE, String.valueOf(startDate)).build();
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_START_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }

    }
}
