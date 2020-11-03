package com.agnekdev.bibleunan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.util.ArrayList;
import java.util.List;

import adapters.BibleAdapter;
import models.Bible;
import utilities.Functions;

import static utilities.Functions.stripAccents;


public class BibleActivity extends AppCompatActivity  implements ColorPickerDialogListener, BibleAdapter.MyCallBack {
    Toolbar mToolbar;
    TextView mChapter;
    public static RecyclerView mRecyclerView;
    ImageView imageViewNext;
    ImageView imageViewPrior;
    BottomNavigationView bottomNavigationView;

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
    private String passages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bible);

        mToolbar = findViewById(R.id.bible_appbar);
        mRecyclerView= findViewById(R.id.bible_recyclerview);
        mChapter = findViewById(R.id.tv_bible_chapter);
        imageViewNext = findViewById(R.id.imageView_bible_next);
        imageViewPrior = findViewById(R.id.imageView_bible_prior);
        bottomNavigationView = findViewById(R.id.bible_bottom_navigation);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BibleActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        bibleList=showBibleContent();

        currentChapter = !isOneChapter ? chapterStart : chapter;

        imageViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isOneChapter){
                    currentChapter+=1;
                    if(currentChapter <= chapterEnd){
                        List<Bible> bibleList = Bible.getOneChapter(BibleActivity.this,book,currentChapter,"ot");
                        bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList,BibleActivity.this,bottomNavigationView);
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
                            bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList,BibleActivity.this,bottomNavigationView);
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
                        bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList,BibleActivity.this,bottomNavigationView);
                        mRecyclerView.setAdapter(bibleAdapter);

                        mChapter.setText(book+" "+String.valueOf(currentChapter));
                    } else {
                        currentChapter+=1;
                    }
                } else {

                    currentChapter-=1;
                    if(currentChapter>=1){
                        List<Bible> bibleList = Bible.getOneChapter(BibleActivity.this,book,currentChapter,testament);
                        bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList,BibleActivity.this,bottomNavigationView);
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
        passages=intent.getStringExtra("passages");

        getSupportActionBar().setTitle(passages);
        testament = intent.getStringExtra("testament");

        int position=intent.getIntExtra("position",1);

        List<Bible> bibleList=new ArrayList<>();
        switch (chaptersAndverses.size()){
            //One chapter
            case 1:
                String strChapter=chaptersAndverses.get(0).replaceAll("\\s","");
                chapter = Integer.parseInt(strChapter);
                bibleList = Bible.getOneChapter(BibleActivity.this,book,chapter,testament);

                mChapter.setText(passages);

                bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList,this,bottomNavigationView);
                mRecyclerView.setAdapter(bibleAdapter);
                mRecyclerView.getLayoutManager().scrollToPosition(position-1);
                break;

                // Many chapters
            case 2:
                String strChapterStart=chaptersAndverses.get(0).replaceAll("\\s","");
                String strChapterEnd=chaptersAndverses.get(1).replaceAll("\\s","");
                chapterStart=Integer.parseInt(strChapterStart);
                chapterEnd = Integer.parseInt(strChapterEnd);
                bibleList = Bible.getOneChapter(BibleActivity.this,book,chapterStart,testament);
                mChapter.setText(book+" "+strChapterStart);

                bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList,this,bottomNavigationView);
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

                bibleAdapter = new BibleAdapter(BibleActivity.this,bibleList,this,bottomNavigationView);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bible_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.bible_menu_item_seach);
        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Recherche verset");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent =new Intent(BibleActivity.this,BibleSearchBissActivity.class);
                intent.putExtra("query",stripAccents(query));
                intent.putExtra("book",book);
                intent.putExtra("chapter",chapter);
                intent.putExtra("testament",testament);
                intent.putExtra("from","BibleActivity");
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.bible_menu_item_mode:
//                AlertDialog.Builder builder= new AlertDialog.Builder(this);
//                builder.setTitle("Veuillez choisir le mode");
//                View view = getLayoutInflater().inflate(R.layout.choose_mode,null);
//                builder.setView(view);
//
//                final AlertDialog dialog = builder.create();
//                Button btnModeLight = view.findViewById(R.id.btn_mode_light);
//                Button btnModeDark = view.findViewById(R.id.btn_mode_dark);
//
//                btnModeLight.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        setDayNightMode(false);
//                        dialog.dismiss();
//                    }
//                });
//
//                btnModeDark.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        setDayNightMode(true);
//                        dialog.dismiss();
//                    }
//                });
//                dialog.show();
//                break;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bible.setNbSelected(0);
    }

    void setDayNightMode(boolean isNIghtMode){
        AppCompatDelegate
                .setDefaultNightMode(isNIghtMode?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);
        Intent intent = new Intent(BibleActivity.this,BibleActivity.this.getClass());
        intent.putStringArrayListExtra("chapters_verses",chaptersAndverses);
        intent.putExtra("book",book);
        intent.putExtra("cat",category);
        intent.putExtra("testament",testament);
        intent.putExtra("passages",passages);

        SharedPreferences shp = getSharedPreferences("lecturebible.mode",MODE_PRIVATE);
        //boolean isNIghtMode =shp.getBoolean("is_night_mode",false);
        SharedPreferences.Editor editor= shp.edit();
        editor.putBoolean("is_night_mode",isNIghtMode);
        editor.commit();
        finish();
        startActivity(intent);
    }
}
