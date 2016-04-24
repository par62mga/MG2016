package com.pkrobertson.demo.mg2016;

import android.animation.ObjectAnimator;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pkrobertson.demo.mg2016.data.AppConfig;
import com.pkrobertson.demo.mg2016.data.DatabaseContract;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Phil Robertson on 12/14/2015.
 */
public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsViewHolder> {
    private static final String LOG_TAG = NewsListAdapter.class.getSimpleName();

    private static final String SELECTION_KEY = "news_selection_key";
    private static final int    NO_SELECTION  = -1;

    private int mSelectedItem = NO_SELECTION;
    private NewsViewHolder mSelectedRow = null;

    private Context mContext;
    private Cursor  mCursor;
    private View    mEmptyView;
    private int     mRestoreSelectedItem = NO_SELECTION;

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

        public void selectItem (int position) {
            mSelectedItem = position;
            mSelectedRow  = this;
            mSelectedRow.itemView.setSelected(true);
        }

        @Override
        public void onClick(View v) {
            if (mSelectedRow != null) {
                mSelectedRow.itemView.setSelected(false);
            }
            selectItem(getAdapterPosition());
            if (mNewsID != NO_SELECTION) {
                OnNewsListInteraction handler = null;
                try {
                    handler = (OnNewsListInteraction)mContext;
                } catch (ClassCastException e) {
                    throw new ClassCastException(mContext.toString()
                            + " must implement OnNewsListInteraction");
                }
                Uri newsUri = DatabaseContract.NewsEntry.buildNewsUri(mNewsID);
                handler.onNewsListInteraction(newsUri.toString());
            }
        }

    };


    public NewsListAdapter (Context context, View emptyView) {
        // save reference to context
        mContext = context;
        // mOnClickListener = listener;
        mEmptyView = emptyView;

    }

    public void onSaveInstanceState(Bundle outState) {
        if (mSelectedItem != NO_SELECTION) {
            outState.putInt (SELECTION_KEY, mSelectedItem);
        }
    }

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

        Utility.setImageView(viewHolder.mImageViewNewsThumbnail,
                mContext,
                mCursor.getString(mIndexThumbnail),
                R.drawable.news_placeholder);
        Utility.setTextView(viewHolder.mTextViewNewsTitle, mCursor.getString(mIndexTitle));
        Utility.setTextView(viewHolder.mTextViewNewsByline, mCursor.getString(mIndexByline1));

        if (position == mRestoreSelectedItem) {
            mRestoreSelectedItem = NO_SELECTION;
            viewHolder.selectItem(position);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

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

    public int getSelectedItem () {
        return mSelectedItem;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    /**
     * This interface must be implemented by MainActivity to handle the news list item click and
     * open up the news detail fragment/dialog.
     */
    public interface OnNewsListInteraction {
        public void onNewsListInteraction(String newsItemUri);
    }

}