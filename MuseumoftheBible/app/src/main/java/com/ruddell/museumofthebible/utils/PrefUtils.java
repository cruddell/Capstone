/*
 * Copyright (c) 2015. Museum of the Bible
 */

package com.ruddell.museumofthebible.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.UUID;

public class PrefUtils {
    private static final String TAG = "PrefUtils";

    /** Boolean indicating if data bootstrap (eg default database setup) has been performed  */
    public static final String PREF_DATA_BOOTSTRAP_COMPLETE = "pref_data_bootstrap_complete";
    public static final String PREF_TICKET_AVAILABLE = "pref_ticket_available";
    public static final String PREF_FEATURED_EXHIBITS = "pref_featured_exhibits";
    public static final String PREF_VERSE_OF_THE_DAY = "pref_verseOfTheDay";
    public static final String PREF_SENT_TOKEN_TO_SERVER = "pref_sent_token_to_server";
    public static final String PREF_DEVICE_ID = "pref_deviceId";

    public static boolean isDataBootstrapDone(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_DATA_BOOTSTRAP_COMPLETE, false);
    }

    public static void setDataBootstrapComplete(final Context context, boolean isComplete) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_DATA_BOOTSTRAP_COMPLETE, isComplete).apply();
    }

    public static void setTicketAvailable(final Context context, boolean isAvailable) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_TICKET_AVAILABLE, isAvailable).apply();
    }

    public static boolean isTicketAvailable(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_TICKET_AVAILABLE, false);
    }

    public static void setFeaturedExhibits(final Context context, String exhibitJsonArray) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_FEATURED_EXHIBITS, exhibitJsonArray).apply();
    }

    public static boolean getPrefVerseOfTheDay(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_VERSE_OF_THE_DAY, true);
    }

    public static void setPrefVerseOfTheDay(final Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_VERSE_OF_THE_DAY, value).apply();
    }

    public static boolean getPrefSentTokenToServer(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_SENT_TOKEN_TO_SERVER, false);
    }

    public static void setPrefSentTokenToServer(final Context context, boolean value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_SENT_TOKEN_TO_SERVER, value).apply();
    }

    public static String getPrefDeviceId(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String deviceId = sp.getString(PREF_DEVICE_ID, "");
        if (deviceId.length()<1) {
            //need new device Id
            Log.e(TAG,"creating new device id");
            deviceId = UUID.randomUUID().toString();
            setPrefDeviceId(context, deviceId);
        }

        return deviceId;
    }

    public static void setPrefDeviceId(final Context context, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_DEVICE_ID, value).apply();
        Log.d(TAG,"setPrefDeviceId:" + value);
    }

    public static JSONArray getFeaturedExhibits(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = sp.getString(PREF_FEATURED_EXHIBITS, "[]");
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

}
