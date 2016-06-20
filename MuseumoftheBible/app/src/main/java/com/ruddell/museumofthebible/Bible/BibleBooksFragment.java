package com.ruddell.museumofthebible.Bible;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruddell.museumofthebible.R;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class BibleBooksFragment extends Fragment {
    private static final String TAG = "BibleBooksFragment";
    private static final boolean DEBUG = true;

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BibleBooksFragment() {
    }


    @SuppressWarnings("unused")
    public static BibleBooksFragment newInstance(int columnCount, OnListFragmentInteractionListener listener) {
        BibleBooksFragment fragment = new BibleBooksFragment();
        fragment.mListener = listener;
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_biblebooks_list, container, false);

        // Set the adapter
        Context context = mRecyclerView.getContext();
        if (mColumnCount <= 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        refreshData();
//        mRecyclerView.setAdapter(new MyBibleBooksRecyclerViewAdapter(MyBibleBooksRecyclerViewAdapter.ITEMS, mListener));
        return mRecyclerView;
    }

    public void refreshData() {
        if (DEBUG) Log.d(TAG, "refreshData");
        MyBibleBooksRecyclerViewAdapter.getItems(new MyBibleBooksRecyclerViewAdapter.BibleQueryListener() {
            @Override
            public void databaseQueried() {
                mRecyclerView.setAdapter(new MyBibleBooksRecyclerViewAdapter(MyBibleBooksRecyclerViewAdapter.ITEMS, mListener));
                mRecyclerView.addOnItemTouchListener(new SimpleItemTouchListener(getActivity(), new SimpleItemTouchListener.Listener() {
                    @Override
                    public void onItemClicked(final RecyclerView.ViewHolder viewHolder) {
                        MyBibleBooksRecyclerViewAdapter.ViewHolder vh = (MyBibleBooksRecyclerViewAdapter.ViewHolder)viewHolder;
                        if (DEBUG) Log.d(TAG, "onItemClicked:" + vh.mItem.longName);
                        mListener.onListFragmentInteraction(vh.mItem);
                    }
                }));
            }
        });

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(MyBibleBooksRecyclerViewAdapter.BibleBookItem item);
    }
}
