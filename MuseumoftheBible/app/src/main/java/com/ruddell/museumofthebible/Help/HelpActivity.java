package com.ruddell.museumofthebible.Help;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.ruddell.museumofthebible.R;

public class HelpActivity extends AppCompatActivity {
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
