package com.ruddell.museumofthebible.Home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.ruddell.museumofthebible.Bible.BibleActivity;
import com.ruddell.museumofthebible.Database.BibleDatabase;
import com.ruddell.museumofthebible.Database.BibleDatabaseCopier;
import com.ruddell.museumofthebible.Exhibits.ExhibitActivity;
import com.ruddell.museumofthebible.Help.HelpActivity;
import com.ruddell.museumofthebible.Map.MapActivity;
import com.ruddell.museumofthebible.R;
import com.ruddell.museumofthebible.Ticketing.TicketActivity;
import com.ruddell.museumofthebible.views.AnimatedImageButton;

import java.io.IOException;

public class Home extends AppCompatActivity {
    private static final String TAG = "Home";
    private boolean mDidInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //perform data bootstrap if needed
//        DatabaseUpdater.updateDatabaseIfNeeded(this);

        setContentView(R.layout.activity_home);


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
            randomDelayReveal(findViewById(R.id.button_help));
            randomDelayReveal(findViewById(R.id.button_settings));
            mDidInit = true;
        }

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
        else if (button==findViewById(R.id.button_help)) {
            intent = new Intent(Home.this, HelpActivity.class);
            options = ActivityOptions.makeSceneTransitionAnimation(this, button, "button_help");
        }
        else if (button==findViewById(R.id.button_bible)) {
            intent = new Intent(Home.this, BibleActivity.class);
            options = ActivityOptions.makeSceneTransitionAnimation(this, button, "button_bible");
        }

        if (intent!=null)
        {
            if (options==null) startActivity(intent);
            else startActivity(intent,options.toBundle());
        }

    }
}
