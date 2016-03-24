package com.pkrobertson.demo.mg2016;

import android.app.Activity;
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

import java.util.List;

/**
 * Created by Phil Robertson on 3/14/2016.
 */
public class EventPagerFragment extends Fragment {
    private static final String LOG_TAG = EventPagerFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "event_pager";

    public static final String DEFAULT = "default";

    private static final String ARG_ACTION = "action";

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

        mEventViewPager = (ViewPager) fragmentView.findViewById(R.id.event_pager);
        mEventPagerAdapter = new EventPagerAdapter(getActivity().getSupportFragmentManager());
        mEventViewPager.setAdapter(mEventPagerAdapter);

        return fragmentView;
    }

    @Override
    public void onResume () {
        super.onResume();

        Utility.updateActionBarTitle(getActivity(), getString(R.string.drawer_events));
    }

    public static class EventPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 4;

        public EventPagerAdapter (FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount () {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem (int position) {
            return EventListFragment.newInstance (EventListFragment.DEFAULT);
        }

        @Override
        public CharSequence getPageTitle (int position) {
            switch (position) {
                case 0:  return "MON - 13th June";
                case 1:  return "TUE - 14th June";
                case 2:  return "WED - 15th June";
                default: return "THU - 16th June";
            }
        }

    }
}
