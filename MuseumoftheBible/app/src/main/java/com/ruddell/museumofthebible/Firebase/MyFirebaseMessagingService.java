package com.ruddell.museumofthebible.Firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ruddell.museumofthebible.Firebase.NotificationTypes.VerseOfTheDay;
import com.ruddell.museumofthebible.Home.Home;
import com.ruddell.museumofthebible.R;
import com.ruddell.museumofthebible.Widget.WidgetUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ChristopherRuddell, Museum of the Bible, on 7/28/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final boolean DEBUG_LOG = true;
    private static final String TAG = "MyFBMessagingService";

    public MyFirebaseMessagingService() {

    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        String message = "";
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            message = remoteMessage.getData().get("message");
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            message = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Message Notification Body: " + message);
        }

        try {
            JSONObject jsonObject = new JSONObject(message);
            String book = jsonObject.getString("book");
            int chapter = jsonObject.getInt("chapter");
            int verse = jsonObject.getInt("verse");
            String text = jsonObject.getString("text");

            VerseOfTheDay verseOfTheDay = new VerseOfTheDay(book, chapter, verse, text);
            sendNotification(verseOfTheDay);

            //update widget
            WidgetUtil.setVerseOfDay(getApplication(), text);



        } catch (JSONException e) {
            e.printStackTrace();
        }

        // [END_EXCLUDE]
    }

    /**
     * Create and show a simple notification containing the received Firebase message.
     *
     * @param verseOfTheDay Firebase message received.
     */
    private void sendNotification(VerseOfTheDay verseOfTheDay) {
        Intent intent = new Intent(this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.bible)
                .setContentTitle("Verse of the Day")
                .setContentText(verseOfTheDay.getText())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}
