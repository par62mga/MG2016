package com.pkrobertson.demo.mg2016;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pkrobertson.demo.mg2016.data.DatabaseContract;

import java.util.List;

/**
 * EventListFragment -- handles the event list page that holds one day in the Event Diary managed
 *     by the EventPagerFragment
 */
public class EventListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {
    private static final String LOG_TAG = EventListFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "event_list";

    private static final String ARG_URISTRING = "uri";
    private static final String ARG_ITEM = "item";

    private static final int EVENTS_LOADER = 300;

    private RecyclerView     mEventRecyclerView;
    private EventListAdapter mEventListAdapter;
    private TextView         mEmptyTextView;

    private Uri  mEventsUri = null;
    private long mEventItem = EventListAdapter.NO_SELECTION;

    private boolean mLoaderInitialized = false;
    private int     mItemToShow = EventListAdapter.NO_SELECTION;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param eventsUri provides a specific event item to view.
     * @return A new instance of fragment EventListFragment.
     */
    public static EventListFragment newInstance(String eventsUri, long selectedItem) {
        EventListFragment fragment = new EventListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URISTRING, eventsUri);
        args.putLong(ARG_ITEM, selectedItem);
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
        } else {
            mEventItem = EventListAdapter.NO_SELECTION;
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View fragmentView = inflater.inflate(R.layout.fragment_event_list, container, false);
        if (mEventsUri != null) {
            getLoaderManager().restartLoader(EVENTS_LOADER, null, this);
        }
        mEmptyTextView = (TextView)fragmentView.findViewById(R.id.empty_events_view);

        // set up the recycler view
        mEventRecyclerView = (RecyclerView)fragmentView.findViewById(R.id.events_recycler_view);
        LinearLayoutManager eventLayoutManager = new LinearLayoutManager(getActivity());
        mEventRecyclerView.setLayoutManager(eventLayoutManager);
        mEventListAdapter = new EventListAdapter(
                getActivity(), mEventRecyclerView, mEmptyTextView, mEventItem);
        mEventRecyclerView.setAdapter(mEventListAdapter);

        // see if we need to restore the selected item
        if (savedInstanceState != null) {
            mItemToShow = mEventListAdapter.onRestoreInstanceState(savedInstanceState);
        } else {
            mItemToShow = LodgingListAdapter.NO_SELECTION;
        }
        return fragmentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState()");
        // When tablets rotate, the currently selected list item needs to be saved
        mEventListAdapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause () {
        super.onPause();
        Log.d(LOG_TAG, "onPause()");
    }

    @Override
    public void onResume () {
        super.onResume();

        Log.d(LOG_TAG, "onResume()");
        if (! mLoaderInitialized) {
            getLoaderManager().initLoader(EVENTS_LOADER, null, this);
            mLoaderInitialized = true;
        } else {
            if (mItemToShow == EventListAdapter.NO_SELECTION) {
                mItemToShow = findWidgetItem();
            }
            if (mItemToShow != EventListAdapter.NO_SELECTION) {
                mEventRecyclerView.smoothScrollToPosition(mItemToShow);
                mItemToShow = EventListAdapter.NO_SELECTION;
            }
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

        if (mItemToShow == EventListAdapter.NO_SELECTION) {
            mItemToShow = findWidgetItem();
        }
        if (mItemToShow != EventListAdapter.NO_SELECTION) {
            mEventRecyclerView.smoothScrollToPosition(mItemToShow);
            mItemToShow = EventListAdapter.NO_SELECTION;
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "onLoaderReset()");
        mEventListAdapter.swapCursor(null);
    }

    public void onPageNotVisible () {
        mEventListAdapter.onPageNotVisible();
    }

    /**
     * updateEmptyView -- updates text message based on what was stored by the sync adapter
     */
    private void updateEmptyView() {
        if ( mEventListAdapter.getItemCount() == 0 ) {
            mEmptyTextView.setText(Utility.getServerStatusMessage(getActivity()));
        }
    }

    /**
     * findWidgetItem -- search active cursor to find position matching the selected item
     * @return
     */
    private int findWidgetItem () {
        Cursor cursor = mEventListAdapter.getCursor();
        if ((cursor == null) || (mEventItem == EventListAdapter.NO_SELECTION)) {
            return EventListAdapter.NO_SELECTION;
        }

        // find item matching the ID of the one selected by the widget
        int item = 0;
        int indexID = cursor.getColumnIndex(DatabaseContract.EventsEntry._ID);
        int startPosition = cursor.getPosition();
        while (item < cursor.getCount()) {
            cursor.moveToPosition (item);
            int eventItem = cursor.getInt(indexID);
            if (eventItem == mEventItem) {
                Log.d (LOG_TAG, "findWidgetItem() found ID ==> " + mEventItem);
                cursor.moveToPosition(startPosition);
                return (item);
            }
            item++;
        }

        // not found, should not happen...
        Log.e (LOG_TAG, "findWidgetItem() did not find ID ==> " + mEventItem);
        cursor.moveToPosition(startPosition);
        return EventListAdapter.NO_SELECTION;
    }
}
