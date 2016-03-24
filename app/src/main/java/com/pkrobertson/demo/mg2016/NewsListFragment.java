package com.pkrobertson.demo.mg2016;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class NewsListFragment extends Fragment {
    private static final String LOG_TAG = NewsListFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "news_list";

    public static final String DEFAULT = "default";

    private static final String ARG_ACTION = "action";

    private OnFragmentInteractionListener mListener;
	
    private NewsListAdapter mNewsListAdapter;
    private ListView        mNewsListView;
    private TextView        mEmptyTextView;

    private List<NewsItem> mNewsItemList;

    private String mAction = null;

    // TODO: Rename and change types of parameters
    public static NewsListFragment newInstance(String action) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACTION, action);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NewsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mAction = getArguments().getString(ARG_ACTION);
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mNewsItemList = NewsItem.getNewsItemList();

        Log.d(LOG_TAG, "onCreateView() number of items ==> " + String.valueOf(mNewsItemList.size()));
        View fragmentView = inflater.inflate(R.layout.fragment_news_list, container, false);

        mEmptyTextView = (TextView) fragmentView.findViewById(R.id.empty_news_view);

        mNewsListView = (ListView) fragmentView.findViewById(R.id.news_list_view);
        mNewsListAdapter = new NewsListAdapter(getActivity(), R.layout.news_list_item, mNewsItemList);
        mNewsListView.setAdapter(mNewsListAdapter);
        mNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mListener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    NewsItem item = mNewsItemList.get(position);
                    mListener.onFragmentInteraction(item);
                }
            }
        });

        return fragmentView;
    }

    @Override
    public void onResume () {
        super.onResume();

        Utility.updateActionBarTitle(getActivity(), getString(R.string.drawer_news));

        if (mNewsListAdapter != null) {
            //mNewsListAdapter.clear();
            mNewsItemList = NewsItem.getNewsItemList();
            //mNewsListAdapter.addAll (mNewsItemList);
            mNewsListAdapter.notifyDataSetChanged();
            mEmptyTextView.setVisibility(
                    (mNewsItemList.size() > 0) ? View.INVISIBLE : View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(NewsItem selectedNewsItem);
    }

}
