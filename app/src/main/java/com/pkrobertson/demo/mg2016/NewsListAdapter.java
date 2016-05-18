package com.pkrobertson.demo.mg2016;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkrobertson.demo.mg2016.data.DatabaseContract;

/**
 * NewsListAdapter -- handles the news list recycler view that is held under the
 *     NewsListFragment
 */
public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsViewHolder> {
    private static final String LOG_TAG = NewsListAdapter.class.getSimpleName();

    private static final String SELECTION_KEY = "news_selection_key";
    private static final int    NO_SELECTION  = -1;

    // this is a reference to the "selected" row actually in view or nearly in view
    private NewsViewHolder mSelectedRow = null;

    private Context mContext;
    private Cursor  mCursor;
    private View    mEmptyView;

    // used to restore selected item after screen rotation
    private int     mRestoreSelectedItem = NO_SELECTION;

    // database column IDs used to access cursor data
    private int     mIndexID;
    private int     mIndexThumbnail;
    private int     mIndexTitle;
    private int     mIndexByline1;

    class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImageViewNewsThumbnail;
        TextView  mTextViewNewsTitle;
        TextView  mTextViewNewsByline;
        long      mNewsID = NO_SELECTION;

        public NewsViewHolder(View view) {
            super(view);
            mImageViewNewsThumbnail = (ImageView)  view.findViewById(R.id.news_thumbnail);
            mTextViewNewsTitle      = (TextView)   view.findViewById(R.id.news_title);
            mTextViewNewsByline     = (TextView)   view.findViewById(R.id.news_byline);
            view.setClickable(true);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            boolean selectThisRow = this != mSelectedRow;
            if (mSelectedRow != null) {
                deselectItem (mSelectedRow);
            }
            if (selectThisRow) {
                selectItem(this);

                // launch news detail page
                Uri newsUri = DatabaseContract.NewsEntry.buildNewsUri(mNewsID);
                getHandler().onNewsListInteraction(newsUri.toString());
            }
        }

    };


    /**
     * NewsListAdapter -- constructor
     * @param context
     * @param emptyView
     */
    public NewsListAdapter (Context context, View emptyView) {
        // save reference to context
        mContext = context;

        // mOnClickListener = listener;
        mEmptyView = emptyView;

    }

    /**
     * onSaveInstanceState -- called by the fragment to save currently selected hotel
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState) {
        if (mSelectedRow != null) {
            outState.putInt (SELECTION_KEY, mSelectedRow.getAdapterPosition());
        }
    }

    /**
     * onRestoreInstanceState -- called by the fragment to restore the selection
     * @param savedInstanceState
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if ( savedInstanceState.containsKey(SELECTION_KEY)) {
            // selected item to show on restore
            mRestoreSelectedItem = savedInstanceState.getInt(SELECTION_KEY);
        } else {
            mRestoreSelectedItem = NO_SELECTION;
        }
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if ( viewGroup instanceof RecyclerView ) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_list_item, viewGroup, false);
            view.setFocusable(true);
            return new NewsViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(NewsViewHolder viewHolder, int position) {
        if (mCursor == null) {
            return;
        }
        mCursor.moveToPosition(position);
        viewHolder.mNewsID = mCursor.getInt (mIndexID);
        String newsTitle   = mCursor.getString(mIndexTitle);
        String contentDescription = String.format(
                mContext.getString(R.string.content_image_for), newsTitle);

        Utility.setImageView(viewHolder.mImageViewNewsThumbnail,
                mContext,
                mCursor.getString(mIndexThumbnail),
                null,
                R.drawable.news_placeholder,
                contentDescription);
        Utility.setTextView(viewHolder.mTextViewNewsTitle, mCursor.getString(mIndexTitle));
        Utility.setTextView(viewHolder.mTextViewNewsByline, mCursor.getString(mIndexByline1));
    }

    @Override
    public void onViewAttachedToWindow (NewsViewHolder viewHolder) {
        // view is about to be displayed, time to restore the selected item after rotation/change
        if (viewHolder.getAdapterPosition() == mRestoreSelectedItem) {
            Log.d(LOG_TAG, "onViewAttachedToWindow () mRestoreSelectedItem ==> " + mRestoreSelectedItem);
            selectItem(viewHolder);
        }
        super.onViewAttachedToWindow(viewHolder);
    }

    @Override
    public void onViewDetachedFromWindow (NewsViewHolder viewHolder) {
        // view is about to go off screen, deselect the row if selected
        if (viewHolder == mSelectedRow) {
            deselectItem (viewHolder);
        }
        super.onViewDetachedFromWindow(viewHolder);
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    /**
     * swapCursor -- new data set is ready or old data set is no longer available
     * @param newCursor
     */
    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        mCursor = newCursor;
        if (mCursor != null) {
            mIndexID        = mCursor.getColumnIndex(DatabaseContract.NewsEntry._ID);
            mIndexThumbnail = mCursor.getColumnIndex(DatabaseContract.NewsEntry.COLUMN_THUMBNAIL);
            mIndexTitle     = mCursor.getColumnIndex(DatabaseContract.NewsEntry.COLUMN_TITLE);
            mIndexByline1   = mCursor.getColumnIndex(DatabaseContract.NewsEntry.COLUMN_BYLINE1);
        }
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {
        return mCursor;
    }

    /**
     * getHandler -- casts activity to the OnFragmentInteraction handler to get access to turn
     *     on/off Call, Locate and Website menu options
     * @return handler
     */
    private OnFragmentInteraction getHandler () {
        OnFragmentInteraction handler = null;

        try {
            handler = (OnFragmentInteraction) mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException(mContext.toString()
                    + " must implement OnFragmentInteraction");
        }
        return handler;
    }

    /**
     * deselectItem -- used to deselect a currently selected row and hide detail text
     * @param viewHolder
     */
    private void deselectItem (NewsViewHolder viewHolder) {
        mSelectedRow = null;
        viewHolder.itemView.setSelected(false);
    }

    /**
     * selectItem -- used to select a row and expand detail text
     * @param viewHolder
     */
    private void selectItem (NewsViewHolder viewHolder) {
        mRestoreSelectedItem = NO_SELECTION;
        mSelectedRow = viewHolder;
        viewHolder.itemView.setSelected(true);
    }
}