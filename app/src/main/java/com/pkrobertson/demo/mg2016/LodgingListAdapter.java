package com.pkrobertson.demo.mg2016;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkrobertson.demo.mg2016.data.DatabaseContract;

/**
 * LodgingListAdapter -- handles the lodging list recycler view that is held under the
 *     LodgingListFragment
 */
public class LodgingListAdapter extends RecyclerView.Adapter<LodgingListAdapter.LodgingViewHolder> {
    private static final String LOG_TAG       = LodgingListAdapter.class.getSimpleName();

    private static final String SELECTION_KEY = "lodging_selection_key";
    private static final int    NO_SELECTION  = -1;

    private static final int    MIN_TEXT_LINES  = 1;
    private static final int    MAX_TEXT_LINES  = 32;

    private static final int    FAST_ANIMATION = 200; // 200 msec animation when hiding lines
    private static final int    SLOW_ANIMATION = 400; // 400 msec animation when expanding lines

    // this is a reference to the "selected" row actually in view or nearly in view
    private LodgingViewHolder mSelectedRow = null;

    private Context      mContext;
    private Cursor       mCursor;
    private RecyclerView mRecyclerView;
    private View         mEmptyView;

    // used to restore selected item after screen rotation
    private int     mRestoreSelectedItem = NO_SELECTION;

    // database column IDs used to access cursor data
    private int     mIndexImage;
    private int     mIndexName;
    private int     mIndexAddr1;
    private int     mIndexAddr2;
    private int     mIndexMap;
    private int     mIndexPhone;
    private int     mIndexWebsite;
    private int     mIndexDetails;

    class LodgingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView   mImageViewLodgingImage;
        TextView    mTextViewLodgingTitle;
        TextView    mTextViewLodgingAddress;
        TextView    mTextViewLodgingPhone;
        TextView    mTextViewLodgingDetail;

        // save data used to launch map or website actions
        String      mMapLocation;
        String      mLodgingWebsite;

        // used to fix issue where onDetachedFromView is called spuriously...
        boolean     mRowWasVisible;

