package com.ruddell.museumofthebible.Home;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ruddell.museumofthebible.Api.ApiHelper;
import com.ruddell.museumofthebible.BaseActivity.BaseActivity;
import com.ruddell.museumofthebible.Bible.BibleActivity;
import com.ruddell.museumofthebible.Database.BibleDatabase;
import com.ruddell.museumofthebible.Database.BibleDatabaseCopier;
import com.ruddell.museumofthebible.Exhibits.ExhibitActivity;
import com.ruddell.museumofthebible.Exhibits.model.Exhibit;
import com.ruddell.museumofthebible.Facebook.FacebookActivity;
import com.ruddell.museumofthebible.Map.MapActivity;
import com.ruddell.museumofthebible.R;
import com.ruddell.museumofthebible.Settings.SettingsActivity;
import com.ruddell.museumofthebible.Ticketing.TicketActivity;
import com.ruddell.museumofthebible.utils.PrefUtils;
import com.ruddell.museumofthebible.views.AnimatedImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class Home extends BaseActivity {
    private static final String TAG = "Home";
    private static final boolean DEBUG_LOG = true;

    private boolean mDidInit = false;
    private boolean isReceiverRegistered;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //perform data bootstrap if needed
//        DatabaseUpdater.updateDatabaseIfNeeded(this);

        getExhibits();
        setContentView(R.layout.activity_home);

        //get permissions
        int checkPermissionWriteExternalStorage = ContextCompat.checkSelfPermission(Home.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (checkPermissionWriteExternalStorage== PackageManager.PERMISSION_GRANTED) {
            getExhibits();
        }
        else {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(findViewById(R.id.imageView), "Permission is needed to save data to your device so we can display the featured exhibits.", Snackbar.LENGTH_LONG).show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(Home.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        BibleDatabase myDbHelper = new BibleDatabase(this);

        try {

            //comment out the updateDatabaseIfNeeded method as this will load the JSON file
            // in the raw directory and create a database from it if no database currently exists.
            // instead, we want to inject a precompiled SQLite database as it is much quicker
            // and will not be subject to changes from the user

//            DatabaseUpdater.updateDatabaseIfNeeded(this);
            BibleDatabaseCopier.createDataBases(this);

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        //get token
        if (checkPlayServices()) {
            if (DEBUG_LOG) Log.d(TAG, "device has Google Play Services - attempting to retrieve Firebase token");
//            startService(new Intent(this, MyFirebaseInstanceIDService.class));
            Log.d(TAG, "InstanceID token: " + FirebaseInstanceId.getInstance().getToken());
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        //circle reveal buttons on first load only
        //if coming back, buttons will already be set to visible
        if (mDidInit==false) {
            randomDelayReveal(findViewById(R.id.button_bible));
            randomDelayReveal(findViewById(R.id.button_tickets));
            randomDelayReveal(findViewById(R.id.button_exhibits));
            randomDelayReveal(findViewById(R.id.button_map));
            randomDelayReveal(findViewById(R.id.button_facebook));
            randomDelayReveal(findViewById(R.id.button_settings));
            mDidInit = true;
        }

        registerReceiver();

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void randomDelayReveal(final View myView) {

        //reveal button with a random delay between 400ms and 600 ms
        int delay = (int)(Math.random() * 600.f) + 400;
        final android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                circleReveal(myView);
            }
        }, delay);
    }

    //reveal button using circlular reveal method
    private void circleReveal(final View myView) {

        if (!myView.isAttachedToWindow()) {
            ((AnimatedImageButton)myView).setAttachListener(new AnimatedImageButton.AttachListener() {
                @Override
                public void onAttached() {
                    circleReveal(myView);
                }
            });
            return;
        }

        // get the center for the clipping circle
        int cx = myView.getWidth() / 2;
        int cy = myView.getHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

        anim.addListener(new AnimatorListenerAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param animation
             */
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                myView.setVisibility(View.VISIBLE);
            }
        });
        anim.start();
    }

    //button clicked
    public void onClick(View button) {
        Intent intent = null;
        ActivityOptions options = null;
        if (button==findViewById(R.id.button_exhibits)) {
            intent = new Intent(Home.this, ExhibitActivity.class);
            options = ActivityOptions.makeSceneTransitionAnimation(this, button, "button_exhibits");
        }
        else if (button==findViewById(R.id.button_tickets)) {
            intent = new Intent(Home.this, TicketActivity.class);
            options = ActivityOptions.makeSceneTransitionAnimation(this, button, "button_tickets");
        }
        else if (button==findViewById(R.id.button_map)) {
            intent = new Intent(Home.this, MapActivity.class);
            options = ActivityOptions.makeSceneTransitionAnimation(this, button, "button_map");
        }
        else if (button==findViewById(R.id.button_facebook)) {
            intent = new Intent(Home.this, FacebookActivity.class);
            options = ActivityOptions.makeSceneTransitionAnimation(this, button, "button_facebook");
        }
        else if (button==findViewById(R.id.button_bible)) {
            intent = new Intent(Home.this, BibleActivity.class);
            options = ActivityOptions.makeSceneTransitionAnimation(this, button, "button_bible");
        }
        else if (button==findViewById(R.id.button_settings)) {
            intent = new Intent(Home.this, SettingsActivity.class);
            options = ActivityOptions.makeSceneTransitionAnimation(this, button, "button_settings");
        }

        if (intent!=null)
        {
            if (options==null) startActivity(intent);
            else startActivity(intent,options.toBundle());
        }

    }


    //download featured exhibit data in background so it will be ready when the user taps on the button
    private void getExhibits() {
        ApiHelper.getExhibits(new ApiHelper.NetworkCallback() {
            @Override
            public void onResponseReceived(final ApiHelper.HttpResponse response) {

                if (response.responseCode == HttpURLConnection.HTTP_OK) {
                    ArrayList<Exhibit> exhibits = new ArrayList<>();
                    try {
                        PrefUtils.setFeaturedExhibits(Home.this, response.responseBody);
                        JSONArray jsonArray = new JSONArray(response.responseBody);
                        for (int i=0; i<jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int id = jsonObject.getInt("ID");
                            String title = jsonObject.getString("Title");
                            String audioFile = jsonObject.getString("Audio");
                            String imageName = jsonObject.getString("Image");
                            String description = jsonObject.getString("Description");
                            Exhibit exhibit = new Exhibit(id, title, audioFile, imageName,description);
                            exhibits.add(exhibit);

                            //save image and audio files
                            ApiHelper.getAudioFile(audioFile, Home.this);
                            ApiHelper.getImageFile(imageName, Home.this);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }


    public static final String REGISTRATION_COMPLETE = "registration_complete";
    private void registerReceiver(){
        if(!isReceiverRegistered) {
            if (DEBUG_LOG) Log.d(TAG, "registerReceiver");
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        if (DEBUG_LOG) Log.d(TAG,"checkPlayServices");
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    getExhibits();

                } else {

                    // permission denied, boo!
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
