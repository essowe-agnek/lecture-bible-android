package com.agnekdev.planlecturebible;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.Normalizer;
import java.util.List;
import adapters.BooksAdapter;
import models.Bible;

import static utilities.Functions.stripAccents;

public class BooksActivity extends AppCompatActivity{
    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    BooksAdapter adapter;
    List<String> booksList;
    private static String testament;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        mToolbar = findViewById(R.id.books_appbar);
        mRecyclerView = findViewById(R.id.books_recyclerview);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2,RecyclerView.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showBooks();
    }

    void showBooks(){
        Intent intent =getIntent();
        testament = intent.getStringExtra("testament");
        booksList = Bible.getBooks(this,testament);
        adapter = new BooksAdapter(this,booksList);
        mRecyclerView.setAdapter(adapter);

        final String title = testament.equals("ot") ? "Ancien testament" : "Nouveau testament";
        getSupportActionBar().setTitle(title);
    }

    public static String getTestament(){
        return testament;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem item=menu.findItem(R.id.menu_seach);
        SearchView searchView=(SearchView)item.getActionView();
        searchView.setQueryHint("Recherche verset");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent =new Intent(BooksActivity.this,SearchActivity.class);
                intent.putExtra("query",stripAccents(query));
                intent.putExtra("testament",testament);
                intent.putExtra("zone","un_testament");
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
