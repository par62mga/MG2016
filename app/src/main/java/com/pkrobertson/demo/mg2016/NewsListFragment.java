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
 * NewsListFragment -- handles the news list page
 */
public class NewsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = NewsListFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "news_list";
    public static final String DEFAULT = "default";

    private static final String ARG_ACTION = "action";

    private static final int NEWS_LOADER = 200;

    private NewsListAdapter mNewsListAdapter;
    private RecyclerView    mNewsRecyclerView;
    private TextView        mEmptyTextView;

    private String mAction = null;

    private boolean mLoaderInitialized = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param action placeholder until a specific action is needed.
     * @return A new instance of fragment LodgingListFragment.
     */
    public static NewsListFragment newInstance(String action) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACTION, action);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NewsListFragment() {
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

        View fragmentView = inflater.inflate(R.layout.fragment_news_list, container, false);
        mEmptyTextView = (TextView)fragmentView.findViewById(R.id.empty_news_view);

        // set up the recycler view
        mNewsRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.news_recycler_view);
        mNewsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNewsListAdapter = new NewsListAdapter(getActivity(), mEmptyTextView);
        mNewsRecyclerView.setAdapter(mNewsListAdapter);

        // restore selected item
        if (savedInstanceState != null) {
            mNewsListAdapter.onRestoreInstanceState(savedInstanceState);
        }
        return fragmentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved
        if (mNewsListAdapter != null) {
            mNewsListAdapter.onSaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume () {
        super.onResume();
        Log.d(LOG_TAG, "onResume()");
        if (! mLoaderInitialized) {
            getLoaderManager().initLoader(NEWS_LOADER, null, this);
            mLoaderInitialized = true;
        }
        Utility.updateActionBarTitle(getActivity(), getString(R.string.title_news));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(LOG_TAG, "onCreateLoader()");
        // Sort order:  record ID ascending
        String sortOrder = DatabaseContract.NewsEntry._ID + " ASC";

        return new CursorLoader(getActivity(),
                DatabaseContract.NewsEntry.CONTENT_URI,
                null, // get all columns
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG, "onLoadFinished()");
        mNewsListAdapter.swapCursor(data);
        updateEmptyView();
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "onLoaderReset()");
        mNewsListAdapter.swapCursor(null);
    }

    /**
     * updateEmptyView -- updates text message based on what was stored by the sync adapter
     */
    private void updateEmptyView() {
        if ( mNewsListAdapter.getItemCount() == 0 ) {
            mEmptyTextView.setText(Utility.getServerStatusMessage(getActivity()));
        }
    }
}