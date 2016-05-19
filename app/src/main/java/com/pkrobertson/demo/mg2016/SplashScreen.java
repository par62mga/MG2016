package com.pkrobertson.demo.mg2016;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.pkrobertson.demo.mg2016.data.AppConfig;
import com.pkrobertson.demo.mg2016.sync.AppSyncAdapter;

/**
 * SplashScreen -- simple activity to show splash screen while initializing/updating
 *     the local database.
 */
public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_DELAY = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // initialize database and sync adapter
        AppSyncAdapter.initializeSyncAdapter(this);

        // if no data found, sync right away
        AppConfig appConfig = AppConfig.getInstance (this);
        if (appConfig == null) {
            AppSyncAdapter.syncImmediately(this);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // start main activity
                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity (mainIntent);

                // close this activity
                finish ();
            }
        }, SPLASH_DELAY);
    }

}
