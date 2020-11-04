package com.agnekdev.bibleunan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class DonsActivity extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dons);

        webView = findViewById(R.id.dons_webview);

        webView.loadUrl("file:///android_res/raw/index.html");
    }
}