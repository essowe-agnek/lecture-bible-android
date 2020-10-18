package com.agnekdev.planlecturebible;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import adapters.BibleAdapter;
import models.Bible;
import utilities.Functions;


public class BibleActivity extends AppCompatActivity  implements ColorPickerDialogListener, BibleAdapter.MyCallBack {
    Toolbar mToolbar;
    TextView mChapter;
    public static RecyclerView mRecyclerView;
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
    private TextToSpeech textToSpeech;
    public static List<Bible> bibleList;
    private int versePosition;
    private static String category;
    private String testament;

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

        bibleList=showBibleContent();

//        if(!isOneChapter){
//            currentChapter =chapterStart;
//        }

        currentChapter = !isOneChapter ? chapterStart : chapter;

        imageViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isOneChapter){
                    currentChapter+=1;
                    if(currentChapter <= chapterEnd){
                        List<Bible> bibleList = Bible.getOneChapter(BibleActivity.this,book,currentChapter,"ot");
                        bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList,BibleActivity.this);
                        mRecyclerView.setAdapter(bibleAdapter);

                        mChapter.setText(book+" "+String.valueOf(currentChapter));
                    } else {
                        --currentChapter;
                    }
                } else {
                    if(category.equals("bible")){
                        // Navigation bible
                        int lastChapter= ChaptersActivity.getChaptersSize();
                        currentChapter+=1;
                        if(currentChapter <= lastChapter){
                            List<Bible> bibleList = Bible.getOneChapter(BibleActivity.this,book,currentChapter,testament);
                            bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList,BibleActivity.this);
                            mRecyclerView.setAdapter(bibleAdapter);

                            mChapter.setText(book+" "+String.valueOf(currentChapter));
                            getSupportActionBar().setTitle(book+" "+String.valueOf(currentChapter));
                        } else {
                            --currentChapter;
                        }
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
                        bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList,BibleActivity.this);
                        mRecyclerView.setAdapter(bibleAdapter);

                        mChapter.setText(book+" "+String.valueOf(currentChapter));
                    } else {
                        currentChapter+=1;
                    }
                } else {

                    currentChapter-=1;
                    if(currentChapter>=1){
                        List<Bible> bibleList = Bible.getOneChapter(BibleActivity.this,book,currentChapter,testament);
                        bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList,BibleActivity.this);
                        mRecyclerView.setAdapter(bibleAdapter);

                        mChapter.setText(book+" "+String.valueOf(currentChapter));
                        getSupportActionBar().setTitle(book+" "+String.valueOf(currentChapter));
                    } else {
                        currentChapter+=1;
                    }
                }
            }
        });
    }

    private void bibleTextToSpeech(){

        StringBuilder stringBuilder = new StringBuilder();
        for(Bible bible:bibleList){
            stringBuilder.append(bible.getVerseText());
        }
        Functions.agnekLog(stringBuilder.toString());
        textToSpeech.speak(stringBuilder.toString(),TextToSpeech.QUEUE_FLUSH,null);
    }

    public  List<Bible> showBibleContent() {
        Intent intent = getIntent();
        chaptersAndverses=intent.getStringArrayListExtra("chapters_verses");
        book= intent.getStringExtra("book");

        category=intent.getStringExtra("cat");

        String passages=intent.getStringExtra("passages");
        getSupportActionBar().setTitle(passages);
        testament = intent.getStringExtra("testament");

        List<Bible> bibleList=new ArrayList<>();
        switch (chaptersAndverses.size()){
            //One chapter
            case 1:
                String strChapter=chaptersAndverses.get(0).replaceAll("\\s","");
                chapter = Integer.parseInt(strChapter);
                bibleList = Bible.getOneChapter(BibleActivity.this,book,chapter,testament);

                mChapter.setText(passages);

                bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList,this);
                mRecyclerView.setAdapter(bibleAdapter);
                break;

                // Many chapters
            case 2:
                String strChapterStart=chaptersAndverses.get(0).replaceAll("\\s","");
                String strChapterEnd=chaptersAndverses.get(1).replaceAll("\\s","");
                chapterStart=Integer.parseInt(strChapterStart);
                chapterEnd = Integer.parseInt(strChapterEnd);
                bibleList = Bible.getOneChapter(BibleActivity.this,book,chapterStart,testament);
                mChapter.setText(book+" "+strChapterStart);

                bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList,this);
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
                bibleList= Bible.getOneChapter(BibleActivity.this,book,chapter,startVerse,endVerse,testament);

                mChapter.setText(passages);

                bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList,this);
                mRecyclerView.setAdapter(bibleAdapter);
                break;
        }
        return bibleList;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        Bible bible = bibleList.get(versePosition);
        String id = bible.getId();
        long resultat = Bible.highLightBible(this, id,color);
        if (resultat != -1) {
            Bible bible1 = BibleActivity.bibleList.get(versePosition);
            bible1.setBgColor(color);
            BibleActivity.bibleList.remove(versePosition);
            BibleActivity.bibleList.add(versePosition, bible1);

            bibleAdapter.notifyItemChanged(versePosition);

        }

    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    @Override
    public void listenerMethod(int position) {
        versePosition = position;
    }
}
