package com.ruddell.museumofthebible.Widget;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ChristopherRuddell, Museum of the Bible, on 8/3/16.
 */
public class WidgetUtil {
    private static final boolean DEBUG_LOG = true;
    private static final String TAG = "WidgetUtil";
    private static final String PREF_VERSE_OF_DAY = "verse_of_day";

    public WidgetUtil() {

    }

    public static String getVerseOfDay(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_VERSE_OF_DAY, "");
    }

    public static void setVerseOfDay(final Application application, String value) {
        Context context = application.getApplicationContext();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_VERSE_OF_DAY, value).apply();

        //update widget
        Intent intent = new Intent(context,MOTBWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int ids[] = AppWidgetManager.getInstance(application).getAppWidgetIds(new ComponentName(application, MOTBWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        context.sendBroadcast(intent);
    }
}
