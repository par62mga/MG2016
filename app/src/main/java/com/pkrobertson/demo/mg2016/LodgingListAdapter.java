package com.pkrobertson.demo.mg2016;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Phil Robertson on 3/15/2016.
 */
public class LodgingListAdapter extends ArrayAdapter<LodgingItem> {
    private Context mContext;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener = null;


    static class ViewHolder {
        TextView textViewLodgingTitle;
        TextView textViewLodgingAddress;
        TextView textViewLodgingPhone;
        TextView textViewLodgingDetail;
    };

    // constructor used to populate adapter with list of artist items
    public LodgingListAdapter (Context context, int resourceId, List<LodgingItem> items) {
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
            itemView = (RelativeLayout) mInflater.inflate(R.layout.lodging_list_item, parent, false);

            // get and save references to the artist_list_item view elements
            itemHolder = new ViewHolder ();

            itemHolder.textViewLodgingTitle   = (TextView) itemView.findViewById(R.id.lodging_title);
            itemHolder.textViewLodgingAddress = (TextView) itemView.findViewById(R.id.lodging_address);
            itemHolder.textViewLodgingPhone   = (TextView) itemView.findViewById(R.id.lodging_phone);
            itemHolder.textViewLodgingDetail  = (TextView) itemView.findViewById(R.id.lodging_detail);
            itemHolder.textViewLodgingDetail.setHeight(0);
            ImageButton showMore = (ImageButton)itemView.findViewById(R.id.lodging_more);
            showMore.setTag (itemHolder);
            if (mOnClickListener == null) {
                mOnClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageButton thisView   = (ImageButton)v;
                        ViewHolder  thisHolder = (ViewHolder)thisView.getTag();
                        if (thisHolder.textViewLodgingDetail.getVisibility() == View.VISIBLE) {
                            thisHolder.textViewLodgingDetail.setVisibility(View.INVISIBLE);
                            thisHolder.textViewLodgingDetail.setHeight(0);
                            thisView.setImageResource (R.drawable.ic_show_more);
                        } else {
                            thisHolder.textViewLodgingDetail.setVisibility(View.VISIBLE);
                            thisView.setImageResource(R.drawable.ic_show_less);
                            thisHolder.textViewLodgingDetail.setHeight(96);;
                        }
                    }
                };
            }
            showMore.setOnClickListener(mOnClickListener);

            itemView.setTag(itemHolder);
        } else {
            itemView   = (RelativeLayout)convertView;
            itemHolder = (ViewHolder)itemView.getTag();
        }

        LodgingItem lodgingItem = getItem (position);

        itemHolder.textViewLodgingTitle.setText(lodgingItem.getLodgingItemTitle());
        itemHolder.textViewLodgingAddress.setText(lodgingItem.getLodgingItemAddress());
        itemHolder.textViewLodgingPhone.setText(lodgingItem.getLodgingItemPhone());
        itemHolder.textViewLodgingDetail.setText(lodgingItem.getLodgingItemDetail());

        return itemView;
    }

}

