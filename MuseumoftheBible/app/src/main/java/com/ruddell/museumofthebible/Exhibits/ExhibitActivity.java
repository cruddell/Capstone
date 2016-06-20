package com.ruddell.museumofthebible.Exhibits;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SharedElementCallback;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.ruddell.museumofthebible.R;

import java.util.List;

public class ExhibitActivity extends Activity {
    private static final String TAG = "ExhibitActivity";
    private static final boolean DEBUG_LOG = true;
    private RecyclerView mRecyclerView = null;
    private ExhibitAdapter mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit);

        mAdapter = new ExhibitAdapter();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setAdapter(mAdapter);

        getWindow().setStatusBarColor(getResources().getColor(R.color.md_red_900));


    }

}
