package com.pkrobertson.demo.mg2016;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

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
        // initialize database and sync adapter here
        String randomContent = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent vitae mollis ipsum. Aenean id odio nisi. Proin suscipit efficitur vehicula. Proin laoreet ipsum non tincidunt dapibus. Nam ultricies justo in libero aliquet tempor. Phasellus congue eget arcu eget tempus. Praesent sodales turpis ac velit ultricies venenatis. Fusce vehicula in sapien eget mattis. Quisque pulvinar sem at nisl varius, sit amet tempor tellus molestie. Quisque eget nisi nulla. Nullam ullamcorper dui a justo pharetra imperdiet. Curabitur elementum erat ipsum, molestie tempus ex euismod quis.\n" +
                "\n" +
                "Nullam sodales mi eu dui pulvinar luctus. Aenean ut nulla venenatis, semper diam eu, gravida nisi. Aenean vehicula enim in elit pulvinar suscipit. Donec massa ipsum, fringilla a commodo ut, venenatis vitae tellus. Vivamus lacinia est dui. Ut eros augue, suscipit sit amet suscipit et, gravida sed elit. Nulla ut lobortis urna. Pellentesque lobortis ac nisl eget gravida. Aenean tristique risus purus. Nunc ullamcorper ante ut mi pharetra, vel dapibus augue consectetur. Ut placerat purus sed purus rhoncus vestibulum. Sed sed dolor ligula.";
        NewsItem.createNewsItem (
                "June 13-16, 2016", "Welcome to MG2016", "Maintaining the Breed", randomContent);
        NewsItem.createNewsItem (
                "March 11, 2016", "NAMGBR Concours Judging Guidelines", "MG 2016", randomContent);
        NewsItem.createNewsItem (
                "March 11, 2016", "Official Rocker Cover Racer Rules", "MG 2016", randomContent);

        EventItem.createEventItem(
                "9:00am", "2:00pm", "MG2016 CAR SHOW", "Waterfront park\nRocker cover races at show");
        EventItem.createEventItem (
                "9:30am", "3:00pm", "Outlet Mall Bus", "");
        EventItem.createEventItem (
                "5:00pm", "7:00pm", "Pre-Banquet Awards", "4 separate events (by register)");
        EventItem.createEventItem (
                "7:00pm", "?", "Wrap-Up Banquet", "One last bash before you head home");

        LodgingItem.createLodgingItem(
                "Crowne Plaza", "800 Phillips Lane\nLouisville, KY 40209", "+1-502-366-8100", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent vitae mollis ipsum.");
        LodgingItem.createLodgingItem(
                "Comfort Suites", "800 Phillips Lane\nLouisville, KY 40209", "+1-502-366-8100", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent vitae mollis ipsum.");
        LodgingItem.createLodgingItem(
                "Courtyard", "800 Phillips Lane\nLouisville, KY 40209", "+1-502-366-8100", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent vitae mollis ipsum.");
        LodgingItem.createLodgingItem(
                "Hampton Inn", "800 Phillips Lane\nLouisville, KY 40209", "+1-502-366-8100", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent vitae mollis ipsum.");

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
