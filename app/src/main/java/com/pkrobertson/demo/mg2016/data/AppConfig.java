package com.pkrobertson.demo.mg2016.data;

import android.content.Context;

/**
 * AppConfig -- singleton used to manage and return config record from the database
 */
public class AppConfig {
    private static AppConfig sAppConfig;

    private final long   mTzOffset;
    private final long   mSyncMinutes;
    private final long   mFlexMinutes;
    private final String mProductionURL;
    private final String mTestURL;
    private final String mImageFolder;
    private final String mContactImage;
    private final String mContactEmail;
    private final String mDefaultLocation;
    private final String mDefaultMap;
    private final long   mStartDate;
    private final long   mEndDate;
    private final String mEventAdjustText;
    private final String mAboutInfo;

    /**
     * AppConfig -- read database and populate config object
     * @param context
     */
    private AppConfig (Context context, CursorHelper cursor) {
        mTzOffset = cursor.getLong(DatabaseContract.ConfigEntry.COLUMN_TZ_OFFSET);
        mSyncMinutes = cursor.getLong(DatabaseContract.ConfigEntry.COLUMN_SYNC_MINUTES);
        mFlexMinutes = cursor.getLong(DatabaseContract.ConfigEntry.COLUMN_FLEX_MINUTES);
        mProductionURL = cursor.getString(DatabaseContract.ConfigEntry.COLUMN_PROD_URL);
        mTestURL = cursor.getString(DatabaseContract.ConfigEntry.COLUMN_TEST_URL);
        mImageFolder = cursor.getString(DatabaseContract.ConfigEntry.COLUMN_IMAGE_FOLDER);
        mContactImage = cursor.getString(DatabaseContract.ConfigEntry.COLUMN_CONTACT_IMAGE);
        mContactEmail = cursor.getString(DatabaseContract.ConfigEntry.COLUMN_CONTACT_EMAIL);
        mDefaultLocation = cursor.getString(DatabaseContract.ConfigEntry.COLUMN_DEFAULT_LOCATION);
        mDefaultMap = cursor.getString(DatabaseContract.ConfigEntry.COLUMN_DEFAULT_MAP);
        mStartDate = cursor.getLong(DatabaseContract.ConfigEntry.COLUMN_START_DATE);
        mEndDate = cursor.getLong (DatabaseContract.ConfigEntry.COLUMN_END_DATE);
        mEventAdjustText = cursor.getString(DatabaseContract.ConfigEntry.COLUMN_EVENT_ADJUSTMENT);
        mAboutInfo = cursor.getString(DatabaseContract.ConfigEntry.COLUMN_ABOUT_INFO);
    }

    /**
     * getInstance () -- return current config object or get a new one
     * @param context
     * @return shared null if no data is available when server is down.
     *     NOTES:(config contents may change so do not keep outside of method scope)
     */
    public static AppConfig getInstance (Context context) {
        if (sAppConfig == null) {
            CursorHelper cursor = new CursorHelper(context, DatabaseContract.ConfigEntry.CONTENT_URI);
            if (cursor.hasContent()) {
                sAppConfig = new AppConfig(context, cursor);
            }
        }
        return sAppConfig;
    }

    /**
     * forceRefresh () -- used to signal when database config record is updated and reload needed
     */
    public static void forceRefresh () {
        sAppConfig = null;
    }

    public long getTzOffset () {
        return mTzOffset;
    }

    public long getSyncMinutes () {
        return mSyncMinutes;
    }

    public long getFlexMinutes () {
        return mFlexMinutes;
    }

    public String getContentURL () {
        //TODO: need to return production or test URL based on settings
        return mProductionURL;
    }

    public String getImageURL (String imageFile) {
        //TODO: need to return production or test URL based on settings
        if (imageFile == null) {
            return null;
        } else {
            return mProductionURL + mImageFolder + "/" + imageFile;
        }
    }

    public String getContactImageFile () {
        return mContactImage;
    }

    public String getContactEmail () {
        return mContactEmail;
    }

    public String getDefaultLocation () {
        return mDefaultLocation;
    }

    public String getDefaultMap () {
        return mDefaultMap;
    }

    public long getStartDate () {
        return mStartDate;
    }

    public long getEndDate () {
        return mEndDate;
    }

    public String getEventAdjustText () {
        return mEventAdjustText;
    }

    public String getAboutInfo () {
        return mAboutInfo;
    }

}
