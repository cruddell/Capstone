package com.ruddell.museumofthebible.Firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.ruddell.museumofthebible.Api.ApiHelper;

/**
 * Created by ChristopherRuddell, Museum of the Bible, on 7/28/16.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final boolean DEBUG_LOG = true;
    private static final String TAG = "MyFBInstanceIDService";

    public MyFirebaseInstanceIDService() {

    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        if (DEBUG_LOG) Log.d(TAG, "sendRegistrationToServer");
        ApiHelper.registerGcmToken(token, Channel.CHANNEL_VERSE_OF_THE_DAY, this.getApplicationContext());
    }
}
