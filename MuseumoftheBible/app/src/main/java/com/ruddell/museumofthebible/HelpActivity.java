package com.ruddell.museumofthebible;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.google.android.gms.maps.MapView;

public class HelpActivity extends Activity {
    private static final String TAG = "HelpActivity";
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        getWindow().setStatusBarColor(getResources().getColor(R.color.md_blue_900));

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.loadUrl("https://chat.center/chris");
    }
}
