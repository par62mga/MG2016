package com.pkrobertson.demo.mg2016.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.util.Log;

import com.pkrobertson.demo.mg2016.R;
import com.pkrobertson.demo.mg2016.data.AppConfig;
import com.pkrobertson.demo.mg2016.data.DatabaseContract;
import com.pkrobertson.demo.mg2016.data.DateTimeHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * AppSyncAdapter -- MG 2016 sync adapter, derived from Udacity Sunshine sync adapter and other
 *     examples/tutorials
 */
public class AppSyncAdapter extends AbstractThreadedSyncAdapter {
    private final String LOG_TAG = AppSyncAdapter.class.getSimpleName();

    public static final String ACTION_DATA_UPDATED =
            "com.pkrobertson.demo.mg2016.ACTION_DATA_UPDATED";

    // Default sync interval, in minutes (this is overridden by "config"
    // 12 hours: 12 hours * 60 minutes/hour
    public static final int DEFAULT_SYNC_MINUTES = 12 * 60;
    public static final int DEFAULT_FLEX_MINUTES = 60;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SERVER_STATUS_OK, SERVER_STATUS_DOWN, SERVER_STATUS_INVALID,  SERVER_STATUS_UNKNOWN})
    public @interface AppServerStatus {}

    public static final int SERVER_STATUS_OK      = 0;
    public static final int SERVER_STATUS_DOWN    = 1;
    public static final int SERVER_STATUS_INVALID = 2;
    public static final int SERVER_STATUS_UNKNOWN = 3;

    private Context mContext;
    private int     mSyncMinutes = DEFAULT_SYNC_MINUTES;
    private int     mFlexMinutes = DEFAULT_FLEX_MINUTES;

    public AppSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        //Log.d(LOG_TAG, "Starting sync");

        mContext = getContext();

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            String serverURL = mContext.getString(R.string.server_base_url) +
                    mContext.getString(R.string.server_path_data);
            //Log.d (LOG_TAG, "onPerformSync() serverURL ==> " + serverURL);
            URL url = new URL(serverURL);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                setServerStatus (mContext, SERVER_STATUS_UNKNOWN);
                return;
            }
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                setServerStatus(mContext, SERVER_STATUS_DOWN);
                return;
            }
            getAppDataFromJson(buffer.toString());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            setServerStatus(mContext, SERVER_STATUS_DOWN);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            setServerStatus(mContext, SERVER_STATUS_INVALID);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    /**
     * getAppDataFromJson -- parse data from the server and populate the database
     */
    private void getAppDataFromJson(String appDataJson) throws JSONException {

        JSONObject appData     = new JSONObject(appDataJson);

        //TODO: improve efficiency by adding a separate "server version" file and only pull full
        //      content when that changes
        JSONObject configInfo  = appData.getJSONObject ("appConfig");
        JSONArray  lodgingInfo = appData.getJSONArray ("lodgingInfo");
        JSONArray  newsFeed    = appData.getJSONArray ("newsFeed");
        JSONArray  eventDiary  = appData.getJSONArray ("eventDiary");

        {// parse config data
            ContentValues configValues = new ContentValues();
            mSyncMinutes = configInfo.getInt("syncMinutes");
            mFlexMinutes = configInfo.getInt("flexMinutes");
            configValues.put(DatabaseContract.ConfigEntry._ID, 1);
            configValues.put(DatabaseContract.ConfigEntry.COLUMN_TZ_OFFSET, configInfo.getLong("tzOffset"));
            configValues.put(DatabaseContract.ConfigEntry.COLUMN_SYNC_MINUTES, (long)mSyncMinutes);
            configValues.put(DatabaseContract.ConfigEntry.COLUMN_FLEX_MINUTES, (long)mFlexMinutes);
            configValues.put(DatabaseContract.ConfigEntry.COLUMN_PROD_URL, configInfo.getString("productionURL"));
            configValues.put(DatabaseContract.ConfigEntry.COLUMN_TEST_URL, configInfo.getString("testURL"));
            configValues.put(DatabaseContract.ConfigEntry.COLUMN_IMAGE_FOLDER, configInfo.getString("imageFolder"));
            configValues.put(DatabaseContract.ConfigEntry.COLUMN_CONTACT_IMAGE, configInfo.getString("contactImage"));
            configValues.put(DatabaseContract.ConfigEntry.COLUMN_CONTACT_EMAIL, configInfo.getString("contactEmail"));
            configValues.put(DatabaseContract.ConfigEntry.COLUMN_DEFAULT_LOCATION, configInfo.getString("defaultLocation"));
            configValues.put(DatabaseContract.ConfigEntry.COLUMN_DEFAULT_MAP, configInfo.getString("defaultMap"));
            configValues.put(DatabaseContract.ConfigEntry.COLUMN_START_DATE, DateTimeHelper.toDate(configInfo.getString("eventStart")));
            configValues.put(DatabaseContract.ConfigEntry.COLUMN_END_DATE, DateTimeHelper.toDate(configInfo.getString("eventEnd")));
            configValues.put(DatabaseContract.ConfigEntry.COLUMN_EVENT_ADJUSTMENT, configInfo.getString("eventAdjustText"));
            configValues.put(DatabaseContract.ConfigEntry.COLUMN_ABOUT_INFO, configInfo.getString("aboutInfo"));
            mContext.getContentResolver().delete(DatabaseContract.ConfigEntry.CONTENT_URI, null, null);
            mContext.getContentResolver().insert(DatabaseContract.ConfigEntry.CONTENT_URI, configValues);
            AppConfig.forceRefresh();
        }

        {// parse lodging info
            ContentValues[] contentArray = new ContentValues[lodgingInfo.length()];
            for (int i = 0; i < lodgingInfo.length(); i++) {
                JSONObject    object = lodgingInfo.getJSONObject(i);
                ContentValues lodgingValues = new ContentValues();
                lodgingValues.put(DatabaseContract.LodgingEntry._ID, object.getLong("id"));
                lodgingValues.put(DatabaseContract.LodgingEntry.COLUMN_IMAGE, object.getString("image"));
                lodgingValues.put(DatabaseContract.LodgingEntry.COLUMN_NAME, object.getString("name"));
                lodgingValues.put(DatabaseContract.LodgingEntry.COLUMN_ADDRESS1, object.getString("addr1"));
                lodgingValues.put(DatabaseContract.LodgingEntry.COLUMN_ADDRESS2, object.getString("addr2"));
                lodgingValues.put(DatabaseContract.LodgingEntry.COLUMN_MAP_LOCATION, object.getString("mapLocation"));
                lodgingValues.put(DatabaseContract.LodgingEntry.COLUMN_PHONE, object.getString("phone"));
                lodgingValues.put(DatabaseContract.LodgingEntry.COLUMN_WEBSITE, object.getString("website"));
                lodgingValues.put(DatabaseContract.LodgingEntry.COLUMN_DETAILS, object.getString("details"));
                contentArray[i] = lodgingValues;
            }
            mContext.getContentResolver().delete(DatabaseContract.LodgingEntry.CONTENT_URI, null, null);
            int count = mContext.getContentResolver().bulkInsert(DatabaseContract.LodgingEntry.CONTENT_URI, contentArray);
            //Log.d (LOG_TAG, "inserted (" + count + ") lodging records");
        }

        {// parse news info
            ContentValues[] contentArray = new ContentValues[newsFeed.length()];
            for (int i = 0; i < newsFeed.length(); i++) {
                JSONObject    object = newsFeed.getJSONObject(i);
                ContentValues newsValues = new ContentValues();
                newsValues.put(DatabaseContract.NewsEntry._ID, object.getLong("id"));
                newsValues.put(DatabaseContract.NewsEntry.COLUMN_DATE, DateTimeHelper.toDate(object.getString("publishDate")));
                newsValues.put(DatabaseContract.NewsEntry.COLUMN_TIME, DateTimeHelper.toTime(object.getString("publishTime")));
                newsValues.put(DatabaseContract.NewsEntry.COLUMN_THUMBNAIL, object.getString("thumbnail"));
                newsValues.put(DatabaseContract.NewsEntry.COLUMN_IMAGE, object.getString("image"));
                newsValues.put(DatabaseContract.NewsEntry.COLUMN_TITLE, object.getString("title"));
                if (object.has("byline1")) {
                    newsValues.put(DatabaseContract.NewsEntry.COLUMN_BYLINE1, object.getString("byline1"));
                    if (object.has("byline2")) {
                        newsValues.put(DatabaseContract.NewsEntry.COLUMN_BYLINE2, object.getString("byline2"));
                    }
                }
                newsValues.put(DatabaseContract.NewsEntry.COLUMN_SHARE, object.getString("share"));
                newsValues.put(DatabaseContract.NewsEntry.COLUMN_CONTENT, object.getString("content"));
                contentArray[i] = newsValues;
            }
            mContext.getContentResolver().delete(DatabaseContract.NewsEntry.CONTENT_URI, null, null);
            int count = mContext.getContentResolver().bulkInsert(DatabaseContract.NewsEntry.CONTENT_URI, contentArray);
            //Log.d(LOG_TAG, "inserted (" + count + ") news records");
        }

        {// parse event info
            ContentValues[] contentArray = new ContentValues[eventDiary.length()];
            for (int i = 0; i < eventDiary.length(); i++) {
                JSONObject    object = eventDiary.getJSONObject(i);
                ContentValues eventsValues = new ContentValues();
                eventsValues.put(DatabaseContract.EventsEntry._ID, object.getLong("id"));
                eventsValues.put(DatabaseContract.EventsEntry.COLUMN_START_DATE, DateTimeHelper.toDate(object.getString("startDate")));
                eventsValues.put(DatabaseContract.EventsEntry.COLUMN_START_TIME, DateTimeHelper.toTime(object.getString("startTime")));
                long endTime = -1;
                if (object.has("endTime")) {
                    endTime = DateTimeHelper.toTime(object.getString("endTime"));
                }
                eventsValues.put(DatabaseContract.EventsEntry.COLUMN_END_TIME, endTime);
                eventsValues.put(DatabaseContract.EventsEntry.COLUMN_TITLE, object.getString("title"));
                if (object.has("location")) {
                    eventsValues.put(DatabaseContract.EventsEntry.COLUMN_LOCATION, object.getString("location"));
                }
                if (object.has("mapLocation")) {
                    eventsValues.put(DatabaseContract.EventsEntry.COLUMN_MAP_LOCATION, object.getString("mapLocation"));
                }
                if (object.has("content")) {
                    eventsValues.put(DatabaseContract.EventsEntry.COLUMN_CONTENT, object.getString("content"));
                }
                contentArray[i] = eventsValues;
            }
            mContext.getContentResolver().delete(DatabaseContract.EventsEntry.CONTENT_URI, null, null);
            int count = mContext.getContentResolver().bulkInsert(DatabaseContract.EventsEntry.CONTENT_URI, contentArray);
            //Log.d (LOG_TAG, "inserted (" + count + ") event records");
        }

        //Log.d(LOG_TAG, "Sync Complete");
        setServerStatus(mContext, SERVER_STATUS_OK);

        // update sync interval based on configuration file settings
        AppSyncAdapter.configurePeriodicSync(mContext, mSyncMinutes * 60, mFlexMinutes * 60);

        // Tell any widgets that data was updated
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(mContext.getPackageName());
        mContext.sendBroadcast(dataUpdatedIntent);
    }



    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

			/*
			 * Add the account and account type, no password or user data
			 * If successful, return the Account object, otherwise report an error.
			 */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        // do this once when the account is initially created, this will be over-ridden on the first sync
        AppSyncAdapter.configurePeriodicSync(context, DEFAULT_SYNC_MINUTES * 60, DEFAULT_FLEX_MINUTES * 60);

        // enable period syncing
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        // perform an initial sync to get data from the server
        // syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Sets the server status into shared preference.  This function should not be called from
     * the UI thread because it uses commit to write to the shared preferences.
     * @param c Context to get the PreferenceManager from.
     * @param serverStatus The IntDef value to set
     */
    static private void setServerStatus(Context c, @AppServerStatus int serverStatus){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_server_status_key), serverStatus);
        spe.commit();
    }
}