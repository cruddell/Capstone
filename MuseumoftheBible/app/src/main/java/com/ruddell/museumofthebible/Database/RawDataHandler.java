/*
 * Copyright (c) 2015. Museum of the Bible
 */

/*
 * Heavily modified derivative of:
 * com.google.samples.apps.iosched.io.ConferenceDataHandler.java
 *
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

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.ruddell.museumofthebible.Database.Handlers.BookHandler;
import com.ruddell.museumofthebible.Database.Handlers.VerseHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Helper class to parse and process Bible data and
 * import the results into our application's Content Provider.
 */
public class RawDataHandler {
    private static final String TAG = "RawDataHandler";
    private static final boolean DEBUG = false;


    // Shared preferences key under which we store the timestamp
    // that corresponds to the data in our bookName provider.
    private static final String SP_KEY_DATA_TIMESTAMP = "data_timestamp";

    // Default timestamp to use when we are missing timestamp data
    // (which means our data is really old or nonexistent)
    private static final String DEFAULT_TIMESTAMP = "Sat, 1 Jan 2000 00:00:00 GMT";

    private static final String DATA_KEY_BOOK = "books";
    private static final String DATA_KEY_VERSE = "verses";


    private static final String[] DATA_KEYS_IN_ORDER = {
            DATA_KEY_BOOK,
            DATA_KEY_VERSE
    };

    Context mContext = null;

    // Handlers for each data entity:
    BookHandler mBookHandler = null;
    VerseHandler mVerseHandler = null;

    // Convenience map that maps the key name to its corresponding handler
    HashMap<String, JSONHandler> mHandlerForKey = new HashMap<String, JSONHandler>();

    public RawDataHandler(Context context) {
        mContext = context;
    }

    /**
     * Parses the json data and imports the data into the bookName provider.
     *
     * @param jsonDataFiles The collection of JSON data files to parse and import.
     * @param dataTimeStamp The timestamp of the data. This should be in RFC1123 format.
     * @param downloadsAllowed Whether or not we are supposed to download data from the internet if needed.
     * @throws java.io.IOException If there is a problem parsing the data.
     */
    public void applyJsonData(String[] jsonDataFiles,
                              String dataTimeStamp,
                              boolean downloadsAllowed) throws IOException {
        int jsonFileCount = jsonDataFiles.length;
        if(DEBUG) Log.d(TAG, "applyJsonData() -> Data sets: " + jsonFileCount + " (" + dataTimeStamp + ")");

        // Create handlers for each data type;
        mHandlerForKey.put(DATA_KEY_BOOK, mBookHandler = new BookHandler(mContext));
        mHandlerForKey.put(DATA_KEY_VERSE, mVerseHandler = new VerseHandler(mContext));

        // Process the JSON data.
        // Invoke appropriate handlers to process the JSON data elements.
        for (int i = 0; i < jsonFileCount; i++) {
            if(DEBUG) Log.d(TAG, "Processing data set: " + (i + 1) + " of " + jsonFileCount);
            processDataBody(jsonDataFiles[i]);
        }

        // produce the necessary bookName provider operations
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<ContentProviderOperation>();
        for (String key : DATA_KEYS_IN_ORDER) {
            if(DEBUG) Log.d(TAG, "Adding bookName provider operations for: " + key);
            mHandlerForKey.get(key).addContentProviderOperations(batchOperations);
            if(DEBUG) Log.d(TAG, "Content provider operations updated size: " + batchOperations.size());
        }
        if(DEBUG) Log.d(TAG, "Total bookName provider operations: " + batchOperations.size());

        // finally, push the changes into the Content Provider
        if(DEBUG) Log.d(TAG, "Applying " + batchOperations.size() + " bookName provider operations.");
        try {
            int operations = batchOperations.size();
            if (operations > 0) {
                mContext.getContentResolver().applyBatch(BibleContract.CONTENT_AUTHORITY, batchOperations);
            }
            if(DEBUG) Log.d(TAG, "Successfully applied " + operations + " bookName provider operations.");
        } catch (RemoteException ex) {
            Log.e(TAG, "RemoteException while applying bookName provider operations.");
            throw new RuntimeException("Error executing bookName provider batch operation", ex);
        } catch (OperationApplicationException ex) {
            Log.e(TAG, "OperationApplicationException while applying bookName provider operations.");
            throw new RuntimeException("Error executing bookName provider batch operation", ex);
        }

        // notify all top-level paths
        if(DEBUG) Log.d(TAG, "Notifying changes on all top-level paths on Content Resolver.");
        ContentResolver resolver = mContext.getContentResolver();
        for (String path : BibleContract.TOP_LEVEL_PATHS) {
            Uri uri = BibleContract.BASE_CONTENT_URI.buildUpon().appendPath(path).build();
            resolver.notifyChange(uri, null);
        }

        // Update our data's timestamp
        setDataTimeStamp(dataTimeStamp);
        if(DEBUG) Log.d(TAG, "Completed applying exhibit data.");
    }

    /**
     * Processes the JSON data and calls the appropriate data type handlers for each key.
     *
     * @param jsonData The JSON data to process
     * @throws java.io.IOException If parsing JSON data generates an error.
     */
    private void processDataBody(String jsonData) throws IOException {
        JsonReader reader = new JsonReader(new StringReader(jsonData));
        JsonParser parser = new JsonParser();
        try {
            // Don't fail on common JSON syntax errors
            reader.setLenient(true);

            // Begin parsing JSON file
            reader.beginObject();  // consume initial JSON braces

            while (reader.hasNext()) {
                // Keys
                String key = reader.nextName();
                if (mHandlerForKey.containsKey(key)) {
                    // give the parsed JsonElement to the corresponding handler
                    mHandlerForKey.get(key).process(parser.parse(reader));
                } else {
                    Log.w(TAG, "Skipping unknown key in json data: " + key);
                    reader.skipValue();
                }
            }
            reader.endObject(); // consume final JSON braces
        } finally {
            reader.close();
        }
    }

    // Returns the timestamp of the data we have in the bookName provider.
    public String getDataTimeStamp() {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getString(
                SP_KEY_DATA_TIMESTAMP, DEFAULT_TIMESTAMP);
    }

    // Sets the timestamp of the data we have in the bookName provider.
    public void setDataTimeStamp(String timestamp) {
        Log.d(TAG, "Setting data timestamp to: " + timestamp);
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(
                SP_KEY_DATA_TIMESTAMP, timestamp).commit();
    }


}
