package com.ruddell.museumofthebible.Bible;

import android.database.Cursor;
import android.util.Log;

import com.ruddell.museumofthebible.Database.BibleContract;
import com.ruddell.museumofthebible.Database.BibleProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample bookName for user interfaces created by
 * Android template wizards.
 * <p/>
 */
public class BibleBookHelper {
    private static final boolean DEBUG = true;
    private static final String TAG = "BibleBookHelper";

    /**
     * An array of book items.
     */
    public static final List<BibleBookItem> ITEMS = new ArrayList<BibleBookItem>();

    /**
     * A map of book items, by ID.
     */
    public static final Map<String, BibleBookItem> ITEM_MAP = new HashMap<String, BibleBookItem>();

    static {
        //query database in background
        new Thread(new Runnable() {
            @Override
            public void run() {
                BibleProvider provider = new BibleProvider();
                Cursor cursor = provider.query(BibleContract.Books.CONTENT_URI,BibleContract.Books.PROJECTION_ALL,null,null,BibleContract.Books.DEFAULT_SORT);
                if (DEBUG) Log.d(TAG, "query database(" + BibleContract.Books.CONTENT_URI + ") = " + cursor.getCount());
                while (cursor.moveToNext()) {
                    String shortName = cursor.getString(BibleContract.Books.COLUMN_SHORT_NAME);
                    String longName = cursor.getString(BibleContract.Books.COLUMN_LONG_NAME);
                    String id = cursor.getString(BibleContract.Books.COLUMN_BOOK_ID);
                    BibleBookItem item = new BibleBookItem(id,shortName,longName);
                    addItem(item);
                }
            }
        }).start();


    }

    private static void addItem(BibleBookItem item) {
        if (DEBUG) Log.d(TAG, "addItem(" + item.longName + ")");
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }


    /**
     * An item representing a book of the Bible.
     */
    public static class BibleBookItem {
        public final String id;
        public final String shortName;
        public final String longName;

        public BibleBookItem(String id, String shortName, String longName) {
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
