package com.agnekdev.bibleunan;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapters.BibleSearchAdapter;
import models.Bible;

import  static utilities.Functions.pluralize;

public class BibleSearchActivity extends AppCompatActivity {
    //Toolbar mToolbar;
    public static List<Bible> bibleList;

    ExpandableListView expandableListView;
    TextView mCpte1;
    TextView mCpte2;
    TextView mVerses;
    TextView mBooks;
    ImageView imageViewbback;
    List<String> bookList;
    HashMap<String,List<Bible>> mapVerses;
    BibleSearchAdapter adapter;
    private String textSubmited;
    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bible_search);

        expandableListView = findViewById(R.id.bs_expandableListView);
        mCpte1= findViewById(R.id.badge_cpt_1);
        mCpte2= findViewById(R.id.badge_cpt_2);
        mBooks = findViewById(R.id.search_bible_book);
        mVerses = findViewById(R.id.search_bible_verse);
        imageViewbback = findViewById(R.id.imageview_back);

        //setProgressDialog();

//        bookList = getBooksList(this);
//        mapVerses= getVersesAndHeaderAsBook(bookList);
        new AsynFetchData().execute();


        imageViewbback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    List<String> getBooksList(Context context){
        Intent intent = getIntent();
        textSubmited =intent.getStringExtra("query");
        String testament =intent.getStringExtra("testament");
        bibleList =Bible.searchVerses(context,textSubmited,testament);
        List<String> booksList= Bible.getBooks();
        return booksList;
    }
    HashMap<String,List<Bible>> getVersesAndHeaderAsBook(List<String> booksList){
        HashMap<String,List<Bible>> mapVersesWithHeaders = new HashMap<>();
        for(String book:booksList){
            List<Bible> newBibleList = new ArrayList<>();
            for(Bible bible:bibleList){
                if(book.equals(bible.getMorningBook()) || book.equals(bible.getEveningBook())){
                    newBibleList.add(bible);
                }
            }
            mapVersesWithHeaders.put(book,newBibleList);
        }
        return mapVersesWithHeaders;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    class AsynFetchData extends AsyncTask<Void,Integer,HashMap<String,List<Bible>>>{
        private TextView mProgress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AlertDialog.Builder builder = new AlertDialog.Builder(BibleSearchActivity.this);
            View view = getLayoutInflater().inflate(R.layout.progress,null);
            builder.setView(view);
            dialog = builder.create();

            mProgress = view.findViewById(R.id.loading_msg);
            dialog.show();

        }

        @Override
        protected HashMap<String, List<Bible>> doInBackground(Void... voids) {
            bookList = getBooksList(BibleSearchActivity.this);
            mapVerses= getVersesAndHeaderAsBook(bookList);
            //**************************


            return mapVerses;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(HashMap<String, List<Bible>> stringListHashMap) {
            super.onPostExecute(stringListHashMap);
            dialog.dismiss();
            mCpte1.setText(String.valueOf(bookList.size()));
            mCpte2.setText(String.valueOf(bibleList.size()));
            mBooks.setText(pluralize(bookList.size(),"livre"));
            mVerses.setText(pluralize(bibleList.size(),"verset"));

            adapter = new BibleSearchAdapter(BibleSearchActivity.this,bookList,mapVerses,textSubmited.toLowerCase());
            expandableListView.setAdapter(adapter);
        }
    }
}
