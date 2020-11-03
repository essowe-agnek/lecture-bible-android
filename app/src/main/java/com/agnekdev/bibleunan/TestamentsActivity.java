package com.agnekdev.bibleunan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TestamentsActivity extends AppCompatActivity {
    Toolbar mToolbar;
    CardView cvOldTestament;
    CardView cvNewTestament;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testaments);

        mToolbar = findViewById(R.id.testaments_appbar);
        cvOldTestament = findViewById(R.id.cardView_testaments_ot);
        cvNewTestament = findViewById(R.id.cardView_testaments_nt);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Bible Louis Segong");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cvOldTestament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestamentsActivity.this,BooksActivity.class);
                intent.putExtra("testament","ot");
                startActivity(intent);
            }
        });

        cvNewTestament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestamentsActivity.this,BooksActivity.class);
                intent.putExtra("testament","nt");
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
