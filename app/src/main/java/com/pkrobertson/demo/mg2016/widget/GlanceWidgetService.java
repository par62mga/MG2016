package com.pkrobertson.demo.mg2016.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.pkrobertson.demo.mg2016.R;
import com.pkrobertson.demo.mg2016.Utility;
import com.pkrobertson.demo.mg2016.data.AppConfig;
import com.pkrobertson.demo.mg2016.data.DatabaseContract;
import com.pkrobertson.demo.mg2016.data.DateTimeHelper;

/**
 * GlanceWidgetService -- implements MG2016 At a Glance widget functionality
 */
public class GlanceWidgetService extends RemoteViewsService {
    private final String LOG_TAG = GlanceWidgetService.class.getSimpleName();

    // define projection used to retrieve events for the widget
    private static final String[] COLUMNS = {
            DatabaseContract.EventsEntry.TABLE_NAME + "." + DatabaseContract.EventsEntry._ID,
            DatabaseContract.EventsEntry.COLUMN_START_DATE,
            DatabaseContract.EventsEntry.COLUMN_START_TIME,
            DatabaseContract.EventsEntry.COLUMN_END_TIME,
            DatabaseContract.EventsEntry.COLUMN_TITLE,
            DatabaseContract.EventsEntry.COLUMN_LOCATION
    };

    // these indices must match the above projection
    private static final int INDEX_ID         = 0;
    private static final int INDEX_START_DATE = 1;
    private static final int INDEX_START_TIME = 2;
    private static final int INDEX_END_TIME   = 3;
    private static final int INDEX_TITLE      = 4;
    private static final int INDEX_LOCATION   = 5;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        // Log.d (LOG_TAG, "onGetViewFactory()");
        return new GlanceViewsFactory();
    }

    public class GlanceViewsFactory implements RemoteViewsService.RemoteViewsFactory {


        private Cursor mCursor = null;

        @Override
        public void onCreate() {
            // Nothing to do
        }

        @Override
        public void onDataSetChanged() {
            //Log.d(LOG_TAG, "onDataSetChanged()");
            if (mCursor != null) {
                mCursor.close();
            }
            AppConfig appConfig = AppConfig.getInstance(GlanceWidgetService.this);

            // This method is called by the app hosting the widget (e.g., the launcher)
            // However, our ContentProvider is not exported so it doesn't have access to the
            // data. Therefore we need to clear (and finally restore) the calling identity so
            // that calls use our process and permission
            final long identityToken = Binder.clearCallingIdentity();

            // get current date & time to only show events from this point forward
            long dateToday = DateTimeHelper.getCurrentDate(appConfig.getTzOffset());
            long timeToday = DateTimeHelper.getCurrentTime(appConfig.getTzOffset());
            //Log.d (LOG_TAG, "onDataSetChanged() dateToday ==> " + String.valueOf(dateToday) +
            //        " timeToday ==> " + String.valueOf(timeToday));

            // search for all events starting today or later
            Uri    eventsUri = DatabaseContract.EventsEntry.CONTENT_URI;
            String selectionClause =
                    DatabaseContract.EventsEntry.COLUMN_START_DATE +
                            " > " +
                            String.valueOf(dateToday) +
                            " OR ( " +
                            DatabaseContract.EventsEntry.COLUMN_START_DATE +
                            " = " +
                            String.valueOf(dateToday) +
                            " AND " +
                            DatabaseContract.EventsEntry.COLUMN_START_TIME +
                            " >= " +
                            String.valueOf(timeToday) +
                            " )";
            mCursor = getContentResolver().query(
                    eventsUri,       // Uri matched by content provider
                    COLUMNS,         // projection
                    selectionClause, // selection clause
                    null,            // selection args
                    DatabaseContract.EventsEntry.COLUMN_START_DATE + "," +
                            DatabaseContract.EventsEntry.COLUMN_START_TIME + " ASC"); // order by clause
            //Log.d (LOG_TAG, "numberOfRecords ==> " + String.valueOf(mCursor.getCount()));
            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public void onDestroy() {
            // Log.d(LOG_TAG, "onDestroy()");
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }
        }

        @Override
        public int getCount() {
            // Log.d(LOG_TAG, "getCount()");
            return mCursor == null ? 0 : mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            // Log.d(LOG_TAG, "getViewAt() position ==> " + String.valueOf(position));
            if (position == AdapterView.INVALID_POSITION ||
                    mCursor == null ||
                    !mCursor.moveToPosition(position)) {
                return null;
            }
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_list_item);

            int  eventId        = mCursor.getInt(INDEX_ID);
            long eventDate      = mCursor.getLong(INDEX_START_DATE);
            long eventStartTime = mCursor.getLong(INDEX_START_TIME);
            long eventEndTime   = mCursor.getLong(INDEX_END_TIME);

            int eventColor = Utility.getEventColor(eventId);
            views.setInt (R.id.widget_event_time, "setBackgroundResource", eventColor);
            views.setTextViewText(R.id.widget_event_date,
                    DateTimeHelper.formatDate("EEE - dd MMMM", eventDate));
            views.setTextViewText(R.id.widget_event_start,
                    DateTimeHelper.formatTime(eventStartTime,
                            Utility.is24HourFormat(GlanceWidgetService.this)) + " -");
            String endText = "";
            if (eventEndTime >= 0) {
                endText = DateTimeHelper.formatTime(eventEndTime,
                        Utility.is24HourFormat(GlanceWidgetService.this));
            }
            views.setTextViewText(R.id.widget_event_end, endText);
            views.setTextViewText(R.id.widget_event_title, mCursor.getString(INDEX_TITLE));

            String location = mCursor.getString(INDEX_LOCATION);
            if (location == null) {
                location = "None";
            } else if (location.contentEquals(DatabaseContract.EventsEntry.DEFAULT_LOCATION)) {
                AppConfig appConfig = AppConfig.getInstance(GlanceWidgetService.this);
                location = appConfig.getDefaultLocation();
            }
            views.setTextViewText(R.id.widget_event_location, location);
            // add date and match ID to the intent to open date + detail view...
            final Intent fillInIntent = new Intent();
            Uri eventUri = DatabaseContract.EventsEntry.buildEventsUri((long)eventId);
            fillInIntent.setData(eventUri);
            views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            // Log.d(LOG_TAG, "getLoadingView()");
            return new RemoteViews(getPackageName(), R.layout.widget_list_item);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            // Log.d(LOG_TAG, "getItemId()");
            if (mCursor.moveToPosition(position))
                return mCursor.getLong(INDEX_ID);
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
