package com.pkrobertson.demo.mg2016;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pkrobertson.demo.mg2016.data.AppConfig;
import com.pkrobertson.demo.mg2016.data.DatabaseContract;
import com.pkrobertson.demo.mg2016.data.DateTimeHelper;

import java.util.List;

/**
 * EventPagerFragment -- handles the Event Diary and individual EventListFragments
 */
public class EventPagerFragment extends Fragment {
    private static final String LOG_TAG = EventPagerFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "event_pager";

    public static final String DEFAULT = null;

    private static final String ARG_EVENT_URI = "event_uri";

    private static final int MAX_ITEMS    = 7;
    private static final int NO_SELECTION = -1;

    private static Context mContext;

    // identifies the selected page launched by the widget
    private static int     mSelectedPage = NO_SELECTION;
    private static long    mSelectedItem = NO_SELECTION;

    private ViewPager mEventViewPager;
    private EventPagerAdapter mEventPagerAdapter;


    private Uri  mEventUri = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param eventUri provides a specific event item to view.
     * @return A new instance of fragment EventListFragment.
     */
    public static EventPagerFragment newInstance(String eventUri) {
        EventPagerFragment fragment = new EventPagerFragment();
        Bundle args = new Bundle();
        if (eventUri != null) {
            args.putString(ARG_EVENT_URI, eventUri);
        }
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventPagerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String uriString = getArguments().getString(ARG_EVENT_URI);
            if (uriString != null) {
                mEventUri = Uri.parse(uriString);
            }
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View fragmentView = inflater.inflate(R.layout.fragment_event_pager, container, false);

        // figure out how many pages will be needed from number of event days
        AppConfig appConfig = AppConfig.getInstance(mContext);
        long startDate = appConfig.getStartDate();
        long numberPages = DateTimeHelper.getNumberDays(appConfig.getEndDate(), startDate) + 1;
        Log.d (LOG_TAG, "onCreateView() == numPages " + numberPages);
        if (numberPages > MAX_ITEMS) {
            numberPages = MAX_ITEMS;
        }

        mContext = getActivity();
        mEventViewPager = (ViewPager)fragmentView.findViewById(R.id.event_pager);
        mEventPagerAdapter = new EventPagerAdapter(
                getActivity().getSupportFragmentManager(), startDate, numberPages);
        mEventViewPager.setAdapter(mEventPagerAdapter);
        mEventViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d (LOG_TAG, "onPageSelected() position ==> " + position);
                mEventPagerAdapter.newPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // if Uri present it holds an event ID that encodes the day (page)
        if (mEventUri != null &&
                getActivity().getContentResolver().getType(mEventUri).contentEquals(
                        DatabaseContract.EventsEntry.CONTENT_ITEM_TYPE)) {
            mSelectedItem = Long.parseLong (mEventUri.getLastPathSegment());
            mSelectedPage = (int)(mSelectedItem / 100) - 1;
            Log.d (LOG_TAG, "onCreateView() URI Item ==> " + mSelectedItem + " Page ==> " + mSelectedPage);
            mEventViewPager.setCurrentItem(mSelectedPage, true);
            mEventPagerAdapter.newPageSelected (mSelectedPage);
        } else {
            mSelectedPage = NO_SELECTION;
            mSelectedItem = NO_SELECTION;

            // if today is >= first day in the event diary, go to that or the last page
            long today = DateTimeHelper.getCurrentDate(appConfig.getTzOffset());
            int days   = DateTimeHelper.getNumberDays(today, startDate);
            if (days >= 0) {
                if (days >= numberPages) {
                    days = (int)numberPages - 1;
                }
                mEventViewPager.setCurrentItem(days, true);
            }
        }

        return fragmentView;
    }

    @Override
    public void onResume () {
        super.onResume();

        Utility.updateActionBarTitle(getActivity(), getString(R.string.title_events));
    }

    @Override
    public void onPause () {
        super.onPause();
    }

    public static class EventPagerAdapter extends FragmentStatePagerAdapter {


        private long mStartDate;
        private long mNumberPages;
        private int  mCurrentPage;

        private EventListFragment mEventFragments[];

        public EventPagerAdapter (FragmentManager fragmentManager, long startDate, long numberPages) {
            super(fragmentManager);

            mStartDate      = startDate;
            mNumberPages    = numberPages;
            mCurrentPage    = 0;
            mEventFragments = new EventListFragment[(int)numberPages];
        }

        @Override
        public int getCount () {
            return (int)mNumberPages;
        }

        @Override
        public Object instantiateItem (ViewGroup container, int position) {
            Fragment createdFragment = (Fragment)super.instantiateItem (container, position);

            // save reference to what might be a recycled fragment
            // TODO: may need way to reload events if needed
            mEventFragments[position] = (EventListFragment)createdFragment;
            return createdFragment;
        }

        @Override
        public Fragment getItem (int position) {
            long thisDate = DateTimeHelper.addNumberDays(mStartDate, position);
            Log.d (LOG_TAG, "getItem position, startDate, thisDate ==> " +
                    position + "; " +
                    mStartDate + "; " +
                    thisDate + "; ");
            mEventFragments[position] = EventListFragment.newInstance (
                    DatabaseContract.EventsEntry.buildEventsUriWithStartDate(thisDate).toString(),
                    position == mSelectedPage ? mSelectedItem : NO_SELECTION);
            return mEventFragments[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return DateTimeHelper.formatDate (
                    mContext.getString(R.string.format_sdf_event_day),
                    DateTimeHelper.addNumberDays(mStartDate, position));
        }

        public void newPageSelected (int page) {
            if (mEventFragments[mCurrentPage] != null) {
                mEventFragments[mCurrentPage].onPageNotVisible();
            }
            mCurrentPage = page;
        }

    }
}
