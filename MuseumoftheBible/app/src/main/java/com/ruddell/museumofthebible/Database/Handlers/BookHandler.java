/*
 * Copyright (c) 2015. Museum of the Bible
 */

package com.ruddell.museumofthebible.Database.Handlers;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.ruddell.museumofthebible.Database.BibleContract;
import com.ruddell.museumofthebible.Database.JSONHandler;
import com.ruddell.museumofthebible.Database.models.BibleBook;


import java.util.ArrayList;
import java.util.HashMap;


/**
 * POI (Point Of Interest) handler for parsing JSON data structures
 */
public class BookHandler extends JSONHandler {
    private static final String TAG = "BookHandler";
    private static final boolean DEBUG = false;

    private HashMap<String, BibleBook> mBooks = new HashMap<String, BibleBook>();
    
    public BookHandler(Context context) {
        super(context);
        // mDefaultPOIColor = mContext.getResources().getColor(R.color.default_session_color);
    }

    /** update the list with all bookName provider operations necessary to update POI information */
    @Override
    public void addContentProviderOperations(ArrayList<ContentProviderOperation> list) {
        Uri uri = BibleContract.Books.CONTENT_URI;

        // delete and reinsert POIs to simplify our update process
        list.add(ContentProviderOperation.newDelete(uri).build());

        for (BibleBook bibleBook : mBooks.values()) {
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(uri);
            builder.withValue(BibleContract.Books.BOOK_ID, bibleBook._ID);
            builder.withValue(BibleContract.Books.SHORT_NAME, bibleBook.short_name);
            builder.withValue(BibleContract.Books.LONG_NAME, bibleBook.long_name);

            list.add(builder.build());
        }
    }


    @Override
    public void process(JsonElement element) {
        if(DEBUG) Log.i(TAG, "process() JSON element: BibleBook");
        for(BibleBook bibleBook : new Gson().fromJson(element, BibleBook[].class)){
            mBooks.put(bibleBook._ID,bibleBook);
        }
    }

}
