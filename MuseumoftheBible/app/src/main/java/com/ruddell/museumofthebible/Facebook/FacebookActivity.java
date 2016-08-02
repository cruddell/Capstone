package com.ruddell.museumofthebible.Facebook;

import android.os.Bundle;
import android.webkit.WebView;

import com.ruddell.museumofthebible.BaseActivity.BaseActivity;
import com.ruddell.museumofthebible.R;

public class FacebookActivity extends BaseActivity {

    private static final String TAG = "FacebookActivity";
    private static final boolean DEBUG_LOG = true;

    private static final String FACEBOOK_URL = "https://m.facebook.com/museumoftheBible/?fref=ts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);

        WebView webView = (WebView)findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(FACEBOOK_URL);
    }
}
