package com.pkrobertson.demo.mg2016;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pkrobertson.demo.mg2016.data.AppConfig;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Phil Robertson on 11/19/2015.
 */
public class Utility {
    private static String LOG_TAG = Utility.class.getSimpleName();

    private static long MILLIS_IN_MINUTE = 1000 * 60;
    private static long MILLIS_IN_HOUR = MILLIS_IN_MINUTE * 60;
    private static long MILLIS_IN_DAY = MILLIS_IN_HOUR * 24;

    private static long mSequenceCounter = 1000;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yy h:mm a");
    // private static final SimpleDateFormat sdfToday = new SimpleDateFormat("h:mm a");
    private static final SimpleDateFormat wof = new SimpleDateFormat("yyMMdd");

    public static long getCurrentTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static String formatTime(long timeInMillis) {
        Date date = new Date(timeInMillis);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Calendar today = Calendar.getInstance();
        /*
        if (today.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH) &&
            today.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
            today.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
            return sdfToday.format(date);
        }
        */
        return sdf.format(date);
    }

    /**
     * hideSoftInput -- code used to hide input associated with any active view and also to make
     * sure the keypad does not come up right away for the current view (such as ListOfBooks)
     *
     * @param activity
     */
    public static void hideSoftInput(Activity activity) {
        // this is needed to make sure the keypad is hidden and does not come up automatically
        // I really don't know why Android makes managing the soft keypad so complicated...
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showAddressOnMap (Activity activity, String streetAddress) {
                Uri geoLocation = Uri.parse(
                "geo:0,0?q=" + streetAddress.replace (' ', '+'));
        Log.d(LOG_TAG, "navigateWorkOrder ==> " + geoLocation.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData (geoLocation);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
    }

    public static void setImageView (ImageView view, Context context, String imageFile, int imageDefault) {
        String imageURL = AppConfig.getInstance(context).getImageURL(imageFile);
        Log.d(LOG_TAG, "setImageView () imageURL ==> " + imageURL);

        // load contact us image using "Picasso" or with a default image if the URL is not valid
        if ((imageURL != null) && Patterns.WEB_URL.matcher(imageURL).matches()) {
            try {
                Picasso.with(context).load(imageURL).into(view);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Picasso call failed" + e.toString());
                view.setImageResource(imageDefault);
            }
        } else {
            view.setImageResource(imageDefault);
        }
    }

    public static void setTextView (TextView view, String text) {
        if (text == null) {
            view.setText ("");
        } else {
            view.setText(text);
        }
    }

    public static void setTextView (TextView view, Cursor data, String columnName) {
        setTextView(view, data.getString(data.getColumnIndex(columnName)));
    }

    public static void setTextViewFromHTML (TextView view, String html) {
        if (html == null) {
            view.setText ("");
        } else {
            view.setText(Html.fromHtml(html));
        }
    }

    public static void setTextViewFromHTML(TextView view, Cursor data, String columnName) {
        setTextViewFromHTML(view, data.getString(data.getColumnIndex(columnName)));
    }

    public static void updateActionBarTitle (Activity activity, String title) {
        if (activity != null) {
            ActionBar actionBar = ((AppCompatActivity)activity).getSupportActionBar();
            actionBar.setTitle(title);
            actionBar.setSubtitle(R.string.app_name);
        }
    }

    public static int getEventColor (int eventID) {
        int eventCategory = 10 * ((eventID % 100) / 10);
        int eventColor = R.color.colorEventDefault;
        switch (eventCategory) {
            case 10:
                eventColor = R.color.colorEvent10;
                break;
            case 20:
                eventColor = R.color.colorEvent20;
                break;
            case 30:
                eventColor = R.color.colorEvent30;
                break;
            case 40:
                eventColor = R.color.colorEvent40;
                break;
            case 50:
                eventColor = R.color.colorEvent50;
                break;
            case 60:
                eventColor = R.color.colorEvent60;
                break;
            case 70:
                eventColor = R.color.colorEvent70;
                break;
            default:
                break;
        }
        return eventColor;
    }

}
