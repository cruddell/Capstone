package com.ruddell.museumofthebible.Database.Handlers;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.ruddell.museumofthebible.Database.BibleContract;
import com.ruddell.museumofthebible.Database.JSONHandler;
import com.ruddell.museumofthebible.Database.models.BibleVerse;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by chris on 1/2/16.
 */
public class VerseHandler extends JSONHandler {
    private static final String TAG = "VerseHandler";
    private static final boolean DEBUG = false;

    private HashMap<String, BibleVerse> mVerses = new HashMap<String, BibleVerse>();

    public VerseHandler(Context context) {
        super(context);
        // mDefaultPOIColor = mContext.getResources().getColor(R.color.default_session_color);
    }

    /** update the list with all bookName provider operations necessary to update POI information */
    @Override
    public void addContentProviderOperations(ArrayList<ContentProviderOperation> list) {
        Uri uri = BibleContract.Verses.CONTENT_URI;

        // delete and reinsert POIs to simplify our update process
        list.add(ContentProviderOperation.newDelete(uri).build());

        for (BibleVerse bibleVerse : mVerses.values()) {
            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(uri);
            builder.withValue(BibleContract.Verses.VERSE_ID, bibleVerse._ID);
            builder.withValue(BibleContract.Verses.VerseNumber, bibleVerse.Verse);
            builder.withValue(BibleContract.Verses.BOOK, bibleVerse.Book);
            builder.withValue(BibleContract.Verses.Chapter, bibleVerse.Chapter);
            builder.withValue(BibleContract.Verses.VerseText, bibleVerse.Text);

            list.add(builder.build());
        }
    }


    @Override
    public void process(JsonElement element) {
        if(DEBUG) Log.i(TAG, "process() JSON element: BibleVerse");
        for(BibleVerse bibleVerse : new Gson().fromJson(element, BibleVerse[].class)){
            mVerses.put(bibleVerse._ID, bibleVerse);
        }
    }
}
