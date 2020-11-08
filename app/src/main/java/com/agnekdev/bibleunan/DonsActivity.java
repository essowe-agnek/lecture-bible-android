package com.agnekdev.bibleunan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.webkit.WebView;

public class DonsActivity extends AppCompatActivity {
    Toolbar mToolbar;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dons);

        webView = findViewById(R.id.dons_webview);
        mToolbar= findViewById(R.id.dons_appbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Faire un don");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView.loadUrl("file:///android_res/raw/index.html");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}