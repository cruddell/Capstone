package com.ruddell.museumofthebible.Database;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ruddell.museumofthebible.utils.PrefUtils;


/**
 * Created by chris1 on 7/9/15.
 */
public class DatabaseUpdater {
    private static String TAG = "DatabaseUpdater";
    public static boolean hasStartedUpdated = false;
    private static Context mContext;

    public DatabaseUpdater(Context context) {
        mContext = context;
    }

    public static void updateDatabaseIfNeeded(Context context) {
        if (mContext==null) mContext = context;
        if (!hasStartedUpdated && !PrefUtils.isDataBootstrapDone(mContext)) {
            Log.d(TAG, "Initial data bootstrap is required.  Starting bootstrap process.");
            hasStartedUpdated = true;
            Intent serviceIntent = new Intent(mContext, DatabaseService.class);
            mContext.startService(serviceIntent);
        }
    }

    public static void forceUpdate(Context context) {
        PrefUtils.setDataBootstrapComplete(context, false);
        hasStartedUpdated = false;
        DatabaseUpdater.updateDatabaseIfNeeded(context);

    }
}



