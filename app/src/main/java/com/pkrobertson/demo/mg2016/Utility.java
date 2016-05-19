package com.pkrobertson.demo.mg2016;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;

import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkrobertson.demo.mg2016.data.AppConfig;
import com.pkrobertson.demo.mg2016.sync.AppSyncAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Utility -- shared helper methods
 */
public class Utility {
    private static String LOG_TAG = Utility.class.getSimpleName();


    /**
     * getServerStatus -- returns server status stored by the sync adapter
     * @param c Context used to get the SharedPreferences
     * @return the location status integer type
     */
    @SuppressWarnings("ResourceType")
    public static @AppSyncAdapter.AppServerStatus
    int getServerStatus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_server_status_key), AppSyncAdapter.SERVER_STATUS_UNKNOWN);
    }

    /**
     * getServerStatusMessage -- translate server status into a message user can understand
     * @param c
     * @return message
     */
    public static int getServerStatusMessage (Context c) {
        ConnectivityManager cm =
                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        int serverMessage = R.string.error_empty_list;
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            int serverStatus = getServerStatus(c);
            switch (serverStatus) {
                case AppSyncAdapter.SERVER_STATUS_DOWN:
                    serverMessage = R.string.error_server_down;
                    break;
                case AppSyncAdapter.SERVER_STATUS_INVALID:
                    serverMessage = R.string.error_server_error;
                    break;
                default:
                    break;
            }
        } else {
            serverMessage = R.string.error_no_network;
        }
        return serverMessage;
    }

    /**
     * openDrawerOnLaunch -- check preferences and open drawer if this is the first launch
     * @param c Context used to get the SharedPreferences
     * @return the location status integer type
     */
    public static boolean openDrawerOnLaunch (Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        boolean openDrawer = sp.getBoolean(c.getString(R.string.pref_open_drawer), true);
        if (openDrawer) {
            SharedPreferences.Editor spe = sp.edit();
            spe.putBoolean(c.getString(R.string.pref_open_drawer), false);
            spe.commit();
        }
        return openDrawer;
    }

    /**
     * hasPhoneAvailability -- return true if phone service is available
     * @param c -- context
     * @return true when phone is available
     */
    public static boolean hasPhoneAvailability (Context c) {
        TelephonyManager tm = (TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null || (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE)) {
            return false;
        }
        return true;
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

    /**
     * showAddressOnMap -- original way map was launched before Google Maps API was implemented
     * @param activity
     * @param streetAddress
     */
    public void showAddressOnMap (Activity activity, String streetAddress) {
                Uri geoLocation = Uri.parse(
                        "geo:0,0?q=" + streetAddress.replace(' ', '+'));
        Log.d(LOG_TAG, "navigateWorkOrder ==> " + geoLocation.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
    }

    /**
     * setImageView -- method used to load image
     * @param view
     * @param context
     * @param imageFile
     * @param imageThumbnail - when non-null, thumbnail is loaded first for faster response
     * @param imageDefault
     * @param contentDescription
     */
    public static void setImageView (final ImageView view, final Context context,
                                     String imageFile, String imageThumbnail,
                                     int imageDefault, CharSequence contentDescription) {
        final String imageURL = AppConfig.getInstance(context).getImageURL(imageFile);
        Log.d(LOG_TAG, "setImageView () imageURL ==> " + imageURL);

        // load contact us image using "Picasso" or with a default image if the URL is not valid
        if ((imageURL != null) && Patterns.WEB_URL.matcher(imageURL).matches()) {
            try {
                String thumbnailURL = AppConfig.getInstance(context).getImageURL(imageThumbnail);
                Log.d(LOG_TAG, "setImageView () thumbnailURL ==> " + thumbnailURL);
                if ((thumbnailURL != null) && Patterns.WEB_URL.matcher(thumbnailURL).matches()) {

                    Picasso.with(context)
                            .load(thumbnailURL)
                            .placeholder(imageDefault)
                            .fit()
                             //.transform(blurTransformation)
                            .into(view, new Callback() {
                        @Override
                        public void onSuccess() {
                            Picasso.with(context)
                                    .load(imageURL) // image url goes here
                                    .fit()
                                    .placeholder(view.getDrawable())
                                    .into(view);
                        }

                        @Override
                        public void onError() {
                        }
                    });

                } else {
                    Picasso.with(context)
                            .load(imageURL)
                            .placeholder(imageDefault)
                            .fit()
                            .into(view);
                }
            } catch (Exception e) {
                Log.d(LOG_TAG, "Picasso call failed" + e.toString());
                view.setImageResource(imageDefault);
            }
        } else {
            view.setImageResource(imageDefault);
        }
        view.setContentDescription (contentDescription);
    }

    /**
     * setTextView -- helper method to update text view, handling null text gracefully
     * @param view
     * @param text
     */
    public static void setTextView (TextView view, String text) {
        if (text == null) {
            view.setText ("");
        } else {
            view.setText(text);
        }
    }


    /**
     * setTextView -- helper method to update text view from cursor column name and allows
     *     null string from database
     * @param view
     * @param data
     * @param columnName
     */
    public static void setTextView (TextView view, Cursor data, String columnName) {
        setTextView(view, data.getString(data.getColumnIndex(columnName)));
    }

    /**
     * setTextViewOrHide -- helper method to update text view from cursor column name. If database
     *     field is null, the view is hidden/GONE
     * @param view
     * @param data
     * @param columnName
     */
    public static void setTextViewOrHide (TextView view, Cursor data, String columnName) {
        String textViewData = data.getString(data.getColumnIndex(columnName));
        if (textViewData == null) {
            view.setVisibility(View.GONE);
        } else {
            view.setText(textViewData);
        }
    }

    /**
     * setTextViewFromHTML -- sets text view from HTML source, allows for relatively nice formatting
     * @param view
     * @param html
     */
    public static void setTextViewFromHTML (TextView view, String html) {
        if (html == null) {
            view.setText ("");
        } else {
            view.setText(Html.fromHtml(html));
        }
    }

    /**
     *  setTextViewFromHTML -- sets text view from HTML source in a cursor column
     * @param view
     * @param data
     * @param columnName
     */
    public static void setTextViewFromHTML(TextView view, Cursor data, String columnName) {
        setTextViewFromHTML(view, data.getString(data.getColumnIndex(columnName)));
    }

    /**
     * updateActionBarTitle -- as the name implies...
     * @param activity
     * @param title
     */
    public static void updateActionBarTitle (Activity activity, String title) {
        if (activity != null) {
            ActionBar actionBar = ((AppCompatActivity)activity).getSupportActionBar();
            actionBar.setTitle(title);
            actionBar.setSubtitle(R.string.app_name);
        }
    }

    /**
     * getEventColor -- assigns color resource ID based on MG 2016 event category (see appData.json)
     * @param eventID
     * @return color resource ID
     */
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

    /**
     * is24HourFormat -- checks device/locale settings to see if time should be 24-hour format
     * @param context
     * @return
     */
    public static boolean is24HourFormat (Context context) {
        return DateFormat.is24HourFormat(context);
    }
}
