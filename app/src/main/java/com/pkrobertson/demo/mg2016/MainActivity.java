package com.pkrobertson.demo.mg2016;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.pkrobertson.demo.mg2016.data.AppConfig;
import com.pkrobertson.demo.mg2016.data.DateTimeHelper;
import com.pkrobertson.demo.mg2016.sync.AppSyncAdapter;

/**
 * MainActivity
 */
public class MainActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener,
                    OnFragmentInteraction {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private Toolbar      mToolbar;
    private DrawerLayout mDrawer;

    // the following fields manage the Export to Calendar menu item
    private MenuItem mMenuItemCalendar = null;
    private boolean  mCalendarEnabled  = false;
    private long     mCalendarDate;
    private long     mCalendarStartTime;
    private long     mCalendarEndTime;
    private String   mCalendarTitle       = null;
    private String   mCalendarDescription = null;
    private String   mCalendarLocation    = null;

    // the following fields manage the Call menu item
    private MenuItem mMenuItemCall     = null;
    private boolean  mCallEnabled      = false;
    private String   mPhoneNumber      = null;

    // the following fields manage the Locate menu item
    private MenuItem mMenuItemLocate   = null;
    private boolean  mLocateEnabled    = false;
    private String   mMapTitle         = null;
    private String   mMapSnippet       = null;
    private String   mMapLocation      = null;

    // the following fields manage the Open Website menu item
    private MenuItem mMenuItemWebsite  = null;
    private boolean  mWebsiteEnabled   = false;
    private String   mWebsiteAddress   = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // make sure sync adapter/content resolver are really when launched by Widget
        AppSyncAdapter.initializeSyncAdapter(this);

        // if no data found, try syncing again
        AppConfig appConfig = AppConfig.getInstance (this);
        if (appConfig == null) {
            AppSyncAdapter.syncImmediately(this);
        }

        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle((CharSequence) getString(R.string.drawer_title));
        setSupportActionBar(mToolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawer != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            mDrawer.setDrawerListener(toggle);
            toggle.syncState();

            // open drawer when the app is initially launched to show the feature...
            if (Utility.openDrawerOnLaunch(this)) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (mDrawer != null) {
            navigationView.inflateHeaderView(R.layout.nav_header_main);
        }

        // show news list or handle launch intent when there is no saved instance state
        if (savedInstanceState == null) {
            Fragment fragment;

            // see if we were launched by the widget
            if (getIntent() != null && getIntent().getData() != null) {
                Uri launchUri = getIntent().getData();
                Log.d(LOG_TAG, "onCreate() launchUri ==> " + launchUri.toString());
                fragment = EventPagerFragment.newInstance (launchUri.toString());
            } else {
                fragment = NewsListFragment.newInstance(NewsListFragment.DEFAULT);
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.container, fragment).commit();
        }
    }

    @Override
    public void onResume () {
        Log.d (LOG_TAG, "onResume()...");
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if ( (mDrawer != null) && (mDrawer.isDrawerOpen(GravityCompat.START)) ) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d (LOG_TAG, "onCreateOptionsMenu()...");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        //mMenuItemSettings = (MenuItem) menu.findItem(R.id.action_settings);
        mMenuItemCalendar = menu.findItem(R.id.action_calendar);
        mMenuItemCall     = menu.findItem(R.id.action_call);
        mMenuItemLocate   = menu.findItem(R.id.action_locate);
        mMenuItemWebsite  = menu.findItem(R.id.action_website);

        // show menu options only when enabled by the active fragment
        mMenuItemCalendar.setVisible (mCalendarEnabled);
        mMenuItemCall.setVisible (mCallEnabled);
        mMenuItemLocate.setVisible (mLocateEnabled);
        mMenuItemWebsite.setVisible(mWebsiteEnabled);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // action bar home/up should open or close the drawer
            if (mDrawer != null) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        } else if (id == R.id.action_calendar) {
            //TODO: Need to see if location can be improved to go directly to location
            //      We can either use a separate calendarfor calendar or rework map location to be location string that works both for
            //      google maps and calendar
            AppConfig appConfig = AppConfig.getInstance(this);
            long tzAdjustment = DateTimeHelper.getTimeZoneAdjustment(appConfig.getTzOffset());
            long startTime = DateTimeHelper.getDateTimeInMillis(mCalendarDate, mCalendarStartTime);
            long endTime = startTime;
            if (mCalendarEndTime >= 0) {
                endTime = DateTimeHelper.getDateTimeInMillis(mCalendarDate, mCalendarEndTime);
            }
            if (tzAdjustment != 0) {
                Toast.makeText(this, appConfig.getEventAdjustText(), Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime - tzAdjustment)
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime - tzAdjustment)
                    .putExtra(CalendarContract.Events.TITLE,
                            String.format (getString(R.string.format_event_calendar), mCalendarTitle))
                    .putExtra(CalendarContract.Events.DESCRIPTION, mCalendarDescription)
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, mCalendarLocation)
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_call) {
            Uri dialUri = Uri.parse("tel:" + mPhoneNumber);
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData (dialUri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            return true;
        } else if (id == R.id.action_locate) {
            MapActivity.launchMapActivity(
                    this, mMapTitle, mMapSnippet, mMapLocation);
            return true;
        } else if (id == R.id.action_website) {
            Uri websiteUri = Uri.parse(mWebsiteAddress);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData (websiteUri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment newFragment = null;

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_news) {
            newFragment = NewsListFragment.newInstance(NewsListFragment.DEFAULT);
        } else if (id == R.id.nav_events) {
            // see if we have data from the server...
            AppConfig appConfig = AppConfig.getInstance(this);
            if (appConfig != null) {
                newFragment = EventPagerFragment.newInstance(EventPagerFragment.DEFAULT);
            } else {
                Toast.makeText(this, getString(R.string.error_empty_events), Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (id == R.id.nav_lodging) {
            newFragment = LodgingListFragment.newInstance(LodgingListFragment.DEFAULT);
        } else if (id == R.id.nav_contact_us) {
            newFragment = ContactUsFragment.newInstance(ContactUsFragment.DEFAULT);
        }

        if (newFragment != null) {
            disableMenuItems();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, newFragment).commit();
        }

        if (mDrawer != null) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    /**
     * onNewsListInteraction -- handle news list item selection by launching detail fragment
     * @param newsUri
     */
    @Override
    public void onNewsListInteraction(String newsUri) {
        NewsDetailFragment fragment = NewsDetailFragment.newInstance(newsUri);
        FragmentManager fm = getSupportFragmentManager();

        // see if this is a tablet (fixed menu) or phone to determine how detail is shown
        if (mDrawer == null) {
            fragment.show (fm, NewsDetailFragment.FRAGMENT_TAG);
        } else {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container, fragment, NewsDetailFragment.FRAGMENT_TAG);
            ft.addToBackStack(NewsDetailFragment.FRAGMENT_TAG);
            ft.commit();
        }
    }

    /**
     * disableMenuItems -- list item was deselected, turn off menu options to call, locate, etc.
     */
    @Override
    public void disableMenuItems () {
        Log.d (LOG_TAG, "disableMenuItems()...");
        mCalendarEnabled = false;
        mCallEnabled = false;
        mLocateEnabled = false;
        mWebsiteEnabled = false;

        if (mMenuItemCalendar != null) {
            mMenuItemCalendar.setVisible(false);
            mMenuItemCall.setVisible(false);
            mMenuItemLocate.setVisible(false);
            mMenuItemWebsite.setVisible(false);
        }
    }

    /**
     * showMenuItems -- this ugly bit of code is needed to make sure menu items reliably show
     *     up on the action bar...yuck!
     */
    @Override
    public void showMenuItems () {
        Log.d (LOG_TAG, "showMenuItems()...");
        invalidateOptionsMenu ();
    }

    /**
     * enableMenuItemCall -- item selected that supports the call menu action
     * @param phoneNumber
     */
    @Override
    public void enableMenuItemCall (String phoneNumber) {
        mPhoneNumber = phoneNumber;
        mCallEnabled = true;
        if (mMenuItemCall != null) {
            mMenuItemCall.setVisible(true);
        }
    }

    /**
     * enableMenuItemLocate -- item selected that supports the locate menu action
     * @param locationName
     * @param locationAddress
     * @param mapLocation
     */
    @Override
    public void enableMenuItemLocate (
            String locationName, String locationAddress, String mapLocation) {
        mMapTitle    = locationName;
        mMapSnippet  = locationAddress;
        mMapLocation = mapLocation;
        mLocateEnabled = true;
        if (mMenuItemLocate != null) {
            mMenuItemLocate.setVisible(true);
        }

    }

    /**
     * enableMenuItemWebsite -- item selected that supports the website menu action
     * @param websiteURL
     */
    @Override
    public void enableMenuItemWebsite (String websiteURL) {
        mWebsiteAddress = websiteURL;
        mWebsiteEnabled = true;
        if (mMenuItemWebsite != null) {
            mMenuItemWebsite.setVisible(true);
        }
    }

    /**
     * enableMenuItemCalendar -- item selected that supports the calendar menu action
     * @param startDate
     * @param startTime
     * @param endTime
     * @param title
     * @param description
     * @param location
     */
    @Override
    public void enableMenuItemCalendar (
            long startDate, long startTime, long endTime,
            String title, String description, String location) {
        mCalendarDate        = startDate;
        mCalendarStartTime   = startTime;
        mCalendarEndTime     = endTime;
        mCalendarTitle       = title;
        mCalendarDescription = description;
        mCalendarLocation    = location;
        mCalendarEnabled     = true;
        if (mMenuItemCalendar != null) {
            mMenuItemCalendar.setVisible(true);
        }
    }

}
