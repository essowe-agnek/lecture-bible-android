package com.agnekdev.planlecturebible;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Bible;
import utilities.Functions;

public class TestamentsActivity extends AppCompatActivity {
    Toolbar mToolbar;
    ImageView imageViewOt;
    ImageView imageViewNt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testaments);

        mToolbar = findViewById(R.id.testaments_appbar);
        imageViewOt = findViewById(R.id.imageView_ot);
        imageViewNt = findViewById(R.id.imageView_nt);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Bible Louis Secong");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageViewOt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestamentsActivity.this,BooksActivity.class);
                intent.putExtra("testament","ot");
                startActivity(intent);
            }
        });

        imageViewNt.setOnClickListener(new View.OnClickListener() {
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
