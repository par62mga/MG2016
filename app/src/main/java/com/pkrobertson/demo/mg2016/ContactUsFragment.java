package com.pkrobertson.demo.mg2016;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Phil Robertson on 3/17/2016.
 */
public class ContactUsFragment extends Fragment  {
    private static final String LOG_TAG = ContactUsFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "contact_us";

    public static final String DEFAULT = "default";

    private static final String ARG_ACTION = "action";

    private ImageView mImageViewPhoto;
    private TextView  mTextViewContent;
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
        Utility.updateActionBarTitle(getActivity(), getString(R.string.drawer_contact_us));
        super.onResume();
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

        mTextViewContent  = (TextView)view.findViewById(R.id.contact_details);
        setTextViewText(mTextViewContent, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent vitae mollis ipsum. Aenean id odio nisi. Proin suscipit efficitur vehicula. Proin laoreet ipsum non tincidunt dapibus. Nam ultricies justo in libero aliquet tempor. Phasellus congue eget arcu eget tempus. Praesent sodales turpis ac velit ultricies venenatis. Fusce vehicula in sapien eget mattis. Quisque pulvinar sem at nisl varius, sit amet tempor tellus molestie. Quisque eget nisi nulla. Nullam ullamcorper dui a justo pharetra imperdiet. Curabitur elementum erat ipsum, molestie tempus ex euismod quis.\n" +
                "\n" +
                "Nullam sodales mi eu dui pulvinar luctus. Aenean ut nulla venenatis, semper diam eu, gravida nisi. Aenean vehicula enim in elit pulvinar suscipit. Donec massa ipsum, fringilla a commodo ut, venenatis vitae tellus. Vivamus lacinia est dui. Ut eros augue, suscipit sit amet suscipit et, gravida sed elit. Nulla ut lobortis urna. Pellentesque lobortis ac nisl eget gravida. Aenean tristique risus purus. Nunc ullamcorper ante ut mi pharetra, vel dapibus augue consectetur. Ut placerat purus sed purus rhoncus vestibulum. Sed sed dolor ligula.");

    }
}

