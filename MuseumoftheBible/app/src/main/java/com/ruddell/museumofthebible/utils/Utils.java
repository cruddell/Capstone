package com.ruddell.museumofthebible.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by ChristopherRuddell, Museum of the Bible, on 6/21/16.
 */

public class Utils {

    public static void closeKeyboard(Activity context) {
        View currentFocus = context.getCurrentFocus();
        if (currentFocus!=null){
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager)context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void runOnUiThread(Context context, Runnable runnable) {
        Handler mainHandler = new Handler(context.getApplicationContext().getMainLooper());
        mainHandler.post(runnable);
    }

}
