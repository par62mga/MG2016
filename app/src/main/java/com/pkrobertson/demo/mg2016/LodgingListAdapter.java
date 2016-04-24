package com.pkrobertson.demo.mg2016;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pkrobertson.demo.mg2016.data.AppConfig;
import com.pkrobertson.demo.mg2016.data.DatabaseContract;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Phil Robertson on 3/15/2016.
 */
public class LodgingListAdapter extends RecyclerView.Adapter<LodgingListAdapter.LodgingViewHolder> {
    private static final String LOG_TAG       = LodgingListAdapter.class.getSimpleName();

    private static final String SELECTION_KEY = "lodging_selection_key";
    private static final int    NO_SELECTION  = -1;

    private static final int    MIN_TEXT_LINES = 1;
    private static final int    MAX_TEXT_LINES = 32;

    private int mSelectedItem = NO_SELECTION;
    private LodgingViewHolder mSelectedRow = null;

    private MenuItem mMenuItemCall = null;
    private MenuItem mMenuItemLocate = null;
    private MenuItem mMenuItemWebsite = null;

    private Context mContext;
    private Cursor  mCursor;
    private View    mEmptyView;
    private int     mRestoreSelectedItem = NO_SELECTION;

    private int     mIndexImage;
    private int     mIndexName;
    private int     mIndexAddr1;
    private int     mIndexAddr2;
    private int     mIndexPhone;
    private int     mIndexWebsite;
    private int     mIndexDetails;

    class LodgingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView   mImageViewLodgingImage;
        TextView    mTextViewLodgingTitle;
        TextView    mTextViewLodgingAddress;
        TextView    mTextViewLodgingPhone;
        TextView    mTextViewLodgingDetail;
        String      mLodgingWebsite;

        public LodgingViewHolder(View view) {
            super(view);
            mImageViewLodgingImage  = (ImageView)  view.findViewById(R.id.lodging_thumbnail);
            mTextViewLodgingTitle   = (TextView)   view.findViewById(R.id.lodging_title);
            mTextViewLodgingAddress = (TextView)   view.findViewById(R.id.lodging_address);
            mTextViewLodgingPhone   = (TextView)   view.findViewById(R.id.lodging_phone);
            mTextViewLodgingDetail  = (TextView)   view.findViewById(R.id.lodging_detail);
            view.setClickable(true);
            view.setOnClickListener(this);
        }

        public void selectItem (int position) {
            mSelectedItem = position;
            mSelectedRow  = this;
            mSelectedRow.itemView.setSelected(true);
            if (mMenuItemCall != null) {
                mMenuItemCall.setVisible(true);
                mMenuItemLocate.setVisible(true);
                mMenuItemWebsite.setVisible(true);
            }
        }

        @Override
        public void onClick(View v) {
            if (mSelectedRow != null) {
                mSelectedRow.itemView.setSelected(false);
                ObjectAnimator animator = ObjectAnimator.ofInt (mSelectedRow.mTextViewLodgingDetail, "maxLines", MIN_TEXT_LINES);
                animator.setDuration(200).start();
                if (mSelectedRow == this) {
                    mSelectedRow  = null;
                    mSelectedItem = NO_SELECTION;
                    if (mMenuItemCall != null) {
                        mMenuItemCall.setVisible(false);
                        mMenuItemLocate.setVisible(false);
                        mMenuItemWebsite.setVisible(false);
                    }
                    return;
                }
            }

            selectItem(getAdapterPosition());
            ObjectAnimator animator = ObjectAnimator.ofInt (mTextViewLodgingDetail, "maxLines", MAX_TEXT_LINES);
            animator.setDuration(300).start();
            //TODO: scroll recycler view up to make sure text is visible
        }

    };


    public LodgingListAdapter (Context context, View emptyView) {
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
    public LodgingViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if ( viewGroup instanceof RecyclerView ) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lodging_list_item, viewGroup, false);
            view.setFocusable(true);
            return new LodgingViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(LodgingViewHolder viewHolder, int position) {
        if (mCursor == null) {
            return;
        }
        mCursor.moveToPosition(position);

        Utility.setImageView(viewHolder.mImageViewLodgingImage,
                mContext,
                mCursor.getString(mIndexImage),
                R.drawable.lodging_placeholder);

        Utility.setTextView(viewHolder.mTextViewLodgingTitle, mCursor.getString(mIndexName));
        Utility.setTextView(viewHolder.mTextViewLodgingAddress,
                mCursor.getString(mIndexAddr1) + "\n" + mCursor.getString(mIndexAddr2));
        Utility.setTextView(viewHolder.mTextViewLodgingPhone, mCursor.getString(mIndexPhone));
        Utility.setTextView(viewHolder.mTextViewLodgingDetail, mCursor.getString(mIndexDetails));

        viewHolder.mLodgingWebsite = mCursor.getString(mIndexWebsite);
        if (position == mRestoreSelectedItem) {
            mRestoreSelectedItem = NO_SELECTION;
            viewHolder.selectItem(position);
            viewHolder.mTextViewLodgingDetail.setMaxLines(MAX_TEXT_LINES);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public void onCreateOptionsMenu (Menu menu) {
        mMenuItemCall    = menu.findItem(R.id.action_call);
        mMenuItemLocate  = menu.findItem(R.id.action_locate);
        mMenuItemWebsite = menu.findItem(R.id.action_website);
        if (mSelectedRow == null) {
            mMenuItemCall.setVisible(false);
            mMenuItemLocate.setVisible(false);
            mMenuItemWebsite.setVisible(false);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mSelectedRow == null) {
            return false;
        }

        int id = item.getItemId();
        if (id == R.id.action_call) {
            String phoneNumber = String.valueOf(mSelectedRow.mTextViewLodgingPhone.getText());
            Uri dialUri = Uri.parse("tel:" + phoneNumber);
            Log.d(LOG_TAG, "onOptionsItemSelected Uri ==> " + dialUri.toString());
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData (dialUri);
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(intent);
            }
            return true;
        } else if (id == R.id.action_locate) {
            String streetAddress = String.valueOf(mSelectedRow.mTextViewLodgingAddress.getText());
            Uri geoLocation = Uri.parse(
                    "geo:0,0?q=" + streetAddress.replace (' ', '+'));
            Log.d(LOG_TAG, "onOptionsItemSelected Uri ==> " + geoLocation.toString());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData (geoLocation);
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(intent);
            }
            return true;
        } else if (id == R.id.action_website) {
            Uri websiteUri = Uri.parse(mSelectedRow.mLodgingWebsite);
            Log.d(LOG_TAG, "onOptionsItemSelected Uri ==> " + websiteUri.toString());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData (websiteUri);
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(intent);
            }
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
            mIndexImage = mCursor.getColumnIndex(DatabaseContract.LodgingEntry.COLUMN_IMAGE);
            mIndexName = mCursor.getColumnIndex(DatabaseContract.LodgingEntry.COLUMN_NAME);
            mIndexAddr1 = mCursor.getColumnIndex(DatabaseContract.LodgingEntry.COLUMN_ADDRESS1);
            mIndexAddr2 = mCursor.getColumnIndex(DatabaseContract.LodgingEntry.COLUMN_ADDRESS2);
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

}

