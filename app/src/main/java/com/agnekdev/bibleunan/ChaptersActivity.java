package com.agnekdev.bibleunan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import adapters.ChaptersAdapter;
import models.Bible;

import static utilities.Functions.pluralize;
import static utilities.Functions.stripAccents;

public class ChaptersActivity extends AppCompatActivity {
    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    ChaptersAdapter adapter;
    List<Integer> chaptersList;
    private static String book;
    private static int chaptersSize;
    private String testament;

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
        testament =intent.getStringExtra("testament");
        book =intent.getStringExtra("book");

        chaptersList = Bible.getChapters(this,testament,book);
        chaptersSize=chaptersList.size();
        adapter = new ChaptersAdapter(this,chaptersList);
        mRecyclerView.setAdapter(adapter);

        getSupportActionBar()
                .setTitle(String.format("%s ( %d %s )",book,chaptersSize,pluralize(chaptersSize,"chapitre")));
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
        MenuItem menuItem=menu.findItem(R.id.menu_seach);
        SearchView searchView=(SearchView) menuItem.getActionView();
        searchView.setQueryHint("Recherche verset");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent =new Intent(ChaptersActivity.this,BibleSearchBissActivity.class);
                intent.putExtra("query",stripAccents(query));
                intent.putExtra("book",book);
                intent.putExtra("testament",testament);
                intent.putExtra("from","ChaptersActivity");
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
