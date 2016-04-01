package com.pkrobertson.demo.mg2016.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * AppSyncService -- Defines the sync adapter service.
 *
 * This code is copied directly, in its entirety, from
 * http://developer.android.com/training/sync-adapters/creating-sync-adapter.html
 */
public class AppSyncService extends Service {
    private static final String LOG_TAG = AppSyncService.class.getSimpleName();

    private static final Object sSyncAdapterLock = new Object();

    private static AppSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate ()");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new AppSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}