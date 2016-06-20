package com.ruddell.museumofthebible.Bible;

import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ruddell.museumofthebible.Database.BibleContract;
import com.ruddell.museumofthebible.Database.BibleProvider;
import com.ruddell.museumofthebible.R;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class BibleChapterFragment extends Fragment {
    private static final String TAG = "BibleChapterFragment";
    private static final boolean DEBUG = true;

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private String mBookId;
    private String mBookName;
    private TextView mBookNameLabel;
    private MyBibleChapterRecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ScrollView mScrollView;
    private TextView mBibleText;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BibleChapterFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static BibleChapterFragment newInstance(int columnCount, OnListFragmentInteractionListener listener) {
        BibleChapterFragment fragment = new BibleChapterFragment();
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

    public void showChaptersForBook(MyBibleBooksRecyclerViewAdapter.BibleBookItem book) {
        if (DEBUG) Log.d(TAG,"showChaptersForBook:" + book.longName);
        mBookId = book.id;
        mBookName = book.longName;
        mBookNameLabel.setText(mBookName);
        mAdapter.updateQuery(mBookId);
    }

    public void refreshAdapter() {
        if (DEBUG) Log.d(TAG, "refreshAdapter");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mColumnCount = mAdapter.columnsInBook()>10 ? 2 : 1;
                if (mColumnCount <= 1) {
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                } else {
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mColumnCount));
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewContainer = inflater.inflate(R.layout.fragment_biblechapter_list, container, false);
        mBookNameLabel = (TextView) viewContainer.findViewById(R.id.bookName);
        mScrollView = (ScrollView) viewContainer.findViewById(R.id.scrollView);
        mBibleText = (TextView) viewContainer.findViewById(R.id.bibleText);
        mBookNameLabel.setText(mBookName);
        View listView = viewContainer.findViewById(R.id.list);

        // Set the adapter
        if (listView instanceof RecyclerView) {
            Context context = listView.getContext();
            mRecyclerView = (RecyclerView) listView;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mAdapter = new MyBibleChapterRecyclerViewAdapter(mBookId, mListener);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.addOnItemTouchListener(new SimpleItemTouchListener(getActivity(), new SimpleItemTouchListener.Listener() {
                @Override
                public void onItemClicked(final RecyclerView.ViewHolder viewHolder) {
                    MyBibleChapterRecyclerViewAdapter.ViewHolder vh = (MyBibleChapterRecyclerViewAdapter.ViewHolder)viewHolder;
                    if (DEBUG) Log.d(TAG, "onItemClicked:" + vh.mItem.chapterName);
                    mListener.onListFragmentInteraction(vh.mItem);
                }
            }));
        }
        return viewContainer;
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
        void onListFragmentInteraction(BibleChapterHelper.BibleChapterItem item);
        void onDataUpdated();
    }

    public void loadTextForChapter(String chapter) {
        if (DEBUG) Log.d(TAG,"loadTextForChapter:" + chapter);
        BibleProvider provider = new BibleProvider();
        String[] selectionArgs = {mBookId,chapter};
        Cursor cursor = provider.query(BibleContract.Verses.CONTENT_URI,BibleContract.Verses.PROJECTION_ALL,"book=? AND chapter=?",selectionArgs,BibleContract.Verses.DEFAULT_SORT);
        String verseText = "";
        while (cursor.moveToNext()) {
            verseText += cursor.getString(BibleContract.Verses.COLUMN_VERSE_TEXT) + " ";
        }
        mBibleText.setText(verseText);
        fadeContent(true);
    }

    public void fadeContent(final boolean showBibleText) {
        mScrollView.setVisibility(showBibleText ? View.VISIBLE : View.GONE);
        ValueAnimator animator = ValueAnimator.ofInt(0,100);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = ((int) animation.getAnimatedValue()) / 100.f;
                if (showBibleText) {
                    mRecyclerView.setAlpha(-1f * alpha);
                    mScrollView.setAlpha(alpha);
                } else {
                    mRecyclerView.setAlpha(alpha);
                    mScrollView.setAlpha(-1f * alpha);
                }
            }
        });
        animator.start();
    }

    public boolean bibleTextIsShown() {
        return mScrollView.getAlpha()>0 && mScrollView.getVisibility()==View.VISIBLE;
    }
}
