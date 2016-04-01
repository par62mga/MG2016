package com.pkrobertson.demo.mg2016.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * AppAuthenticatorService -- The service which allows the sync adapter
 *     framework to access the authenticator.
 *
 * This code is copied directly, in its entirety, from
 * http://developer.android.com/training/sync-adapters/creating-authenticator.html
 */
public class AppAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private StubAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new StubAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
