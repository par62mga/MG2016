package com.pkrobertson.demo.mg2016.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pkrobertson.demo.mg2016.data.DatabaseContract.ConfigEntry;
import com.pkrobertson.demo.mg2016.data.DatabaseContract.LodgingEntry;
import com.pkrobertson.demo.mg2016.data.DatabaseContract.NewsEntry;
import com.pkrobertson.demo.mg2016.data.DatabaseContract.EventsEntry;

/**
 * DatabaseHelper -- Manages the local MG 2016 database.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Update the version number when the schema changes
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "mg2016.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ConfigEntry.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(LodgingEntry.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(NewsEntry.SQL_CREATE_TABLE);
        sqLiteDatabase.execSQL(EventsEntry.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ConfigEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LodgingEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NewsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EventsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
