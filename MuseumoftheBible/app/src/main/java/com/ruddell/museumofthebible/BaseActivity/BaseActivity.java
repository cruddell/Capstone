package com.ruddell.museumofthebible.BaseActivity;

import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;

import com.ruddell.museumofthebible.Firebase.NotificationBroadcastReceiver;

/**
 * Created by ChristopherRuddell, Museum of the Bible, on 8/1/16.
 */
public class BaseActivity extends AppCompatActivity {
    private static final boolean DEBUG_LOG = true;
    private static final String TAG = "BaseActivity";
    private NotificationBroadcastReceiver mBroadcastReceiver;

    public BaseActivity() {
        mBroadcastReceiver = new NotificationBroadcastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter("com.google.android.c2dm.intent.RECEIVE");
        filter.setPriority(1);
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mBroadcastReceiver);
    }
}
