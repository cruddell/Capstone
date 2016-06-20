/*
 * Copyright (c) 2015. Museum of the Bible
 *
 * Modification History
 * -Imported core functions from com.google.samples.apps.iosched.provider.ScheduleProvider
 * -Updated data access routines to pair with Exhibit data elements
 * -Significant method updates for application specific customization
 */

/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ruddell.museumofthebible.Database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.ruddell.museumofthebible.utils.SelectionBuilder;

import java.util.Arrays;
import java.util.HashSet;

public class BibleProvider extends ContentProvider {
    private final static String TAG = "BibleProvider";
    private final static boolean DEBUG = true;

    // database
    private BibleDatabase mDatabase;
    private static final String BOOKS_TABLE = BibleDatabase.Tables.BOOKS;
    private static final String VERSES_TABLE = BibleDatabase.Tables.VERSES;

    // UriMatcher and helper constants
    private static final UriMatcher sURIMatcher;

    // query items
    private static final int BOOKS = 100;
    private static final int VERSES = 101;

    private Context mContext;


//        public static final long INVALID = -1;

    static {
        sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BibleContract.CONTENT_AUTHORITY;

        sURIMatcher.addURI(authority, "books", BOOKS);                  // returns list of books
        sURIMatcher.addURI(authority, "verses", VERSES);

    }


    public BibleProvider(BibleDatabase mDatabase) {
        this.mDatabase = mDatabase;
    }

    public BibleProvider(Context context) {
        mContext = context;
        this.mDatabase = BibleDatabase.getInstance(context);
    }

    public BibleProvider() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate()");
        if (mContext == null) mContext = getContext();
        mDatabase = BibleDatabase.getInstance(mContext);
        return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getType(Uri uri) {
        final int match = sURIMatcher.match(uri);
        switch (match) {
            case BOOKS:
                if (DEBUG) Log.d(TAG, "getType(BOOKS)");
                return BibleContract.Books.CONTENT_TYPE;
            case VERSES:
                if (DEBUG) Log.d(TAG, "getType(VERSES)");
                return BibleContract.Verses.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {

        if (DEBUG)
            Log.d(TAG, "query(uri: " + uri + ", COLUMNS: " + Arrays.toString(projection) + ", selection:" + selection + ", args:" + selectionArgs + ")");

        if (mDatabase==null) mDatabase = BibleDatabase.getInstance(mContext);
        final SQLiteDatabase db = mDatabase.getReadableDatabase();
        final int uriMatch = sURIMatcher.match(uri);

        // Use SelectionBuilder to handle general queries matching uri
        final SelectionBuilder builder = buildExpandedSelection(uri, uriMatch);


        boolean distinct = true;  //true if you want each row to be unique, false otherwise

        Cursor cursor = builder.
                where(selection, selectionArgs).
                query(db, distinct, projection, sortOrder, null);

        if (DEBUG) Log.d(TAG,"query (SELECT FORM " + cursor.toString());

        Context context = getContext();
        if (context != null)
            cursor.setNotificationUri(context.getContentResolver(), uri);

        return cursor;



    }

    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {

        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
            case BOOKS:
                return builder.table(BibleDatabase.Tables.BOOKS);
            case VERSES:
                return builder.table(BibleDatabase.Tables.VERSES);
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Uri insert(
            Uri uri,
            ContentValues values) {

        if (DEBUG)
            Log.d(TAG, "insert(" + sURIMatcher.match(uri) + ": uri=" + uri + ", Values=" + values.toString() + ")");


        // Support insert operations
        final SQLiteDatabase db = mDatabase.getWritableDatabase();

        switch (sURIMatcher.match(uri)) {
            case BOOKS: {
                // Validate columns exist for requested bookName values
                checkColumns(BOOKS_TABLE, values.keySet().toArray(new String[values.size()]));
                db.insertOrThrow(BibleDatabase.Tables.BOOKS, null, values);
                notifyChange(uri);
                return BibleContract.Books.buildItemUri(values.getAsString(BibleContract.Books.BOOK_ID));
            }
            case VERSES:
                // Validate columns exist for requested bookName values
                checkColumns(VERSES_TABLE, values.keySet().toArray(new String[values.size()]));
                db.insertOrThrow(BibleDatabase.Tables.VERSES, null, values);
                notifyChange(uri);
                return BibleContract.Verses.buildItemUri(values.getAsString(BibleContract.Verses.VERSE_ID));

            default:
                throw new UnsupportedOperationException("Unknown Insert URI: " + uri);
        }
    }

    public Uri insertOnConflict(Uri uri, ContentValues values, int conflictOption) {
        // Support insert operations
        final SQLiteDatabase db = mDatabase.getWritableDatabase();

        switch (sURIMatcher.match(uri)) {
            case BOOKS:
                db.insertWithOnConflict(BibleDatabase.Tables.BOOKS, null, values, conflictOption);
                notifyChange(uri);
                return BibleContract.Books.buildItemUri(values.getAsString(BibleContract.Books.BOOK_ID));
            case VERSES:
                db.insertWithOnConflict(BibleDatabase.Tables.VERSES, null, values, conflictOption);
                notifyChange(uri);
                return BibleContract.Verses.buildItemUri(values.getAsString(BibleContract.Verses.VERSE_ID));

            default:
                throw new UnsupportedOperationException("Unknown Insert URI: " + uri);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete(uri=" + uri.getPath());
        final SQLiteDatabase db = mDatabase.getWritableDatabase();
        final int uriMatch = sURIMatcher.match(uri);
        int response = -1;
        switch (uriMatch) {
            case BOOKS:
                response = db.delete(BOOKS_TABLE, selection, selectionArgs);
                break;
            case VERSES:
                response = db.delete(VERSES_TABLE, selection, selectionArgs);
                break;
            default:
                Log.e(TAG,"Unknown URI:" + uri.toString());
        }
        notifyChange(uri);
        return response;
    }

    //not implemented as we have no need for updating the table.  If a user "unfavorites" a movie, it will simply be deleted
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private void notifyChange(Uri uri) {
        mContext.getContentResolver().notifyChange(uri, null);
    }

    // Validation: ensure our provider supports requested projection columns
    private void checkColumns(String pTableName, String[] projection) {
        String[] existing = null;

        // Set array to reflect the columns for the table
        if (pTableName.equals(BOOKS_TABLE)) {
            existing = BibleContract.Books.PROJECTION_ALL;

        } else if(pTableName.equals(VERSES_TABLE)){
            existing = BibleContract.Verses.PROJECTION_ALL;

        }

        // check requested columns against projected columns
        if ((existing != null) && (projection != null)) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> existingColumns = new HashSet<>(Arrays.asList(existing));
            // use maps to find if the existing columns contain the projected columns
            if (!existingColumns.containsAll(requestedColumns)) {
                Log.e(TAG,"Table (" + pTableName + ") does not contain all requested columns!  Looking for missing columns...");
                Log.e(TAG, "Requested Columns:" + requestedColumns.toString());
                Log.e(TAG, "Existing Columns:" + existingColumns.toString());
                for (String requestedColumn:requestedColumns) {
                    if (!existingColumns.contains(requestedColumn)) Log.e(TAG, "MISSING COLUMN:" + requestedColumn);
                }
                throw new IllegalArgumentException("Table (" + pTableName + ") does not contain all requested columns: " + Arrays.toString(projection));
            }
        }
    }
}
