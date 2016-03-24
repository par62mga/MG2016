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
 * Created by Phil Robertson on 12/14/2015.
 */
public class NewsListAdapter extends ArrayAdapter<NewsItem> {
    private Context mContext;
    private LayoutInflater mInflater;


    static class ViewHolder {
        ImageView imageViewNewsThumbnail;
        TextView  textViewNewsTitle;
        TextView  textViewNewsDate;
    };

    // constructor used to populate adapter with list of artist items
    public NewsListAdapter (Context context, int resourceId, List<NewsItem> items) {
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
            itemView = (RelativeLayout) mInflater.inflate(R.layout.news_list_item, parent, false);

            // get and save references to the artist_list_item view elements
            itemHolder = new ViewHolder ();

            itemHolder.imageViewNewsThumbnail = (ImageView) itemView.findViewById(R.id.news_thumbnail);
			
            itemHolder.textViewNewsTitle = (TextView) itemView.findViewById(R.id.news_title);
            itemHolder.textViewNewsDate  = (TextView) itemView.findViewById(R.id.news_date);

            itemView.setTag(itemHolder);
        } else {
            itemView   = (RelativeLayout)convertView;
            itemHolder = (ViewHolder)itemView.getTag();
        }

        NewsItem newsItem = getItem (position);
        
        itemHolder.textViewNewsTitle.setText(newsItem.getNewsItemTitle());
        itemHolder.textViewNewsDate.setText(newsItem.getNewsItemDate());
		
        return itemView;
    }

}
