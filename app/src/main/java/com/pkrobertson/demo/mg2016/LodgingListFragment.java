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
 * Created by Phil Robertson on 3/15/2016.
 */
public class LodgingListFragment extends Fragment {
    private static final String LOG_TAG = LodgingListFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "lodging_list";

    public static final String DEFAULT = "default";

    private static final String ARG_ACTION = "action";

    private LodgingListAdapter mLodgingListAdapter;
    private ListView mLodgingListView;
    private TextView mEmptyTextView;

    private List<LodgingItem> mLodgingItemList;

    private String mAction = null;

    // TODO: Rename and change types of parameters
    public static LodgingListFragment newInstance(String action) {
        LodgingListFragment fragment = new LodgingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACTION, action);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LodgingListFragment() {
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

        mLodgingItemList = LodgingItem.getLodgingItemList();

        Log.d(LOG_TAG, "onCreateView() number of items ==> " + String.valueOf(mLodgingItemList.size()));
        View fragmentView = inflater.inflate(R.layout.fragment_lodging_list, container, false);

        mEmptyTextView = (TextView) fragmentView.findViewById(R.id.empty_lodging_view);

        mLodgingListView = (ListView) fragmentView.findViewById(R.id.lodging_list_view);
        mLodgingListAdapter = new LodgingListAdapter(getActivity(), R.layout.event_list_item, mLodgingItemList);
        mLodgingListView.setAdapter(mLodgingListAdapter);
        mLodgingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //TODO: highlight / enable buttons, etc...
            }
        });

        return fragmentView;
    }

    @Override
    public void onResume () {
        super.onResume();

        Utility.updateActionBarTitle(getActivity(), getString(R.string.drawer_lodging));

        if (mLodgingListAdapter != null) {
            //mListAdapter.clear();
            mLodgingItemList = LodgingItem.getLodgingItemList();
            //mListAdapter.addAll (mItemList);
            mLodgingListAdapter.notifyDataSetChanged();
            mEmptyTextView.setVisibility(
                    (mLodgingItemList.size() > 0) ? View.INVISIBLE : View.VISIBLE);
        }
    }

}
