package com.pkrobertson.demo.mg2016;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pkrobertson.demo.mg2016.data.DatabaseContract;

/**
 * LodgingListFragment -- handles the lodging list page
 */
public class LodgingListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = LodgingListFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "lodging_list";
    public static final String DEFAULT = "default";

    private static final String ARG_ACTION = "action";

    private static final int LODGING_LOADER = 100;

    private RecyclerView        mLodgingRecyclerView;
    private LodgingListAdapter  mLodgingListAdapter;
    private TextView            mEmptyTextView;

    private String mAction = null;

    private boolean mLoaderInitialized = false;
    private int     mItemToShow = LodgingListAdapter.NO_SELECTION;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param action placeholder until a specific action is needed.
     * @return A new instance of fragment LodgingListFragment.
     */
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

        Log.d(LOG_TAG, "onCreateView()");
        View fragmentView = inflater.inflate(R.layout.fragment_lodging_list, container, false);
        mEmptyTextView = (TextView)fragmentView.findViewById(R.id.empty_lodging_view);

        // set up the recycler view
        mLodgingRecyclerView  = (RecyclerView) fragmentView.findViewById(R.id.lodging_recycler_view);
        LinearLayoutManager lodgingLayoutManager = new LinearLayoutManager(getActivity());
        mLodgingRecyclerView.setLayoutManager(lodgingLayoutManager);
        mLodgingListAdapter = new LodgingListAdapter(
                getActivity(), mLodgingRecyclerView, mEmptyTextView);
        mLodgingRecyclerView.setAdapter(mLodgingListAdapter);

        // do we need to restore the selected item?
        if (savedInstanceState != null) {
            mItemToShow = mLodgingListAdapter.onRestoreInstanceState(savedInstanceState);
        } else {
            mItemToShow = LodgingListAdapter.NO_SELECTION;
        }
        return fragmentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState()");
        // When tablets rotate, the currently selected list item needs to be saved
        mLodgingListAdapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume () {
        super.onResume();
        Log.d(LOG_TAG, "onResume()");
        if (! mLoaderInitialized) {
            getLoaderManager().initLoader(LODGING_LOADER, null, this);
            mLoaderInitialized = true;
        } else if (mItemToShow != LodgingListAdapter.NO_SELECTION) {
            mLodgingRecyclerView.smoothScrollToPosition(mItemToShow);
            mItemToShow = LodgingListAdapter.NO_SELECTION;
        }
        Utility.updateActionBarTitle(getActivity(), getString(R.string.title_lodging));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(LOG_TAG, "onCreateLoader()");
        // Sort order:  record ID ascending
        String sortOrder = DatabaseContract.LodgingEntry._ID + " ASC";

        return new CursorLoader(getActivity(),
        DatabaseContract.LodgingEntry.CONTENT_URI,
        null, // get all columns
        null,
        null,
        sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG, "onLoadFinished()");
        mLodgingListAdapter.swapCursor(data);
        updateEmptyView();

        if (mItemToShow != LodgingListAdapter.NO_SELECTION) {
            mLodgingRecyclerView.smoothScrollToPosition(mItemToShow);
            mItemToShow = LodgingListAdapter.NO_SELECTION;
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "onLoaderReset()");
        mLodgingListAdapter.swapCursor(null);
    }

    /**
     * updateEmptyView -- updates text message based on what was stored by the sync adapter
     */
    private void updateEmptyView() {
        if ( mLodgingListAdapter.getItemCount() == 0 ) {
            mEmptyTextView.setText(Utility.getServerStatusMessage(getActivity()));
        }
    }

}
