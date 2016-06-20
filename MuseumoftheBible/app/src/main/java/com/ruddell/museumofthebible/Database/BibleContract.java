package com.ruddell.museumofthebible.Database;

/**
 * Created by David Butts on 1/16/15.
 * Contract class for interacting with {@link ExhibitProvider}.
 */

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.ruddell.museumofthebible.BuildConfig;


public class BibleContract {
    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID+".Database.BibleProvider";
    private static final boolean DEBUG_LOG = false;
    private static final String TAG = "BibleContract";

    public static final Uri BASE_CONTENT_URI = Uri.parse("bookName://" + CONTENT_AUTHORITY);

    private static final String PATH_BOOKS  = "books";
    private static final String PATH_VERSES = "verses";


    public static final String[] TOP_LEVEL_PATHS = {
            PATH_BOOKS,
            PATH_VERSES
    };

    interface BookColumns {
        String BOOK_ID = "id";
        String SHORT_NAME = "short_name";
        String LONG_NAME = "long_name";
    }

    interface VerseColumns {
        String VERSE_ID = "id";
        String BOOK = "book";
        String Chapter = "chapter";
        String VerseNumber = "verse";
        String VerseText = "text";
    }

    public static class Books implements BookColumns, BaseColumns {
        /**
         * Content URI identifying Items table data
         */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOOKS).build();
        public static final Uri CONTENT_FILTER_URI = Uri.withAppendedPath(CONTENT_URI, "filter");

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.digitaldocent.bible";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.digitaldocent.bible";

        // Default "ORDER BY" clause (Item barcode in descending order)
        public static final String DEFAULT_SORT = "books." + BookColumns.BOOK_ID + " ASC";


        // String array of all table columns
        public static final String[] PROJECTION_ALL =
                {BaseColumns._ID,
                        BOOK_ID,
                        SHORT_NAME,
                        LONG_NAME
                };

        public static final int COLUMN_BASE_ID = 0;
        public static final int COLUMN_BOOK_ID = 1;
        public static final int COLUMN_SHORT_NAME = 2;
        public static final int COLUMN_LONG_NAME = 3;




        public static Uri buildItemUri(String itemId) {
            return CONTENT_URI.buildUpon().appendPath(itemId).build();
        }

        public static String getItemId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    public static class Verses implements VerseColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VERSES).build();
        public static final Uri CONTENT_FILTER_URI = Uri.withAppendedPath(CONTENT_URI, "filter");

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.digitaldocent.bible";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.digitaldocent.bible";

        // Default "ORDER BY" clause (Item barcode in descending order)
        public static final String DEFAULT_SORT = "verses." + VerseColumns.VERSE_ID + " ASC";


        // String array of all table columns
        public static final String[] PROJECTION_ALL =
                {BaseColumns._ID,
                        VERSE_ID,
                        BOOK,
                        Chapter,
                        VerseNumber,
                        VerseText
                };

        public static final int COLUMN_BASE_ID = 0;
        public static final int COLUMN_VERSE_ID = 1;
        public static final int COLUMN_BOOK = 2;
        public static final int COLUMN_CHAPTER = 3;
        public static final int COLUMN_VERSE_NUMBER = 4;
        public static final int COLUMN_VERSE_TEXT = 5;

        public static Uri buildItemUri(String itemId) {
            return CONTENT_URI.buildUpon().appendPath(itemId).build();
        }

        public static String getItemId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }


}
