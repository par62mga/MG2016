package com.pkrobertson.demo.mg2016.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * CursorHelper -- simple helper class used to access cursor content by column name. For
 *     efficiency sake, this should only be used for single-record access like the AppConfig
 *     record or the NewsDetailFragment
 */
public class CursorHelper {

    Cursor mCursor;

    public CursorHelper (Context context, Uri contentUri) {
        mCursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (mCursor != null) {
            if (! mCursor.moveToFirst())
                mCursor = null;
        }
    }

    public boolean hasContent () {
        return mCursor != null;
    }

    public long getLong (String columnName) {
        if (mCursor != null) {
            int index = mCursor.getColumnIndex(columnName);
            if (index >= 0) {
                return mCursor.getLong(mCursor.getColumnIndex(columnName));
            }
        }
        return Long.MIN_VALUE;
    }

    public String getString (String columnName) {
        if (mCursor != null) {
            int index = mCursor.getColumnIndex(columnName);
            if (index >= 0) {
                return mCursor.getString(mCursor.getColumnIndex(columnName));
            }
        }
        return null;
    }

}
