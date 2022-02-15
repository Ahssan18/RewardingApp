package com.ncodelab.rewardingapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ncodelab.rewardingapp.R;

public class WebActivity extends AppCompatActivity {

    WebView webView;

    String urlToLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        webView = findViewById(R.id.web_view);

        WebSettings webSettings = webView.getSettings();

        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new Callback());

        urlToLoad = getIntent().getStringExtra("URL");

        webView.loadUrl(urlToLoad);


    }

    private class Callback extends WebViewClient {

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return false;
        }
    }
}