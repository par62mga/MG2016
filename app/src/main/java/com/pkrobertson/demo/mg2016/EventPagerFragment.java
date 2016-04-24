package com.pkrobertson.demo.mg2016;

import android.app.Activity;
import android.content.Context;
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

    public static final String DEFAULT = "default";

    private static final String ARG_ACTION = "action";

    private static Context mContext;

    private ViewPager mEventViewPager;
    private EventPagerAdapter mEventPagerAdapter;


    private String mAction = null;

    // TODO: Rename and change types of parameters
    public static EventPagerFragment newInstance(String action) {
        EventPagerFragment fragment = new EventPagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACTION, action);
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
            mAction = getArguments().getString(ARG_ACTION);
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
                            thisDate).toString());
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
