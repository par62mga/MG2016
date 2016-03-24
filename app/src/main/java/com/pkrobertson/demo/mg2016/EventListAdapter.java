package com.pkrobertson.demo.mg2016;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Phil Robertson on 3/14/2016.
 */
public class EventListAdapter extends ArrayAdapter<EventItem> {
    private Context mContext;
    private LayoutInflater mInflater;


    static class ViewHolder {
        TextView textViewEventStart;
        TextView textViewEventEnd;
        TextView textViewEventTitle;
        TextView textViewEventSubtitle;
    };

    // constructor used to populate adapter with list of artist items
    public EventListAdapter (Context context, int resourceId, List<EventItem> items) {
        super (context, resourceId, items);

        // save reference to context
        this.mContext = context;

        // get layout inflater reference from context
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get reference to and optionally inflate each item
        RelativeLayout itemView;
        ViewHolder     itemHolder;

        if (convertView == null) {
            itemView = (RelativeLayout) mInflater.inflate(R.layout.event_list_item, parent, false);

            // get and save references to the artist_list_item view elements
            itemHolder = new ViewHolder ();

            itemHolder.textViewEventStart    = (TextView) itemView.findViewById(R.id.event_start);
            itemHolder.textViewEventEnd      = (TextView) itemView.findViewById(R.id.event_end);
            itemHolder.textViewEventTitle    = (TextView) itemView.findViewById(R.id.event_title);
            itemHolder.textViewEventSubtitle = (TextView) itemView.findViewById(R.id.event_subtitle);

            itemView.setTag(itemHolder);
        } else {
            itemView   = (RelativeLayout)convertView;
            itemHolder = (ViewHolder)itemView.getTag();
        }

        EventItem eventItem = getItem (position);

        itemHolder.textViewEventStart.setText(eventItem.getEventItemStart());
        itemHolder.textViewEventEnd.setText(eventItem.getEventItemEnd());
        itemHolder.textViewEventTitle.setText(eventItem.getEventItemTitle());
        itemHolder.textViewEventSubtitle.setText(eventItem.getEventItemSubtitle());

        return itemView;
    }

}

