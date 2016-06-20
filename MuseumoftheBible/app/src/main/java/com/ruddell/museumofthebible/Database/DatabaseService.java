/*
 * Copyright (c) 2015. Museum of the Bible
 */

package com.ruddell.museumofthebible.Database;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;


import com.ruddell.museumofthebible.R;
import com.ruddell.museumofthebible.utils.PrefUtils;

import java.io.IOException;

public class DatabaseService extends IntentService {
    private static String TAG = "DatabaseService";
    public static final String BOOTSTRAP_DATA_TIMESTAMP = "Mon, 1 Jun 2015 00:42:42 GMT";
    Thread mDataBootstrapThread = null;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DatabaseService(String name) {
        super(name);
    }

    public DatabaseService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (mDataBootstrapThread == null) {

        }

        Log.d(TAG, "Initial data bootstrap is required.  Starting bootstrap process.");
        Log.d(TAG, "starting data bootstrap in background.");

        mDataBootstrapThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Bootstrap thread started.");
                try {
                    // Load data from bootstrap raw resource

                    String booksJson = JSONHandler.parseResource(DatabaseService.this, R.raw.bible_books);
                    String versesJson = JSONHandler.parseResource(DatabaseService.this, R.raw.asv);

                    RawDataHandler dataHandler = new RawDataHandler(DatabaseService.this);
                    dataHandler.applyJsonData(new String[]{booksJson, versesJson}, BOOTSTRAP_DATA_TIMESTAMP, false);
//                    dataHandler.applyJsonData(new String[]{versesJson}, BOOTSTRAP_DATA_TIMESTAMP, false);

                    Log.d(TAG, "Completed data bootstrap and recording bootstrap as done.");


                    PrefUtils.setDataBootstrapComplete(DatabaseService.this, true);

                    getContentResolver().notifyChange(Uri.parse(BibleContract.CONTENT_AUTHORITY), null, false);

                } catch (IOException ex) {
                    Log.e(TAG, "IO Exception!");
                    ex.printStackTrace();
                }
                finally {
                    mDataBootstrapThread = null;
                }

            }
        });
        mDataBootstrapThread.start();

    }



    /**
     * Performs the one-time data bootstrap. This means parsing our prepackaged data
     * contained in R.raw files and populating the database.
     */
    private void performDataBootstrap() {

    }
}
