package com.agnekdev.planlecturebible;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import java.util.List;

import adapters.ChaptersAdapter;
import models.Bible;

public class ChaptersActivity extends AppCompatActivity {
    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    ChaptersAdapter adapter;
    List<Integer> chaptersList;
    private static String book;
    private static int chaptersSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);

        mToolbar = findViewById(R.id.chapters_appbar);
        mRecyclerView = findViewById(R.id.chapters_recyclerview);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,4);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        showChapters();

    }

    private void showChapters() {
        Intent intent = getIntent();
        final String testament =intent.getStringExtra("testament");
        book =intent.getStringExtra("book");

        chaptersList = Bible.getChapters(this,testament,book);
        chaptersSize=chaptersList.size();
        adapter = new ChaptersAdapter(this,chaptersList);
        mRecyclerView.setAdapter(adapter);

        getSupportActionBar().setTitle(book);
    }

    public static String getBook(){
        return book;
    }

    public static int getChaptersSize(){
        return chaptersSize;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
