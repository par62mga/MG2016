package com.pkrobertson.demo.mg2016;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.pkrobertson.demo.mg2016.data.AppConfig;
import com.pkrobertson.demo.mg2016.data.DatabaseContract;
import com.pkrobertson.demo.mg2016.data.DateTimeHelper;

import java.util.List;

/**
 * Created by Phil Robertson on 3/14/2016.
 */
public class EventPagerFragment extends Fragment {
    private static final String LOG_TAG = EventPagerFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "event_pager";

    public static final String DEFAULT = null;

    private static final String ARG_EVENT_URI = "event_uri";

    private static Context mContext;
    private static long    mSelectedItem = -1;

    private ViewPager mEventViewPager;
    private EventPagerAdapter mEventPagerAdapter;


    private Uri  mEventUri = null;

    // TODO: Rename and change types of parameters
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

        mContext = getActivity();
        mEventViewPager = (ViewPager)fragmentView.findViewById(R.id.event_pager);
        mEventPagerAdapter = new EventPagerAdapter(getActivity().getSupportFragmentManager());
        mEventViewPager.setAdapter(mEventPagerAdapter);
        if (mEventUri != null &&
                getActivity().getContentResolver().getType(mEventUri) ==
                        DatabaseContract.EventsEntry.CONTENT_ITEM_TYPE) {
            mSelectedItem = Long.parseLong (mEventUri.getLastPathSegment());
            int selectedPage = (int)(mSelectedItem / 100) - 1;
            Log.d (LOG_TAG, "onCreateView() URI Item ==> " + mSelectedItem + " Page ==> " + selectedPage);
            mEventViewPager.setCurrentItem(selectedPage, true);
        } else {
            mSelectedItem = -1;
        }
        return fragmentView;
    }

    @Override
    public void onResume () {
        super.onResume();

        Utility.updateActionBarTitle(getActivity(), getString(R.string.title_events));
    }

    public static class EventPagerAdapter extends FragmentPagerAdapter {
        private static final int MAX_ITEMS = 7;

        private long mStartDate;
        private long mNumberPages;

        //TODO: change starting page based on today's date
        //TODO: change day name to "TODAY" or "TOMORROW"

        public EventPagerAdapter (FragmentManager fragmentManager) {
            super(fragmentManager);

            AppConfig appConfig = AppConfig.getInstance(mContext);
            mStartDate = appConfig.getStartDate();
            mNumberPages = DateTimeHelper.getNumberDays(appConfig.getEndDate(), mStartDate) + 1;
            Log.d (LOG_TAG, "EventPagerAdapter() == numPages " + mNumberPages);
            if (mNumberPages > MAX_ITEMS) {
                mNumberPages = MAX_ITEMS;
            }
        }

        @Override
        public int getCount () {
            return (int)mNumberPages;
        }

        @Override
        public Fragment getItem (int position) {
            long thisDate = DateTimeHelper.addNumberDays(mStartDate, position);
            Log.d (LOG_TAG, "getItem position, startDate, thisDate ==> " +
                    position + "; " +
                    mStartDate + "; " +
                    thisDate + "; ");
            return EventListFragment.newInstance (
                    DatabaseContract.EventsEntry.buildEventsUriWithStartDate(
                            thisDate).toString(), mSelectedItem);
        }

        @Override
        public CharSequence getPageTitle (int position) {
            return DateTimeHelper.formatDate (
                    "EEE - dd MMMM",
                    DateTimeHelper.addNumberDays(mStartDate, position));
            /*switch (position) {
                case 0:  return "MON - 13th June";
                case 1:  return "TUE - 14th June";
                case 2:  return "WED - 15th June";
                default: return "THU - 16th June";
            }
            */
        }

    }
}
