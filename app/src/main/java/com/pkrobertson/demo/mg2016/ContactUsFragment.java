package com.pkrobertson.demo.mg2016;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkrobertson.demo.mg2016.data.AppConfig;
import com.pkrobertson.demo.mg2016.data.DatabaseContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Phil Robertson on 3/17/2016.
 */
public class ContactUsFragment extends Fragment  {
    private static final String LOG_TAG = ContactUsFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "contact_us";

    public static final String DEFAULT = "default";

    private static final String ARG_ACTION = "action";

    private FloatingActionButton mActionButtonEmail;

    private String mAction = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param action that defines how the activity was launched.
     * @return A new instance of fragment NewsDetailFragment.
     */
    public static ContactUsFragment newInstance(String action) {
        ContactUsFragment fragment = new ContactUsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACTION, action);
        fragment.setArguments(args);
        return fragment;
    }

    public ContactUsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAction = getArguments().getString(ARG_ACTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View fragmentView = inflater.inflate(R.layout.fragment_contact_us, container, false);

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
        Utility.updateActionBarTitle(getActivity(), getString(R.string.title_contact));
        super.onResume();
    }

    private void initUi(View view) {
        AppConfig configInfo = AppConfig.getInstance(getActivity());

        Utility.setImageView ((ImageView)view.findViewById(R.id.contact_photo),
                getActivity(),
                configInfo.getContactImageFile(),
                R.drawable.contact_placeholder);

        Utility.setTextViewFromHTML((TextView) view.findViewById(R.id.contact_details),
                configInfo.getAboutInfo());
    }
}

