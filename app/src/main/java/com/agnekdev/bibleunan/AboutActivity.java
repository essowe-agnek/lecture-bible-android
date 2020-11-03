package com.agnekdev.bibleunan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AboutActivity extends AppCompatActivity {
    TextView mAbout;
    Toolbar mToolbar;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mAbout = findViewById(R.id.about_tv);
        mToolbar = findViewById(R.id.about_appbar);
        fab = findViewById(R.id.about_fab);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("A propos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String content = getString(R.string.about);

        mAbout.setText(Html.fromHtml(getString(R.string.about)));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"frederic.agneketom@gmail.com"});
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choisir votre application de messagerie"));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}