        public LodgingViewHolder(View view) {
            super(view);
            mImageViewLodgingImage  = (ImageView)  view.findViewById(R.id.lodging_thumbnail);
            mTextViewLodgingTitle   = (TextView)   view.findViewById(R.id.lodging_title);
            mTextViewLodgingAddress = (TextView)   view.findViewById(R.id.lodging_address);
            mTextViewLodgingPhone   = (TextView)   view.findViewById(R.id.lodging_phone);
            mTextViewLodgingDetail  = (TextView)   view.findViewById(R.id.lodging_detail);
            mRowWasVisible = false;
            view.setClickable(true);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            boolean selectThisRow = this != mSelectedRow;
            mRowWasVisible = true;
            if (mSelectedRow != null) {
                deselectItem (mSelectedRow, true);
            }
            if (selectThisRow) {
                selectItem (this, true);
            }
        }

    };


    /**
     * LodgingListAdapter -- constructor
     * @param context
     * @param emptyView
     */
    public LodgingListAdapter (Context context, RecyclerView recyclerView, View emptyView) {
        // save reference to context
        mContext = context;
        // save reference to recycler view for scrolling
        mRecyclerView = recyclerView;
        // save reference to text view
        mEmptyView = emptyView;
    }

    /**
     * onSaveInstanceState -- called by the fragment to save currently selected hotel
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState) {
        if (mSelectedRow != null) {
            outState.putInt(SELECTION_KEY, mSelectedRow.getAdapterPosition());
        } else if (mRestoreSelectedItem != NO_SELECTION) {
            outState.putInt (SELECTION_KEY, mRestoreSelectedItem);
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
        if (mRestoreSelectedItem != NO_SELECTION) {
            mRecyclerView.smoothScrollToPosition(mRestoreSelectedItem);
        }
        Log.d (LOG_TAG, "onRestoreInstanceState() mRestoreSelectedItem ==> " + mRestoreSelectedItem);
    }

    @Override
    public LodgingViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if ( viewGroup instanceof RecyclerView ) {
            View view = LayoutInflater.from(
                    viewGroup.getContext()).inflate(R.layout.lodging_list_item, viewGroup, false);
            view.setFocusable(true);
            return new LodgingViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(LodgingViewHolder viewHolder, int position) {
        Log.d (LOG_TAG, "onBindViewHolder () position ==> " + position);
        if (mCursor == null) {
            return;
        }
        mCursor.moveToPosition(position);
        viewHolder.mRowWasVisible = false;
        String lodgingTitle = mCursor.getString(mIndexName);
        String contentDescription = String.format(
                mContext.getString(R.string.content_image_for), lodgingTitle);

        Utility.setImageView(viewHolder.mImageViewLodgingImage,
                mContext,
                mCursor.getString(mIndexImage),
                null,
                R.drawable.lodging_placeholder,
                contentDescription);

        Utility.setTextView(viewHolder.mTextViewLodgingTitle, lodgingTitle);
        Utility.setTextView(viewHolder.mTextViewLodgingAddress,
                String.format(mContext.getString(R.string.format_lodging_address),
                        mCursor.getString(mIndexAddr1),
                        mCursor.getString(mIndexAddr2)));
        Utility.setTextView(viewHolder.mTextViewLodgingPhone, mCursor.getString(mIndexPhone));
        Utility.setTextView(viewHolder.mTextViewLodgingDetail, mCursor.getString(mIndexDetails));
        viewHolder.mTextViewLodgingDetail.setMaxLines(MIN_TEXT_LINES);

        viewHolder.mMapLocation = mCursor.getString(mIndexMap);
        viewHolder.mLodgingWebsite = mCursor.getString(mIndexWebsite);

        // view is about to be displayed, time to restore the selected item after rotation/change
        if (viewHolder.getAdapterPosition() == mRestoreSelectedItem) {
            Log.d(LOG_TAG, "onBindViewHolder () mRestoreSelectedItem ==> " + mRestoreSelectedItem);
            selectItem (viewHolder, false);
        }
    }

    @Override
    public void onViewAttachedToWindow (LodgingViewHolder viewHolder) {
        // view is about to be displayed, time to restore the selected item after rotation/change
        viewHolder.mRowWasVisible = true;
        if (viewHolder.getAdapterPosition() == mRestoreSelectedItem) {
            Log.d(LOG_TAG, "onViewAttachedToWindow () mRestoreSelectedItem ==> " + mRestoreSelectedItem);
            selectItem(viewHolder, false);
        }
        super.onViewAttachedToWindow(viewHolder);
    }

    @Override
    public void onViewDetachedFromWindow (LodgingViewHolder viewHolder) {
        // fix issue where this is spuriously called for last item in recycler view
        if (!viewHolder.mRowWasVisible) {
            return;
        }
        // view is about to go off screen, deselect the row if selected
        if (viewHolder == mSelectedRow) {
            Log.d(LOG_TAG, "onViewDetatchedFromWindow () item ==> " + viewHolder.getAdapterPosition());
            deselectItem (viewHolder, false);
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
            mIndexImage = mCursor.getColumnIndex(DatabaseContract.LodgingEntry.COLUMN_IMAGE);
            mIndexName = mCursor.getColumnIndex(DatabaseContract.LodgingEntry.COLUMN_NAME);
            mIndexAddr1 = mCursor.getColumnIndex(DatabaseContract.LodgingEntry.COLUMN_ADDRESS1);
            mIndexAddr2 = mCursor.getColumnIndex(DatabaseContract.LodgingEntry.COLUMN_ADDRESS2);
            mIndexMap = mCursor.getColumnIndex(DatabaseContract.LodgingEntry.COLUMN_MAP_LOCATION);
            mIndexPhone = mCursor.getColumnIndex(DatabaseContract.LodgingEntry.COLUMN_PHONE);
            mIndexWebsite = mCursor.getColumnIndex(DatabaseContract.LodgingEntry.COLUMN_WEBSITE);
            mIndexDetails = mCursor.getColumnIndex(DatabaseContract.LodgingEntry.COLUMN_DETAILS);
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
     * @param withAnimation
     */
    private void deselectItem (LodgingViewHolder viewHolder, boolean withAnimation) {
        mSelectedRow = null;
        viewHolder.itemView.setSelected(false);

        // turn off menu options for call, locate, website
        getHandler().disableMenuItems();

        // hide extra text
        if (withAnimation) {
            ObjectAnimator animator = ObjectAnimator.ofInt (viewHolder.mTextViewLodgingDetail, "maxLines", MIN_TEXT_LINES);
            animator.setDuration(FAST_ANIMATION).start();
        } else {
            viewHolder.mTextViewLodgingDetail.setMaxLines(MIN_TEXT_LINES);
        }

    }

    /**
     * selectItem -- used to select a row and expand detail text
     * @param viewHolder
     * @param withAnimation
     */
    private void selectItem (LodgingViewHolder viewHolder, boolean withAnimation) {
        mRestoreSelectedItem = NO_SELECTION;
        mSelectedRow = viewHolder;
        viewHolder.itemView.setSelected(true);

        // turn on menu options for call, locate, website
        OnFragmentInteraction handler = getHandler();
        if (Utility.hasPhoneAvailability(mContext)) {
            handler.enableMenuItemCall(String.valueOf(viewHolder.mTextViewLodgingPhone.getText()));
        }
        handler.enableMenuItemLocate(String.valueOf(viewHolder.mTextViewLodgingTitle.getText()),
                String.valueOf(viewHolder.mTextViewLodgingAddress.getText()),
                viewHolder.mMapLocation);
        handler.enableMenuItemWebsite(viewHolder.mLodgingWebsite);

        // show extra text
        if (withAnimation) {
            ObjectAnimator animator = ObjectAnimator.ofInt (
                    viewHolder.mTextViewLodgingDetail, "maxLines", MAX_TEXT_LINES);
            animator.setDuration(SLOW_ANIMATION).start();

            // make sure expanded information is on the screen
            int position = viewHolder.getAdapterPosition();
            if (position + 1 < LodgingListAdapter.this.getItemCount()) {
                position += 1;
            }
            mRecyclerView.smoothScrollToPosition(position);
            Log.d(LOG_TAG, "selectItem() scrolling to ==> " + position);
        } else {
            viewHolder.mTextViewLodgingDetail.setMaxLines(MAX_TEXT_LINES);
        }
    }

}

