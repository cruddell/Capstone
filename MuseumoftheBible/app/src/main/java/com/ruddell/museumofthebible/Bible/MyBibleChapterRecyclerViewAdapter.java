package com.ruddell.museumofthebible.Bible;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruddell.museumofthebible.Database.BibleContract;
import com.ruddell.museumofthebible.Database.BibleProvider;
import com.ruddell.museumofthebible.R;

import java.util.ArrayList;


public class MyBibleChapterRecyclerViewAdapter extends RecyclerView.Adapter<MyBibleChapterRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "MyBibleChapterAdapter";
    private static final boolean DEBUG = true;

    private final ArrayList<BibleChapterHelper.BibleChapterItem> mValues;
    private final BibleChapterFragment.OnListFragmentInteractionListener mListener;

    //constructor
    public MyBibleChapterRecyclerViewAdapter(final String bookID, BibleChapterFragment.OnListFragmentInteractionListener listener) {
        mValues = new ArrayList();
        mListener = listener;

    }

    public void updateQuery(final String bookID) {
        if (DEBUG) Log.d(TAG,"updateQuery(" + bookID + ")");
        if (bookID=="") return;
        mValues.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                BibleProvider provider = new BibleProvider();
                String[] selectionArgs = {bookID};
                Cursor cursor = provider.query(BibleContract.Verses.CONTENT_URI,ChapterQuery.PROJECTION,BibleContract.Verses.BOOK + "=?", selectionArgs,"verses." + BibleContract.Verses.Chapter + " ASC");
                while (cursor.moveToNext()) {
                    String Chapter = cursor.getString(ChapterQuery.COLUMN_CHAPTER);
                    BibleChapterHelper.BibleChapterItem item = new BibleChapterHelper.BibleChapterItem(Chapter);
                    mValues.add(item);
                }
                if (DEBUG) Log.d(TAG,"cursor loaded:" + mValues.toString());
                mListener.onDataUpdated();
            }
        }).start();
    }

    public int columnsInBook() {
        return mValues.size();
    }


    static class ChapterQuery {
        // String array of all table columns
        public static final String[] PROJECTION =
                {
                BibleContract.Verses.Chapter
                };

        public static final int COLUMN_CHAPTER = 0;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bible_chapter_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mChapterName.setText("Chapter " + mValues.get(position).chapterName);

//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mChapterName;
        public BibleChapterHelper.BibleChapterItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mChapterName = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mChapterName.getText() + "'";
        }
    }
}
