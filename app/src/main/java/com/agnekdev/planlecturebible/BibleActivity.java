package com.agnekdev.planlecturebible;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import adapters.BibleAdapter;
import models.Bible;
import utilities.Functions;


public class BibleActivity extends AppCompatActivity  {
    Toolbar mToolbar;
    TextView mChapter;
    RecyclerView mRecyclerView;
    ImageView imageViewNext;
    ImageView imageViewPrior;
    BibleAdapter bibleAdapter;
    ArrayList<String> chaptersAndverses;
    private int chapterStart=0;
    private int chapterEnd;
    private String book;
    private int currentChapter;
    private int chapter=0;
    private boolean isOneChapter=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bible);

        mToolbar = findViewById(R.id.bible_appbar);
        mRecyclerView= findViewById(R.id.bible_recyclerview);
        mChapter = findViewById(R.id.tv_bible_chapter);
        imageViewNext = findViewById(R.id.imageView_bible_next);
        imageViewPrior = findViewById(R.id.imageView_bible_prior);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BibleActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        Intent intent= getIntent();
        String testament = intent.getStringExtra("testament");
        if(testament.equals("ot")){
            showEveningContent();
        } else {
            showMorningContent();
        }

        if(!isOneChapter){
            currentChapter =chapterStart;
        }

        imageViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isOneChapter){
                    currentChapter+=1;
                    if(currentChapter <= chapterEnd){
                        List<Bible> bibleList = Bible.getOneChapter(BibleActivity.this,book,currentChapter,"ot");
                        bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList);
                        mRecyclerView.setAdapter(bibleAdapter);

                        mChapter.setText(book+" "+String.valueOf(currentChapter));
                    } else {
                        --currentChapter;
                    }
                }
            }
        });

        imageViewPrior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isOneChapter){
                    currentChapter-=1;
                    if(currentChapter>=chapterStart){
                        List<Bible> bibleList = Bible.getOneChapter(BibleActivity.this,book,currentChapter,"ot");
                        bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList);
                        mRecyclerView.setAdapter(bibleAdapter);

                        mChapter.setText(book+" "+String.valueOf(currentChapter));
                    } else {
                        currentChapter+=1;
                    }
                }
            }
        });
    }

    private List<Bible> showEveningContent() {
        Intent intent = getIntent();
        chaptersAndverses=intent.getStringArrayListExtra("chapters_verses");
        book= intent.getStringExtra("book");

        String passages=intent.getStringExtra("passages");
        getSupportActionBar().setTitle(passages);

        List<Bible> bibleList=new ArrayList<>();
        switch (chaptersAndverses.size()){
            //One chapter
            case 1:
                String strChapter=chaptersAndverses.get(0).replaceAll("\\s","");
                chapter = Integer.parseInt(strChapter);
                bibleList = Bible.getOneChapter(BibleActivity.this,book,chapter,"ot");

                mChapter.setText(passages);

                bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList);
                mRecyclerView.setAdapter(bibleAdapter);
                break;

                // Many chapters
            case 2:
                String strChapterStart=chaptersAndverses.get(0).replaceAll("\\s","");
                String strChapterEnd=chaptersAndverses.get(1).replaceAll("\\s","");
                chapterStart=Integer.parseInt(strChapterStart);
                chapterEnd = Integer.parseInt(strChapterEnd);
                bibleList = Bible.getOneChapter(BibleActivity.this,book,chapterStart,"ot");
                mChapter.setText(book+" "+strChapterStart);

                bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList);
                mRecyclerView.setAdapter(bibleAdapter);

                isOneChapter = false;
                break;

                // One chapter and some verses
            case 3:
                strChapter=chaptersAndverses.get(0).replaceAll("\\s","");
                String strStartVerse=chaptersAndverses.get(1).replaceAll("\\s","");
                String strEndVerse=chaptersAndverses.get(2).replaceAll("\\s","");
                chapter =Integer.parseInt(strChapter);
                int startVerse =Integer.parseInt(strStartVerse);
                int endVerse =Integer.parseInt(strEndVerse);
                bibleList= Bible.getOneChapter(BibleActivity.this,book,chapter,startVerse,endVerse,"ot");

                mChapter.setText(passages);
                break;
        }
        return bibleList;
    }

    private List<Bible> showMorningContent() {
        Intent intent = getIntent();
        chaptersAndverses=intent.getStringArrayListExtra("chapters_verses");
        book= intent.getStringExtra("book");

        String passages=intent.getStringExtra("passages");
        getSupportActionBar().setTitle(passages);

        List<Bible> bibleList=new ArrayList<>();
        Functions.agnekLog(chaptersAndverses.get(0));
        switch (chaptersAndverses.size()){
            //One chapter
            case 1:
                String strChapter=chaptersAndverses.get(0).replaceAll("\\s","");
                chapter = Integer.parseInt(strChapter);
                bibleList = Bible.getOneChapter(BibleActivity.this,book,chapter,"nt");

                mChapter.setText(passages);

                bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList);
                mRecyclerView.setAdapter(bibleAdapter);
                break;

            // Many chapters
            case 2:
                String strChapterStart=chaptersAndverses.get(0).replaceAll("\\s","");
                String strChapterEnd=chaptersAndverses.get(1).replaceAll("\\s","");
                chapterStart=Integer.parseInt(strChapterStart);
                chapterEnd = Integer.parseInt(strChapterEnd);
                bibleList = Bible.getOneChapter(BibleActivity.this,book,chapterStart,"nt");
                mChapter.setText(book+" "+strChapterStart);

                bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList);
                mRecyclerView.setAdapter(bibleAdapter);

                isOneChapter = false;
                break;

            // One chapter and some verses
            case 3:
                strChapter=chaptersAndverses.get(0).replaceAll("\\s","");
                String strStartVerse=chaptersAndverses.get(1).replaceAll("\\s","");
                String strEndVerse=chaptersAndverses.get(2).replaceAll("\\s","");
                chapter =Integer.parseInt(strChapter);
                int startVerse =Integer.parseInt(strStartVerse);
                int endVerse =Integer.parseInt(strEndVerse);
                bibleList= Bible.getOneChapter(BibleActivity.this,book,chapter,startVerse,endVerse,"nt");

                mChapter.setText(passages);
                break;
        }
        return bibleList;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
