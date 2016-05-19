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
import android.widget.TextView;

import com.pkrobertson.demo.mg2016.data.AppConfig;
import com.pkrobertson.demo.mg2016.data.DatabaseContract;
import com.pkrobertson.demo.mg2016.data.DateTimeHelper;

/**
 * EventListAdapter -- handles the event list recycler view that is held under one event list
 *     fragment for each day in the Event Diary
 */
public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {
    private static final String LOG_TAG       = EventListAdapter.class.getSimpleName();

    private static final String SELECTION_KEY = "event_selection_key";
    public  static final int    NO_SELECTION  = -1;

    private static final int    MIN_TEXT_LINES  = 2;
    private static final int    MAX_TEXT_LINES  = 32;

    private static final int    FAST_ANIMATION = 200; // 200 msec animation when hiding lines
    private static final int    SLOW_ANIMATION = 400; // 400 msec animation when expanding lines

    // this is a reference to the "selected" row actually in view or nearly in view
    private EventViewHolder mSelectedRow = null;

    // this is a hack to ignore spurious onDetailFromWindow call that occurs with the last element
    // after a screen rotation
    private EventViewHolder mLastOnAttachView = null;

    private Context      mContext;
    private Cursor       mCursor;
    private RecyclerView mRecyclerView;
    private View         mEmptyView;

    // used to restore selected item after screen rotation
    private long    mWidgetItem = NO_SELECTION;

    // used to go to a specific item when selected/launched from the widget
    private int     mRestoreSelectedItem = NO_SELECTION;

    // database column IDs used to access cursor data
    private int     mIndexID;
    private int     mIndexDate;
    private int     mIndexStartTime;
    private int     mIndexEndTime;
    private int     mIndexTitle;
    private int     mIndexLocation;
    private int     mIndexMapLocation;
    private int     mIndexContent;

    class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View        mViewEventTime;
        TextView    mTextViewEventStart;
        TextView    mTextViewEventEnd;
        TextView    mTextViewEventTitle;
        TextView    mTextViewEventSubtitle;

        // save data used to launch map and locate actions
        int         mEventId;
        long        mEventDate;
        long        mEventStartTime;
        long        mEventEndTime;
        String      mEventLocation;
        String      mEventMap;

        public EventViewHolder(View view) {
            super(view);
            mViewEventTime         =           view.findViewById(R.id.event_time);
            mTextViewEventStart    = (TextView)view.findViewById(R.id.event_start);
            mTextViewEventEnd      = (TextView)view.findViewById(R.id.event_end);
            mTextViewEventTitle    = (TextView)view.findViewById(R.id.event_title);
            mTextViewEventSubtitle = (TextView)view.findViewById(R.id.event_subtitle);
            view.setClickable(true);
            view.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {
            boolean selectThisRow = this != mSelectedRow;
            if (mSelectedRow != null) {
                deselectItem (mSelectedRow, true);
            }
            if (selectThisRow) {
                selectItem(this, true);
            }
        }

    }


    /**
     * EventListAdapter -- constructor
     * @param context
     * @param emptyView
     * @param selectedItem
     */
    public EventListAdapter (
            Context context, RecyclerView recyclerView, View emptyView, long selectedItem) {
        // save reference to context
        mContext = context;
        // save reference to recycler view
        mRecyclerView = recyclerView;
        // save reference to text view
        mEmptyView = emptyView;
        // item selected by the widget
        mWidgetItem = selectedItem;
    }

    /**
     * onSaveInstanceState -- called by the fragment to save currently selected event
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState) {
        if (mSelectedRow != null) {
            outState.putInt (SELECTION_KEY, mSelectedRow.getAdapterPosition());
        } else if (mRestoreSelectedItem != NO_SELECTION) {
            outState.putInt (SELECTION_KEY, mRestoreSelectedItem);
        }
    }

    /**
     * onRestoreInstanceState -- called by the fragment to restore the selection
     * @param savedInstanceState
     * @return sekected item or NO_SELECTION
     */
    public int onRestoreInstanceState(Bundle savedInstanceState) {
        if ( savedInstanceState.containsKey(SELECTION_KEY)) {
            // selected item to show on restore
            mRestoreSelectedItem = savedInstanceState.getInt(SELECTION_KEY);
        } else {
            mRestoreSelectedItem = NO_SELECTION;
        }
        Log.d(LOG_TAG, "onRestoreInstanceState() mRestoreSelectedItem ==> " + mRestoreSelectedItem);
        return mRestoreSelectedItem;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewGroup instanceof RecyclerView) {
            View view = LayoutInflater.from(
                    viewGroup.getContext()).inflate(R.layout.event_list_item, viewGroup, false);
            view.setFocusable(true);
            return new EventViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(EventViewHolder viewHolder, int position) {
        Log.d (LOG_TAG, "onBindViewHolder () position ==> " + position);
        if (mCursor == null) {
            return;
        }
        mCursor.moveToPosition(position);

        viewHolder.mEventId        = mCursor.getInt(mIndexID);
        viewHolder.mEventDate      = mCursor.getLong(mIndexDate);
        viewHolder.mEventStartTime = mCursor.getLong(mIndexStartTime);
        viewHolder.mEventEndTime   = mCursor.getLong(mIndexEndTime);

        int eventColor = Utility.getEventColor(viewHolder.mEventId);
        viewHolder.mViewEventTime.setBackgroundColor(mContext.getResources().getColor(eventColor));
        Utility.setTextView(viewHolder.mTextViewEventStart,
                DateTimeHelper.formatTime(viewHolder.mEventStartTime, Utility.is24HourFormat(mContext)));
        String endText = " ";
        if (viewHolder.mEventEndTime >= 0) {
            endText = DateTimeHelper.formatTime(viewHolder.mEventEndTime, Utility.is24HourFormat(mContext));
        }
        Utility.setTextView(viewHolder.mTextViewEventEnd, endText);

        Utility.setTextView(viewHolder.mTextViewEventTitle, mCursor.getString(mIndexTitle));
        Utility.setTextViewFromHTML(viewHolder.mTextViewEventSubtitle,
                mCursor.getString(mIndexContent));

        String location = mCursor.getString(mIndexLocation);
        if (location == null) {
            viewHolder.mEventLocation = null;
            viewHolder.mEventMap = null;
        } else if (location.contentEquals(DatabaseContract.EventsEntry.DEFAULT_LOCATION)) {
            AppConfig appConfig = AppConfig.getInstance(mContext);
            viewHolder.mEventLocation = appConfig.getDefaultLocation();
            viewHolder.mEventMap      = appConfig.getDefaultMap();
        } else {
            viewHolder.mEventLocation = location;
            viewHolder.mEventMap      = mCursor.getString(mIndexMapLocation);
        }

        // view is about to be displayed, time to restore the selected item after rotation/change
        if (viewHolder.getAdapterPosition() == mRestoreSelectedItem) {
            Log.d(LOG_TAG, "onBindViewHolder () mRestoreSelectedItem ==> " + mRestoreSelectedItem);
            selectItem (viewHolder, false);
        } else if ((mRestoreSelectedItem == NO_SELECTION) && (viewHolder.mEventId == mWidgetItem)) {
            Log.d(LOG_TAG, "onViewAttachedToWindow () mWidgetItem ==> " + mWidgetItem);
            selectItem (viewHolder, false);
        }
    }

    @Override
    public void onViewAttachedToWindow (EventViewHolder viewHolder) {
        // view is about to be displayed, time to restore the selected item after rotation/change
        if (viewHolder.getAdapterPosition() == mRestoreSelectedItem) {
            Log.d(LOG_TAG, "onViewAttachedToWindow () mRestoreSelectedItem ==> " + mRestoreSelectedItem);
            selectItem(viewHolder, false);
        } else if ((mRestoreSelectedItem == NO_SELECTION) && (viewHolder.mEventId == mWidgetItem)) {
            Log.d(LOG_TAG, "onViewAttachedToWindow () mWidgetItem ==> " + mWidgetItem);
            selectItem (viewHolder, false);
        }
        mLastOnAttachView = viewHolder;
        super.onViewAttachedToWindow(viewHolder);
    }

    @Override
    public void onViewDetachedFromWindow (EventViewHolder viewHolder) {
        // view is about to go off screen, deselect the row if selected
        // ignore onViewDetached call when it occurs right after onViewAttached...
        if ((viewHolder != mLastOnAttachView) && (viewHolder == mSelectedRow)) {
            Log.d(LOG_TAG, "onViewDetatchedFromWindow () item ==> " + viewHolder.getAdapterPosition());
            deselectItem(viewHolder, false);
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
            mIndexID          = mCursor.getColumnIndex(DatabaseContract.EventsEntry._ID);
            mIndexDate        = mCursor.getColumnIndex(DatabaseContract.EventsEntry.COLUMN_START_DATE);
            mIndexStartTime   = mCursor.getColumnIndex(DatabaseContract.EventsEntry.COLUMN_START_TIME);
            mIndexEndTime     = mCursor.getColumnIndex(DatabaseContract.EventsEntry.COLUMN_END_TIME);
            mIndexTitle       = mCursor.getColumnIndex(DatabaseContract.EventsEntry.COLUMN_TITLE);
            mIndexLocation    = mCursor.getColumnIndex(DatabaseContract.EventsEntry.COLUMN_LOCATION);
            mIndexMapLocation = mCursor.getColumnIndex(DatabaseContract.EventsEntry.COLUMN_MAP_LOCATION);
            mIndexContent     = mCursor.getColumnIndex(DatabaseContract.EventsEntry.COLUMN_CONTENT);
        }
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {
        return mCursor;
    }

    /**
     * onPageNotVisible -- used to deselect item when user scrolls to a different page in the
     *     event diary
     */
    public void onPageNotVisible () {
        // page is off screen, deselect row
        Log.d (LOG_TAG, "onPageNotVisible()");
        if (mSelectedRow != null) {
            deselectItem(mSelectedRow, false);
        }
    }

    /**
     * getHandler -- casts activity to the OnFragmentInteraction handler to get access to turn
     *     on/off Calendar and Locate menu options
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
    private void deselectItem (EventViewHolder viewHolder, boolean withAnimation) {
        Log.d (LOG_TAG, "deselectItem() mEventId ==> " + viewHolder.mEventId);
        mSelectedRow = null;
        viewHolder.itemView.setSelected(false);

        // turn off menu options for calendar, locate
        getHandler().disableMenuItems();

        // hide extra text
        if (withAnimation) {
            ObjectAnimator animator = ObjectAnimator.ofInt (
                    viewHolder.mTextViewEventSubtitle, "maxLines", MIN_TEXT_LINES);
            animator.setDuration(FAST_ANIMATION).start();
        } else {
            viewHolder.mTextViewEventSubtitle.setMaxLines(MIN_TEXT_LINES);
        }
    }

    /**
     * selectItem -- used to select a row and expand detail text
     * @param viewHolder
     * @param withAnimation
     */
    private void selectItem (EventViewHolder viewHolder, boolean withAnimation) {
        mRestoreSelectedItem = NO_SELECTION;
        mWidgetItem = NO_SELECTION;
        mSelectedRow = viewHolder;
        viewHolder.itemView.setSelected(true);
        Log.d (LOG_TAG, "selectItem() mEventId ==> " + viewHolder.mEventId);

        // turn on menu options for calendar and locate
        OnFragmentInteraction handler = getHandler();
        Log.d (LOG_TAG, "selectItem() enabling calendar...");
        handler.enableMenuItemCalendar(
                viewHolder.mEventDate, viewHolder.mEventStartTime, viewHolder.mEventEndTime,
                String.valueOf(viewHolder.mTextViewEventTitle.getText()),
                String.valueOf(viewHolder.mTextViewEventSubtitle.getText()),
                viewHolder.mEventLocation == null ? "" : viewHolder.mEventLocation);
        if (viewHolder.mEventLocation != null) {
            handler.enableMenuItemLocate(
                    String.valueOf(viewHolder.mTextViewEventTitle.getText()),
                    viewHolder.mEventLocation,
                    viewHolder.mEventMap);
        }
        handler.showMenuItems();

        // show extra text
        if (withAnimation) {
            ObjectAnimator animator = ObjectAnimator.ofInt (
                    viewHolder.mTextViewEventSubtitle, "maxLines", MAX_TEXT_LINES);
            animator.setDuration(SLOW_ANIMATION).start();

            // make sure expanded information is on the screen
            int position = viewHolder.getAdapterPosition();
            if (position + 1 < EventListAdapter.this.getItemCount()) {
                position += 1;
            }
            mRecyclerView.smoothScrollToPosition(position);
            Log.d(LOG_TAG, "selectItem() scrolling to ==> " + position);
        } else {
            viewHolder.mTextViewEventSubtitle.setMaxLines(MAX_TEXT_LINES);
        }

    }
}