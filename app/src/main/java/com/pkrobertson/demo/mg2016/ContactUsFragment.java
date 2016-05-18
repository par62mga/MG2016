package com.pkrobertson.demo.mg2016;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkrobertson.demo.mg2016.data.AppConfig;

/**
 * ContactUsFragment -- handles the MG 2016 contact us page
 */
public class ContactUsFragment extends Fragment  {
    private static final String LOG_TAG = ContactUsFragment.class.getSimpleName();

    public static final String DEFAULT = "default";

    private static final String ARG_ACTION = "action";

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
        Utility.hideSoftInput(getActivity());
        return fragmentView;
    }

    @Override
    public void onResume () {
        Utility.updateActionBarTitle(getActivity(), getString(R.string.title_contact));
        super.onResume();
    }

    private void initUi(View view) {
        AppConfig configInfo   = AppConfig.getInstance(getActivity());
        String[]  emailAddress = new String[1];
        if (configInfo != null) {
            Utility.setImageView((ImageView) view.findViewById(R.id.contact_photo),
                    getActivity(),
                    configInfo.getContactImageFile(),
                    null,
                    R.drawable.contact_us,
                    getString(R.string.content_contact_us));
            Utility.setTextViewFromHTML((TextView) view.findViewById(R.id.contact_details),
                    configInfo.getAboutInfo());
            emailAddress[0] = configInfo.getContactEmail();
        } else {
            Utility.setTextView((TextView) view.findViewById(R.id.contact_details),
                    getString(R.string.contact_default_text));
            emailAddress[0] = getString (R.string.contact_default_email);
        }

        // set up intent for send email action
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text));

        ((FloatingActionButton)view.findViewById(R.id.contact_fab)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
                    }
                });
    }
}

