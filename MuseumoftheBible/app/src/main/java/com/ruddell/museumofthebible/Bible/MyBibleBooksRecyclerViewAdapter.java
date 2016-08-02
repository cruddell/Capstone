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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link BibleBookItem} and makes a call to the
 * specified {@link BibleBooksFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyBibleBooksRecyclerViewAdapter extends RecyclerView.Adapter<MyBibleBooksRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "BooksAdapter";
    private static final boolean DEBUG = true;
    private final List<BibleBookItem> mValues;
    private final BibleBooksFragment.OnListFragmentInteractionListener mListener;
    public static final List<BibleBookItem> ITEMS = new ArrayList<BibleBookItem>();
    public static final Map<Integer, BibleBookItem> ITEM_MAP = new HashMap<Integer, BibleBookItem>();

    public MyBibleBooksRecyclerViewAdapter(List<BibleBookItem> items, BibleBooksFragment.OnListFragmentInteractionListener listener) {
        if (DEBUG) Log.d(TAG, "constructor");
        if (DEBUG) Log.d(TAG, items.toString());
        mValues = items;
        mListener = listener;
    }

    public static void getItems(BibleQueryListener listener) {
        ITEMS.clear();
        BibleProvider provider = new BibleProvider();
        Cursor cursor = provider.query(BibleContract.Books.CONTENT_URI,BibleContract.Books.PROJECTION_ALL,null,null,BibleContract.Books.DEFAULT_SORT);
        if (DEBUG) Log.d(TAG, "query database(" + BibleContract.Books.CONTENT_URI + ") = " + cursor.getCount());
        while (cursor.moveToNext()) {
            String shortName = cursor.getString(BibleContract.Books.COLUMN_SHORT_NAME);
            String longName = cursor.getString(BibleContract.Books.COLUMN_LONG_NAME);
            int id = cursor.getInt(BibleContract.Books.COLUMN_BOOK_ID);
            BibleBookItem item = new BibleBookItem(id,shortName,longName);
            addItem(item);
        }
        listener.databaseQueried();
    }

    private static void addItem(BibleBookItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (DEBUG) Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bible_book_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mBookNameLabel.setText(mValues.get(position).longName);

//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    if (DEBUG) Log.d(TAG, "item clicked:" + holder.mBookNameLabel);
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
        public final TextView mBookNameLabel;
        public BibleBookItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mBookNameLabel = (TextView) view.findViewById(R.id.bookNameLabel);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBookNameLabel.getText() + "'";
        }
    }

    public interface BibleQueryListener {
        void databaseQueried();
    }


    /**
     * An item representing a book of the Bible.
     */
    public static class BibleBookItem {
        public final int id;
        public final String shortName;
        public final String longName;

        public BibleBookItem(int id, String shortName, String longName) {
            this.id = id;
            this.shortName = shortName;
            this.longName = longName;
        }

        @Override
        public String toString() {
            return shortName;
        }
    }
}
