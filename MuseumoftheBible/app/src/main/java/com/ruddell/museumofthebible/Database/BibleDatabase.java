package com.ruddell.museumofthebible.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chris on 1/1/16.
 */
public class BibleDatabase extends SQLiteOpenHelper {
    private static final String TAG = "BibleDatabase";
    private static final boolean DEBUG_LOG = true;

    private static String DATABASE_NAME = "kjv.db";

    private boolean mDidInit = false;
    private DatabaseListener mListener;
    private boolean mDidCreate = false;



    // Database version information
    private static final int DATABASE_VERSION = 1;

    private static BibleDatabase mInstance = null;

    public static BibleDatabase getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (mInstance == null) {
            mInstance = new BibleDatabase(context.getApplicationContext());
        }
        return mInstance;
    }

    public void setListener(DatabaseListener listener) {
        mListener = listener;
    }

    public void changeDatabase(Context context, String dbName, DatabaseListener listener) {
        if (DEBUG_LOG) Log.d(TAG,"changeDatabase(" + dbName + ")");
        mInstance = null;
        DATABASE_NAME = dbName;
        mInstance = new BibleDatabase(context.getApplicationContext());
        mInstance.setListener(listener);
        mInstance.getReadableDatabase();
        if (DEBUG_LOG) Log.d(TAG,"finished change database");
        if (mInstance.mDidCreate && mInstance.mListener!=null) mInstance.mListener.onDatabaseCreated();
        if (mInstance.mListener!=null) mInstance.mListener.onDatabaseReady();
    }


    public BibleDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if (DEBUG_LOG) Log.d(TAG, "database initialized with version:" + DATABASE_VERSION + " (" + this.getReadableDatabase().getVersion() + ")");

    }


    /* Supported Tables (define table strings in one place) */
    interface Tables {
        String BOOKS = "books";
        String VERSES = "verses";


        List<String> TABLE_LIST = new ArrayList<String>(
                Arrays.asList(new String[]{BOOKS,VERSES}));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate() checkpoint");

        db.execSQL("CREATE TABLE " + Tables.BOOKS + " ("
                        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + BibleContract.BookColumns.BOOK_ID + " INT,"
                        + BibleContract.BookColumns.SHORT_NAME + " TEXT,"
                        + BibleContract.BookColumns.LONG_NAME + " TEXT,"
                        + "UNIQUE (" + BibleContract.BookColumns.BOOK_ID + ") ON CONFLICT REPLACE);"
        );

        db.execSQL("CREATE TABLE " + Tables.VERSES + " ("
                        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + BibleContract.VerseColumns.VERSE_ID + " INT,"
                        + BibleContract.VerseColumns.BOOK + " INT,"
                        + BibleContract.VerseColumns.Chapter + " INT,"
                        + BibleContract.VerseColumns.VerseNumber + " INT,"
                        + BibleContract.VerseColumns.VerseText + " TEXT,"
                        + "UNIQUE (" + BibleContract.VerseColumns.VERSE_ID + ") ON CONFLICT REPLACE);"
        );

        mDidInit = true;
        mDidCreate = true;
        if (mListener!=null) mListener.onDatabaseCreated();
        else Log.d(TAG,"database listener is not set!");
    }

    public boolean didCreateDatabase() {
        return mDidInit;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG,"onUpgrade() from " + oldVersion + " to " + newVersion);

        //initial version - no need to perform any logic here
        //in future versions, use this to drop tables if necesary, etc
    }

    public interface DatabaseListener {
        public void onDatabaseCreated();
        public void onDatabaseReady();
    }
}
