package com.pkrobertson.demo.mg2016.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Phil Robertson on 4/1/2016.
 */
public class CursorHelper {

    Cursor mCursor;

    public CursorHelper (Context context, Uri contentUri) {
        mCursor = context.getContentResolver().query(contentUri, null, null, null, null);
        mCursor.moveToFirst();
    }

    public long getLong (String columnName) {
        return mCursor.getLong(mCursor.getColumnIndex(columnName));
    }

    public String getString (String columnName) {
        return mCursor.getString(mCursor.getColumnIndex(columnName));
    }

}
