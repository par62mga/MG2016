package com.pkrobertson.demo.mg2016;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pkrobertson.demo.mg2016.data.AppConfig;
import com.pkrobertson.demo.mg2016.data.DatabaseContract;
import com.pkrobertson.demo.mg2016.data.DateTimeHelper;

import java.util.List;

/**
 * Created by Phil Robertson on 3/14/2016.
 */
public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {
    private static final String LOG_TAG       = EventListAdapter.class.getSimpleName();

    private static final String SELECTION_KEY = "event_selection_key";
    private static final int    NO_SELECTION  = -1;

    private static final int    MIN_TEXT_LINES = 2;
    private static final int    MAX_TEXT_LINES = 32;

    private int mSelectedItem = NO_SELECTION;
    private EventViewHolder mSelectedRow = null;

    private MenuItem mMenuItemLocate = null;
    private MenuItem mMenuItemCalendar = null;

    private Context mContext;
    private Cursor  mCursor;
    private View    mEmptyView;
    private int     mRestoreSelectedItem = NO_SELECTION;

    private int     mIndexID;
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

        public void selectItem (int position) {
            mSelectedItem = position;
            mSelectedRow  = this;
            mSelectedRow.itemView.setSelected(true);
            if (mMenuItemLocate != null) {
                mMenuItemLocate.setVisible(true);
                mMenuItemCalendar.setVisible(true);
            }
        }

        @Override
        public void onClick(View v) {
            if (mSelectedRow != null) {
                mSelectedRow.itemView.setSelected(false);
                ObjectAnimator animator = ObjectAnimator.ofInt (
                        mSelectedRow.mTextViewEventSubtitle, "maxLines", MIN_TEXT_LINES);
                animator.setDuration(200).start();
                if (mSelectedRow == this) {
                    mSelectedRow = null;
                    mSelectedItem = NO_SELECTION;
                    if (mMenuItemLocate != null) {
                        mMenuItemLocate.setVisible(false);
                        mMenuItemCalendar.setVisible(false);
                    }
                    return;
                }
            }

            selectItem(getAdapterPosition());
            ObjectAnimator animator = ObjectAnimator.ofInt (
                    mTextViewEventSubtitle, "maxLines", MAX_TEXT_LINES);
            animator.setDuration(300).start();
            //TODO: scroll recycler view up to make sure text is visible
        }

    };


    public EventListAdapter (Context context, View emptyView) {
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
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewGroup instanceof RecyclerView) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_list_item,
                    viewGroup,
                    false);
            view.setFocusable(true);
            return new EventViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    private int getEventColor (int eventID) {
        int eventCategory = 10 * ((eventID % 100) / 10);
        int eventColor = R.color.colorEventDefault;
        switch (eventCategory) {
            case 10:
                eventColor = R.color.colorEvent10;
                break;
            case 20:
                eventColor = R.color.colorEvent20;
                break;
            case 30:
                eventColor = R.color.colorEvent30;
                break;
            case 40:
                eventColor = R.color.colorEvent40;
                break;
            case 50:
                eventColor = R.color.colorEvent50;
                break;
            case 60:
                eventColor = R.color.colorEvent60;
                break;
            case 70:
                eventColor = R.color.colorEvent70;
                break;
            default:
                break;
        }
        return eventColor;
    }

    @Override
    public void onBindViewHolder(EventViewHolder viewHolder, int position) {
        if (mCursor == null) {
            return;
        }
        mCursor.moveToPosition(position);

        int eventColor = getEventColor(mCursor.getInt(mIndexID));
        viewHolder.mViewEventTime.setBackgroundColor(mContext.getResources().getColor(eventColor));
        Utility.setTextView(viewHolder.mTextViewEventStart,
                DateTimeHelper.formatTime(mCursor.getLong(mIndexStartTime), false));
        long   endTime = mCursor.getLong(mIndexEndTime);
        String endText = " ";
        if (endTime >= 0) {
            endText = DateTimeHelper.formatTime(endTime, false);
        }
        Utility.setTextView(viewHolder.mTextViewEventEnd, endText);

        Utility.setTextView(viewHolder.mTextViewEventTitle,    mCursor.getString(mIndexTitle));
        Utility.setTextViewFromHTML(viewHolder.mTextViewEventSubtitle,
                mCursor.getString(mIndexContent));

        String location = mCursor.getString(mIndexLocation);
        if (location == DatabaseContract.EventsEntry.DEFAULT_LOCATION) {
            AppConfig appConfig = AppConfig.getInstance(mContext);
            viewHolder.mEventLocation = appConfig.getDefaultLocation();
            viewHolder.mEventMap      = appConfig.getDefaultMap();
        } else {
            viewHolder.mEventLocation = location;
            viewHolder.mEventMap      = mCursor.getString(mIndexMapLocation);
        }

        if (position == mRestoreSelectedItem) {
            mRestoreSelectedItem = NO_SELECTION;
            viewHolder.selectItem(position);
            viewHolder.mTextViewEventSubtitle.setMaxLines(MAX_TEXT_LINES);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public void onCreateOptionsMenu (Menu menu) {
        mMenuItemLocate   = menu.findItem(R.id.action_locate);
        mMenuItemCalendar = menu.findItem(R.id.action_calendar);
        if (mSelectedRow == null) {
            mMenuItemLocate.setVisible(false);
            mMenuItemCalendar.setVisible(false);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mSelectedRow == null) {
            return false;
        }

        int id = item.getItemId();
        if (id == R.id.action_locate) {
            Uri geoLocation = Uri.parse(
                    "geo:0,0?q=" + mSelectedRow.mEventMap);
            Log.d(LOG_TAG, "onOptionsItemSelected Uri ==> " + geoLocation.toString());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData (geoLocation);
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(intent);
            }
            return true;
        } else if (id == R.id.action_calendar) {
            return true;
        }

        return false;
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        mCursor = newCursor;
        if (mCursor != null) {
            mIndexID          = mCursor.getColumnIndex(DatabaseContract.EventsEntry._ID);
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

}