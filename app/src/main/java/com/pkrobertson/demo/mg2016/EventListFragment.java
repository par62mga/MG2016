package com.pkrobertson.demo.mg2016;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.pkrobertson.demo.mg2016.data.DatabaseContract;

import java.util.List;

/**
 * Created by Phil Robertson on 3/14/2016.
 */
public class EventListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {
    private static final String LOG_TAG = EventListFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "event_list";

    private static final String ARG_URISTRING = "uri";
    private static final String ARG_ITEM = "item";

    private static final int EVENTS_LOADER = 300;

    private EventListAdapter mEventListAdapter;
    private RecyclerView     mEventRecyclerView;
    private TextView         mEmptyTextView;

    private View mFragmentView;
    private Uri  mEventsUri = null;
    private long mEventItem = -1;

    private boolean mLoaderInitialized = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param eventsUri provides news item to view.
     * @return A new instance of fragment EventListFragment.
     */
    public static EventListFragment newInstance(String eventsUri, long selectedItem) {
        EventListFragment fragment = new EventListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URISTRING, eventsUri);
        args.putLong (ARG_ITEM, selectedItem);
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
            mEventsUri = Uri.parse(getArguments().getString(ARG_URISTRING));
            mEventItem = getArguments().getLong(ARG_ITEM);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mFragmentView = inflater.inflate(R.layout.fragment_event_list, container, false);
        if (mEventsUri != null) {
            getLoaderManager().restartLoader(EVENTS_LOADER, null, this);
        }

        mEmptyTextView = (TextView)mFragmentView.findViewById(R.id.empty_events_view);

        // set up the recycler view
        mEventRecyclerView = (RecyclerView) mFragmentView.findViewById(R.id.events_recycler_view);
        mEventRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEventListAdapter = new EventListAdapter(getActivity(), mEmptyTextView, mEventItem);
        mEventRecyclerView.setAdapter(mEventListAdapter);
        if (savedInstanceState != null) {
            mEventListAdapter.onRestoreInstanceState(savedInstanceState);
        }
        return mFragmentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(LOG_TAG, "onCreateOptionsMenu()");
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.events, menu);
        mEventListAdapter.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mEventListAdapter.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState()");
        // When tablets rotate, the currently selected list item needs to be saved
        //TODO: fix crash on back-to-back rotation
        mEventListAdapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume () {
        super.onResume();

        Log.d(LOG_TAG, "onResume()");
        if (! mLoaderInitialized) {
            getLoaderManager().initLoader(EVENTS_LOADER, null, this);
            mLoaderInitialized = true;
        }
        Utility.updateActionBarTitle(getActivity(), getString(R.string.title_events));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(LOG_TAG, "onCreateLoader()");

        // Sort order:  record ID ascending
        String sortOrder = DatabaseContract.EventsEntry._ID + " ASC";

        return new CursorLoader(getActivity(),
                mEventsUri,
                null, // get all columns
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG, "onLoadFinished()");
        mEventListAdapter.swapCursor(data);
        updateEmptyView();
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "onLoaderReset()");
        mEventListAdapter.swapCursor(null);
    }

    /*
        Updates the empty list view with contextually relevant information that the user can
        use to determine why they aren't seeing weather.
     */
    private void updateEmptyView() {
        if ( mEventListAdapter.getItemCount() == 0 ) {
            // int message = Utility.getServerStatusMessage (R.string.empty_lodging_list);
            mEmptyTextView.setText(R.string.error_empty_list);
        }
    }

}
