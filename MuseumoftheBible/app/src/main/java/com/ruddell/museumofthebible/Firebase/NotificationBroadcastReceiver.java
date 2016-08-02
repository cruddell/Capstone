package com.ruddell.museumofthebible.Firebase;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.ruddell.museumofthebible.Bible.BibleActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

/**
 * Created by ChristopherRuddell, Museum of the Bible, on 8/1/16.
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {
    private static final boolean DEBUG_LOG = true;
    private static final String TAG = "NotificationBrdcstRcvr";

    public NotificationBroadcastReceiver() {

    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG,"onReceive");
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        Set<String> keys = intent.getExtras().keySet();
        for (String key : keys) {
            Log.d(TAG, "intent key found:" + key);
        }
        String messageBody = intent.getStringExtra("message");

        try {
            JSONObject jsonObject = new JSONObject(messageBody);
            final int book = jsonObject.getInt("book");
            final String bookName = jsonObject.getString("bookName");
            final int chapter = jsonObject.getInt("chapter");
            final int verse = jsonObject.getInt("verse");
            String text = jsonObject.getString("text");

            adb.setTitle("Verse of the Day");
            adb.setMessage(text);
            adb.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogInterface, final int i) {

                }
            });
            adb.setPositiveButton("Go to Bible", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogInterface, final int i) {
                    Log.d(TAG,"go to Bible:" + book + "," + chapter);
                    Intent intent1 = new Intent(context, BibleActivity.class);
                    intent1.putExtra(BibleActivity.ARG_BOOK_TO_LOAD, book);
                    intent1.putExtra(BibleActivity.ARG_BOOK_NAME, bookName);
                    intent1.putExtra(BibleActivity.ARG_CHAPTER_TO_LOAD, chapter);
                    context.startActivity(intent1);
                }
            });
            adb.show();

        } catch (JSONException e) {
            e.printStackTrace();
        }




//        Toast.makeText(context, "Verse of the Day", Toast.LENGTH_LONG).show();
    }
}
