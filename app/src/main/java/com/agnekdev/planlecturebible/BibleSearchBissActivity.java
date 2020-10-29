package com.agnekdev.planlecturebible;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import adapters.BibleSearchBissAdapter;
import models.Bible;
import utilities.Functions;

public class BibleSearchBissActivity extends AppCompatActivity {
    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    BibleSearchBissAdapter adapter;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bible_search_biss);

        mToolbar = findViewById(R.id.bible_search_biss_appbar);
        mRecyclerView= findViewById(R.id.bs_biss_recyclerview);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        Intent intent = getIntent();
        query = intent.getStringExtra("query");
        String testament=intent.getStringExtra("testament");
        String book = intent.getStringExtra("book");
        new SearchTask().execute(query,testament,book);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private class SearchTask extends AsyncTask<String,Integer,List<Bible>>{

        private AlertDialog dialog;
        TextView mProgress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AlertDialog.Builder builder = new AlertDialog.Builder(BibleSearchBissActivity.this);
            View view = getLayoutInflater().inflate(R.layout.progress,null);
            builder.setView(view);
            dialog = builder.create();

            mProgress = view.findViewById(R.id.loading_msg);
            dialog.show();
        }

        @Override
        protected List<Bible> doInBackground(String... strings) {
            String query=strings[0];
            String testament=strings[1];
            String book=strings[2];
            int i=0;
            List<Bible> bibleList= Bible.searchVersesFromBook(BibleSearchBissActivity.this,query,testament,book);
            for (Bible bible:bibleList){
                ++i;
                publishProgress(i);
            }
            return bibleList;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgress.setText(String.valueOf(values[0])+" versets");
        }

        @Override
        protected void onPostExecute(List<Bible> bibleList) {
            super.onPostExecute(bibleList);
            dialog.dismiss();
            getSupportActionBar().setTitle(String.valueOf(bibleList.size())+" "+ Functions.pluralize(bibleList.size(),"verset"));
            adapter = new BibleSearchBissAdapter(BibleSearchBissActivity.this,bibleList,query.toLowerCase());
            mRecyclerView.setAdapter(adapter);
        }
    }
}