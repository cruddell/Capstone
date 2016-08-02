package com.ruddell.museumofthebible.Settings;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.ruddell.museumofthebible.BaseActivity.BaseActivity;
import com.ruddell.museumofthebible.R;
import com.ruddell.museumofthebible.utils.PrefUtils;

public class SettingsActivity extends BaseActivity {

    private static final String TAG = "SettingsActivity";
    private static final boolean DEBUG_LOG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Switch switch_verseOfTheDay = (Switch)findViewById(R.id.switch_verseOfTheDay);

        switch_verseOfTheDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
                PrefUtils.setPrefVerseOfTheDay(SettingsActivity.this, b);
            }
        });
    }
}
