package com.pkrobertson.demo.mg2016;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class EventListFragment extends Fragment {
    private static final String LOG_TAG = EventListFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "event_list";

    public static final String DEFAULT = "default";

    private static final String ARG_ACTION = "action";

    private OnFragmentInteractionListener mListener;

    private EventListAdapter mEventListAdapter;
    private ListView mEventListView;
    private TextView mEmptyTextView;

    private List<EventItem> mEventItemList;

    private String mAction = null;

    // TODO: Rename and change types of parameters
    public static EventListFragment newInstance(String action) {
        EventListFragment fragment = new EventListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACTION, action);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventListFragment() {
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

        mEventItemList = EventItem.getEventItemList();

        Log.d(LOG_TAG, "onCreateView() number of items ==> " + String.valueOf(mEventItemList.size()));
        View fragmentView = inflater.inflate(R.layout.fragment_event_list, container, false);

        mEmptyTextView = (TextView) fragmentView.findViewById(R.id.empty_events_view);

        mEventListView = (ListView) fragmentView.findViewById(R.id.events_list_view);
        mEventListAdapter = new EventListAdapter(getActivity(), R.layout.event_list_item, mEventItemList);
        mEventListView.setAdapter(mEventListAdapter);
        mEventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mListener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    EventItem item = mEventItemList.get(position);
                    mListener.onFragmentInteraction(item);
                }
            }
        });

        return fragmentView;
    }

    @Override
    public void onResume () {
        super.onResume();

        Utility.updateActionBarTitle(getActivity(), getString(R.string.drawer_events));

        if (mEventListAdapter != null) {
            //mNewsListAdapter.clear();
            mEventItemList = EventItem.getEventItemList();
            //mEventListAdapter.addAll (mEventItemList);
            mEventListAdapter.notifyDataSetChanged();
            mEmptyTextView.setVisibility(
                    (mEventItemList.size() > 0) ? View.INVISIBLE : View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(EventItem selectedItem);
    }

}
