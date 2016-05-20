package com.pkrobertson.demo.mg2016.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.pkrobertson.demo.mg2016.MainActivity;
import com.pkrobertson.demo.mg2016.R;

/**
 * GlanceWidgetProvider -- implements MG2016 At a Glance widget functionality
 */
public class GlanceWidgetProvider extends AppWidgetProvider {
    private final String LOG_TAG = GlanceWidgetProvider.class.getSimpleName();

    public void onUpdate (Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        //Log.d(LOG_TAG, "onUpdate(): Starting myFetchService to get latest scores");
        //Intent service_start = new Intent(context, myFetchService.class);
        //context.startService(service_start);

        for (int widgetId : appWidgetIds) {
            // set up remove views using scores widget layout
            RemoteViews views = new RemoteViews (context.getPackageName(), R.layout.glance_widget);

            // create intent that launches the main activity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity (context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_frame, pendingIntent);

            // set up collection
            views.setRemoteAdapter(R.id.widget_listview,
                    new Intent (context, GlanceWidgetService.class));

            // set up pending intent template
            Intent clickIntentTemplate = new Intent(context, MainActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_listview, clickPendingIntentTemplate);
            views.setEmptyView(R.id.widget_listview, R.id.widget_textview);

            // perform an update on the current app widget
            appWidgetManager.updateAppWidget(widgetId, views);

        }

        Toast.makeText(context, context.getString(R.string.widget_updated), Toast.LENGTH_SHORT).show();
    }
}