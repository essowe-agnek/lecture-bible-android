package com.agnekdev.planlecturebible;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import models.Bible;
import models.Lecture;
import utilities.AlarmReceiver;
import utilities.AlarmService;
import utilities.Functions;

import static utilities.Functions.getChapters;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar mToolbar;
    TextToSpeech textToSpeech;
    ImageView mAudioMatin;
    ImageView mAudioSoir;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    BottomNavigationView bottomNavigationView;

    TextView mToday;
    TextView mLectureMatin;
    TextView mLectureSoir;
    TextView mPlanChoosed;
    Button btnDons;

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

        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isNightMode =shp.getBoolean("mode_night",false);
        AppCompatDelegate
                .setDefaultNightMode(isNightMode?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);


        //Vérifier si la lecture est paramétrée
        setLectureSettings();

        mAudioMatin= findViewById(R.id.imageView_audio_matin);
        mAudioSoir= findViewById(R.id.imageView_audio_soir);
        mToolbar = findViewById(R.id.main_appbar);
        mToday = findViewById(R.id.tv_main_today);
        mLectureMatin = findViewById(R.id.tv_main_lecture_matin);
        mLectureSoir = findViewById(R.id.tv_main_lecture_soir);
        llLectureMatin = findViewById(R.id.ll_lecture_matin);
        llLectureSoir = findViewById(R.id.ll_lecture_soir);
        bottomNavigationView = findViewById(R.id.main_bottom_navigation);
        btnDons = findViewById(R.id.btn_main_don);

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

        mAudioMatin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(getPassage("nt"),TextToSpeech.QUEUE_FLUSH,null);
            }
        });

        mAudioSoir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Functions.agnekLog(String.valueOf(getPassage("ot").length()));
                textToSpeech.speak(getPassage("ot"),TextToSpeech.QUEUE_FLUSH,null);
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
//                intent.putExtra("passages_matin",passagesMatin);
                intent.putExtra("testament","ot");
                intent.putExtra("cat","plan");
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
                intent.putExtra("testament","nt");
                intent.putExtra("cat","plan");
                startActivity(intent);
            }
        });

        btnDons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startNotification();
                startService(new Intent(MainActivity.this,AlarmService.class));
                Toast.makeText(MainActivity.this,"Ok",Toast.LENGTH_LONG).show();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.item_menu_lire_tout:
                        startActivity(new Intent(MainActivity.this,ListeLectureActivity.class));
                        break;

                    case R.id.item_menu_read_bible:
                        startActivity(new Intent(MainActivity.this,TestamentsActivity.class));
                        break;
                }
                return false;
            }
        });
    }

    private void startNotification(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notification=sp.getBoolean("notifications",false);
        if(!notification){
            return;
        }
        String strTime =sp.getString("time_not","05:00");
        String[] timeSplit =strTime.split(":");
        String strHour=timeSplit[0];
        String strMinute=timeSplit[1];
        int hour =Integer.parseInt(strHour.replaceFirst("^0+(?!$)", ""));
        int minute =Integer.parseInt(strMinute.replaceFirst("^0+(?!$)", ""));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE,minute);

        AlarmManager alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent2 = new Intent(this, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent2, 0);
//        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_DAY, alarmIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),alarmIntent);
        } else {
            alarmMgr.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),alarmIntent);
        }


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

    String getPassage(String testament){
        List<Bible> bibleList= new ArrayList<>();
        String book = testament.equals("ot") ? lecture1.getLivreSoir():lecture1.getLivreMatin();
        List<String> chaptersList=
                testament.equals("ot") ? getChapters(lecture1.getChapitreSoir()):getChapters(lecture1.getChapitreMatin());
        switch (chaptersList.size()){
            case 1:
                int chapter = Integer.parseInt(chaptersList.get(0));
                bibleList = Bible.getOneChapter(MainActivity.this, book, chapter, testament);
                break;

            case 2:
                int chapterStart = Integer.parseInt(chaptersList.get(0));
                int chapterEnd = Integer.parseInt(chaptersList.get(1));
                bibleList = Bible.getManyChapters(MainActivity.this,book,chapterStart,chapterEnd,testament);
                break;

            case 3:
                chapter = Integer.parseInt(chaptersList.get(0));
                int verseStart = Integer.parseInt(chaptersList.get(1));
                int verseEnd = Integer.parseInt(chaptersList.get(2));
                bibleList = Bible.getOneChapter(MainActivity.this, book, chapter,verseStart,verseEnd,testament);
                break;
        }

        StringBuilder stringBuilder = new StringBuilder();
        // Le passage
        stringBuilder.append(book);
        for(String strChapter:chaptersList){
            stringBuilder.append(strChapter+",");
        }
        for(Bible bible:bibleList){
            stringBuilder.append(bible.getVerseText());
        }

        return stringBuilder.toString();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        int itemId=menuItem.getItemId();
        switch (itemId){
            case R.id.navigation_item_param:
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                break;

            case R.id.navigation_item_tout_leplan:
                startActivity(new Intent(MainActivity.this,ListeLectureActivity.class));
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean settingsisSet = sp.getBoolean("settings_is_set",false);
        if(settingsisSet){
            mPlanChoosed = findViewById(R.id.tv_main_plan_choosed);
            int planChoosed = Integer.parseInt(sp.getString("plan","0"));
            String strPlanChoosed =
                    planChoosed <= 1 ? String.valueOf(planChoosed) +" an" :String.valueOf(planChoosed) +" ans";
            mPlanChoosed.setText("Plan choisi: "+strPlanChoosed);


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

        textToSpeech.stop();
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

    private void setLectureSettings(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean settingsisSet = sp.getBoolean("settings_is_set",false);
        if(!settingsisSet){
            startActivity(new Intent(MainActivity.this,ChoosePlanActivity.class));
        } else {
            //startService(new Intent(this, AlarmService.class));
        }
    }


}
