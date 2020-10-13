package com.agnekdev.planlecturebible;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import models.Bible;
import models.Lecture;
import utilities.Functions;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar mToolbar;
    TextToSpeech textToSpeech;
    ImageView mAudio1;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;

    TextView mToday;
    TextView mLectureMatin;
    TextView mLectureSoir;

    LinearLayout llLectureMatin;
    LinearLayout llLectureSoir;
    private ArrayList<String> chaptersAndVerses;
    private String eviningBook;
    private String passagesSoir;
    private String passagesMatin;
    private String morningBook;
    private ArrayList<String> morningChaptersAndVerses;
    private Lecture lecture1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Vérifier si la lecture est paramétrée
        setLectureSettigs();

        mAudio1= findViewById(R.id.imageView_audio1);
        mToolbar = findViewById(R.id.main_appbar);
        mToday = findViewById(R.id.tv_main_today);
        mLectureMatin = findViewById(R.id.tv_main_lecture_matin);
        mLectureSoir = findViewById(R.id.tv_main_lecture_soir);
        llLectureMatin = findViewById(R.id.ll_lecture_matin);
        llLectureSoir = findViewById(R.id.ll_lecture_soir);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Plan de lecture Bible");

        configureDrawerLayout();
        configureNavigationView();

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                int result=textToSpeech.setLanguage(Locale.FRENCH);
                if(result==TextToSpeech.LANG_MISSING_DATA ||
                        result==TextToSpeech.LANG_NOT_SUPPORTED){
                    Toast.makeText(getApplicationContext(),"Language not supported",Toast.LENGTH_LONG).show();
                }
            }
        });

        mAudio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Au commmencement était la parole", Toast.LENGTH_LONG).show();
                textToSpeech.speak(" Au commencement était la parole",TextToSpeech.QUEUE_FLUSH,null);
            }
        });

        llLectureSoir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String book = lecture1.getLivreSoir();
                chaptersAndVerses =getChapters(lecture1.getChapitreSoir());
                String passages = lecture1.getLivreSoir()+" "+lecture1.getChapitreSoir();

                Intent intent = new Intent(MainActivity.this,BibleActivity.class);
                intent.putStringArrayListExtra("chapters_verses",chaptersAndVerses);
                intent.putExtra("book",book);
                intent.putExtra("passages",passages);
                intent.putExtra("passages_matin",passagesMatin);
                intent.putExtra("testament","ot");
                startActivity(intent);
            }
        });

        llLectureMatin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String book =lecture1.getLivreMatin();
                chaptersAndVerses =getChapters(lecture1.getChapitreMatin());
                String passages = lecture1.getLivreMatin()+" "+lecture1.getChapitreMatin();

                Intent intent = new Intent(MainActivity.this,BibleActivity.class);
                intent.putStringArrayListExtra("chapters_verses",chaptersAndVerses);
                intent.putExtra("book",book);
                intent.putExtra("passages",passages);
                //intent.putExtra("passages_matin",passagesMatin);
                intent.putExtra("testament","nt");
                startActivity(intent);
            }
        });
    }

    private void showTodayLecture() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM");
        Date today= new Date();
        final String todayFormated=sdf.format(today);
        mToday.setText(todayFormated);

        final int nombreAnnees = Functions.getNombreAnnees(getApplicationContext());
        final int rangAnnee = Functions.getRangAnnee(getApplicationContext());

        lecture1=Lecture.getTodayLecture(getApplicationContext(),nombreAnnees,rangAnnee);
        passagesSoir =lecture1.getLivreSoir()+" "+lecture1.getChapitreSoir();
        passagesMatin =lecture1.getLivreMatin()+" "+lecture1.getChapitreMatin();
        mLectureMatin.setText("Matin: "+passagesMatin);
        mLectureSoir.setText("Soir: "+ passagesSoir);

    }



    private ArrayList<String> getChapters(String rawChapter){
        ArrayList<String> response= new ArrayList<>();
        // plusieurs chapitres
        if(rawChapter.contains(",")){
            String[] arrayChapters= rawChapter.split(",");
            String chapterStart =arrayChapters[0];
            String chapterEnd=arrayChapters[arrayChapters.length-1];
            response.add(chapterStart);
            response.add(chapterEnd);

            // 1 chapitre et quelques versets
        } else if(rawChapter.contains(":")){
            String[] arrayChapterVerse=rawChapter.split(":");
            String chapter= arrayChapterVerse[0];
            String verses=arrayChapterVerse[1];

            String[] arrayVerses = verses.split("-");
            String verseStart =arrayVerses[0];
            String verseEnd =arrayVerses[1];

            response.add(chapter);
            response.add(verseStart);
            response.add(verseEnd);

            //un verset
        } else {
            response.add(rawChapter);
        }

        return response;

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        int itemId=menuItem.getItemId();
        switch (itemId){
            case R.id.navigation_item_param:
                startActivity(new Intent(MainActivity.this,ChoosePlanActivity.class));
                break;

            case R.id.navigation_item_tout_leplan:
                startActivity(new Intent(MainActivity.this,ListeLectureActivity.class));
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences("com.agnekdev.lecture",MODE_PRIVATE);
        boolean settingsisSet = sp.getBoolean("settings_is_set",false);
        if(settingsisSet){
            showTodayLecture();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 2 - Configure Drawer Layout
    private void configureDrawerLayout(){
        this.mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // 3 - Configure NavigationView
    private void configureNavigationView(){
        this.mNavigationView = (NavigationView) findViewById(R.id.navigationview);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void setLectureSettigs(){
        SharedPreferences sp = getSharedPreferences("com.agnekdev.lecture",MODE_PRIVATE);
        boolean settingsisSet = sp.getBoolean("settings_is_set",false);
        if(!settingsisSet){
            startActivity(new Intent(MainActivity.this,ChoosePlanActivity.class));
        }
    }
}
