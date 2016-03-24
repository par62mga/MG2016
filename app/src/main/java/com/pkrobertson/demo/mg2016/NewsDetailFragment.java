package com.pkrobertson.demo.mg2016;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class NewsDetailFragment extends DialogFragment {
    private static final String LOG_TAG = NewsDetailFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "news_detail";

    public static final String DEFAULT = "default";
    
    private static final String ARG_ACTION = "action";

    private ImageView mImageViewPhoto;
    private TextView  mTextViewTitle;
    private TextView  mTextViewSubtitle;
    private TextView  mTextViewDate;
    private TextView  mTextViewContent;
    
    // private FloatingActionButton mActionButtonShare;

    private String mAction = null;

    private static NewsItem sNewsItem = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param action that defines how the activity was launched.
     * @return A new instance of fragment NewsDetailFragment.
     */
    public static NewsDetailFragment newInstance(String action) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACTION, action);
        fragment.setArguments(args);
        return fragment;
    }

    public NewsDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAction = getArguments().getString(ARG_ACTION);
        }
        int myTheme = getTheme();
        setStyle (DialogFragment.STYLE_NORMAL, myTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View fragmentView = inflater.inflate(R.layout.fragment_news_detail, container, false);

        initUi(fragmentView);
        
		/*
        mActionButtonShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
		*/

        Utility.hideSoftInput(getActivity());
        return fragmentView;
    }

    @Override
    public void onResume () {
        if (sNewsItem != null) {
            Utility.updateActionBarTitle(getActivity(), sNewsItem.getNewsItemTitle());
        }
        super.onResume();
    }

    public void setNewsItem (NewsItem newsItem) {
        sNewsItem = newsItem;
    }

    private void setTextViewText (TextView view, String text) {
        if (text != null) {
            view.setText (text);
        } else {
            view.setText ("");
        }
    }

    private void initUi(View view) {		
        mImageViewPhoto   = (ImageView)view.findViewById(R.id.news_photo);
		
        mTextViewTitle    = (TextView)view.findViewById(R.id.news_title);
        mTextViewSubtitle = (TextView)view.findViewById(R.id.news_subtitle);
        mTextViewDate     = (TextView)view.findViewById(R.id.news_date);
        mTextViewContent  = (TextView)view.findViewById(R.id.news_content);
       

        // mActionButtonShare  = (FloatingActionButton) view.findViewById(R.id.news_fab);
        if (sNewsItem == null)
            return;

        setTextViewText(mTextViewTitle,    sNewsItem.getNewsItemTitle());
        setTextViewText(mTextViewSubtitle, sNewsItem.getNewsItemSubtitle());
		setTextViewText(mTextViewDate,     sNewsItem.getNewsItemDate());
		setTextViewText(mTextViewContent,  sNewsItem.getNewsItemContent());
    }
}
