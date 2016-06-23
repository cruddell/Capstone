package com.ruddell.museumofthebible.Map;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ruddell.museumofthebible.R;

public class MapActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener {
    private static final String TAG = "MapActivity";
    private static final boolean DEBUG = true;
    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    private boolean mMapIsReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        getWindow().setStatusBarColor(getResources().getColor(R.color.md_purple_900));

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);

        //once map is ready, get a reference to it, then circle reveal the map when loaded
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (DEBUG) Log.i(TAG, "onMapReady");
                mGoogleMap = googleMap;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(38.884628, -77.016297),15,0,0));
                mGoogleMap.moveCamera(cameraUpdate);

                final android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!mMapIsReady) {
                            mMapIsReady = true;
                            circleReveal(findViewById(R.id.map_container));
                            animateCamera();
                        }
                    }
                }, 1000);

                addMarker();
                mGoogleMap.setOnMarkerClickListener(MapActivity.this);
                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        if (DEBUG) Log.i(TAG, "onMapLoaded");
                        if (!mMapIsReady) {
                            mMapIsReady = true;
                            final android.os.Handler handler = new android.os.Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    circleReveal(findViewById(R.id.map_container));
                                    animateCamera();
                                }
                            }, 500);
                        }

                    }
                });

            }
        });

        checkGPSversion();
    }

    //code from StackOverflow: http://stackoverflow.com/a/17858338/4917954
    private void checkGPSversion() {
        Integer resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SUCCESS) {

        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                //This dialog will help the user update to the latest GooglePlayServices
                dialog.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (DEBUG) Log.i(TAG, "onBackPressed");
        if (!mMapIsReady) {
            finish();
            return;
        }
        // get the center for the clipping circle
        final View myView = findViewById(R.id.map_container);
        int cx = myView.getWidth() / 2;
        int cy = myView.getHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, finalRadius, 0);

        anim.addListener(new AnimatorListenerAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param animation
             */
            @Override
            public void onAnimationStart(Animator animation) {
                if (DEBUG) Log.i(TAG, "onAnimationStart");
                super.onAnimationStart(animation);
            }

            /**
             * {@inheritDoc}
             *
             * @param animation
             */
            @Override
            public void onAnimationEnd(Animator animation) {
                if (DEBUG) Log.i(TAG, "onAnimationEnd");
                super.onAnimationEnd(animation);
                myView.setVisibility(View.GONE);
                finish();
            }
        });
        if (DEBUG) Log.i(TAG, "starting exit animation...");
        anim.start();

    }

    private void addMarker() {
        /*
        int versionCode, LatLng position, String title, String snippet, IBinder wrappedIcon, float anchorU, float anchorV, boolean draggable, boolean visible, boolean flat, float rotation, float infoWindowAnchorU, float infoWindowAnchorV, float alpha
         */


        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(38.884628, -77.016297))
                .title(getResources().getString(R.string.app_name))
                .snippet(getResources().getString(R.string.map_snippet))
                ;

        mGoogleMap.addMarker(markerOptions);
    }

    private void animateCamera() {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(38.884628, -77.016297),15,0,0));
        mGoogleMap.animateCamera(cameraUpdate);
    }

    //reveal button using circlular reveal method
    private void circleReveal(final View myView) {

        // get the center for the clipping circle
        int cx = myView.getWidth() / 2;
        int cy = myView.getHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
        anim.setDuration(750);

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

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClick");
        marker.showInfoWindow();
        return false;
    }
}
