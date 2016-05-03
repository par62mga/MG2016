package com.pkrobertson.demo.mg2016;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.pkrobertson.demo.mg2016.data.AppConfig;
import com.pkrobertson.demo.mg2016.data.DatabaseContract;
import com.squareup.picasso.Picasso;


public class NewsDetailFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = NewsDetailFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "news_detail";

    private static final String ARG_URISTRING = "uri";

    private static final int NEWS_DETAIL_LOADER = 201;

    private View mFragmentView;
    private Uri  mNewsUri = null;

    private Intent mShareIntent = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param newsUri provides news item to view.
     * @return A new instance of fragment NewsDetailFragment.
     */
    public static NewsDetailFragment newInstance(String newsUri) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URISTRING, newsUri);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NewsDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNewsUri = Uri.parse(getArguments().getString(ARG_URISTRING));
        }
        int myTheme = getTheme();
        setStyle(DialogFragment.STYLE_NORMAL, myTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mFragmentView = inflater.inflate(R.layout.fragment_news_detail, container, false);
        if (mNewsUri != null) {
            getLoaderManager().restartLoader(NEWS_DETAIL_LOADER, null, this);
        }


        Utility.hideSoftInput(getActivity());
        return mFragmentView;
    }

    @Override
    public void onResume () {
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(LOG_TAG, "onCreateLoader()");

        return new CursorLoader(getActivity(),
                mNewsUri,
                null, // get all columns
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String  newsTitle = data.getString(
                data.getColumnIndex(DatabaseContract.NewsEntry.COLUMN_TITLE));

        Utility.updateActionBarTitle(getActivity(), newsTitle);
        Utility.setImageView((ImageView) mFragmentView.findViewById(R.id.news_photo),
                getActivity(),
                data.getString(data.getColumnIndex(DatabaseContract.NewsEntry.COLUMN_IMAGE)),
                R.drawable.news_placeholder);
        Utility.setTextView((TextView) mFragmentView.findViewById(R.id.news_title), newsTitle);
        Utility.setTextView((TextView) mFragmentView.findViewById(R.id.news_subtitle),
                data,
                DatabaseContract.NewsEntry.COLUMN_BYLINE1);
        Utility.setTextView((TextView) mFragmentView.findViewById(R.id.news_date),
                data,
                DatabaseContract.NewsEntry.COLUMN_BYLINE2);
        Utility.setTextViewFromHTML((TextView) mFragmentView.findViewById(R.id.news_content),
                data,
                DatabaseContract.NewsEntry.COLUMN_CONTENT);

        mShareIntent = new Intent(Intent.ACTION_SEND);
        mShareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        mShareIntent.setType("text/plain");
        mShareIntent.putExtra(Intent.EXTRA_TEXT, data.getString(
                data.getColumnIndex(DatabaseContract.NewsEntry.COLUMN_SHARE)));

        ((FloatingActionButton)mFragmentView.findViewById(R.id.fab)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(Intent.createChooser(mShareIntent, getString(R.string.news_share)));
                    }
                });
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "onLoaderReset()");
    }

}
