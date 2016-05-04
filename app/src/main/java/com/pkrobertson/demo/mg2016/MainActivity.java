package com.pkrobertson.demo.mg2016;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener,
                    NewsListAdapter.OnNewsListInteraction {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private Toolbar      mToolbar;
    private DrawerLayout mDrawer;

    private MenuItem mMenuItemSettings = null;
    
    private boolean  mShowingDetail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (mDrawer != null) {
            navigationView.inflateHeaderView(R.layout.nav_header_main);
        }

        // show news list
        if (savedInstanceState == null) {
            Fragment fragment;
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
    public void onBackPressed() {
        if ( (mDrawer != null) && (mDrawer.isDrawerOpen(GravityCompat.START)) ) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mMenuItemSettings = (MenuItem) menu.findItem(R.id.action_settings);
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
        } else if (id == R.id.action_settings) {
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
            newFragment = EventPagerFragment.newInstance(EventPagerFragment.DEFAULT);
        } else if (id == R.id.nav_lodging) {
            newFragment = LodgingListFragment.newInstance(LodgingListFragment.DEFAULT);
        } else if (id == R.id.nav_contact_us) {
            newFragment = ContactUsFragment.newInstance(ContactUsFragment.DEFAULT);
        }

        if (newFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, newFragment).commit();
        }

        if (mDrawer != null) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onNewsListInteraction(String newsUri) {
        NewsDetailFragment fragment = NewsDetailFragment.newInstance(newsUri);
        FragmentManager fm = getSupportFragmentManager();
        if (mDrawer == null) {
            fragment.show (fm, NewsDetailFragment.FRAGMENT_TAG);
        } else {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container, fragment, NewsDetailFragment.FRAGMENT_TAG);
            ft.addToBackStack(NewsDetailFragment.FRAGMENT_TAG);
            ft.commit();
        }
    }

}
