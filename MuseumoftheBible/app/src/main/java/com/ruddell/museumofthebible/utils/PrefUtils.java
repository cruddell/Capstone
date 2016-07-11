/*
 * Copyright (c) 2015. Museum of the Bible
 */

package com.ruddell.museumofthebible.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;

public class PrefUtils {
    private static final String TAG = "PrefUtils";

    /** Boolean indicating if data bootstrap (eg default database setup) has been performed  */
    public static final String PREF_DATA_BOOTSTRAP_COMPLETE = "pref_data_bootstrap_complete";
    public static final String PREF_TICKET_AVAILABLE = "pref_ticket_available";
    public static final String PREF_FEATURED_EXHIBITS = "pref_featured_exhibits";

    public static boolean isDataBootstrapDone(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_DATA_BOOTSTRAP_COMPLETE, false);
    }

    public static void setDataBootstrapComplete(final Context context, boolean isComplete) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_DATA_BOOTSTRAP_COMPLETE, isComplete).commit();
    }

    public static void setTicketAvailable(final Context context, boolean isAvailable) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_TICKET_AVAILABLE, isAvailable).commit();
    }

    public static boolean isTicketAvailable(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_TICKET_AVAILABLE, false);
    }

    public static void setFeaturedExhibits(final Context context, String exhibitJsonArray) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_FEATURED_EXHIBITS, exhibitJsonArray).commit();
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
