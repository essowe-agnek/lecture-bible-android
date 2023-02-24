package com.agnekdev.bibleunan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import models.Bible;
import models.Lecture;
import utilities.Functions;

import static utilities.Functions.agnekLog;
import static utilities.Functions.getChapters;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar mToolbar;
    TextToSpeech textToSpeech;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    BottomNavigationView bottomNavigationView;

    TextView mToday;
    TextView mLectureMatin;
    TextView mLectureSoir;
    TextView mPlanChoosed;
    Button btnDons;
    Button btnReadBible;

    LinearLayout llLectureMatin;
    LinearLayout llLectureSoir;
    LinearLayout llAudioMatin;
    LinearLayout llAudioSoir;
    LinearLayout llExhortationMatin;
    LinearLayout llExhortationSoir;
    ImageView imageViewAudioMatin;
    ImageView imageViewAudioSoir;

    private Menu menu;

    private ArrayList<String> chaptersAndVerses;
    private String eviningBook;
    private String passagesSoir;
    private String passagesMatin;
    private String morningBook;
    private ArrayList<String> morningChaptersAndVerses;
    private Lecture lecture1;

    private FirebaseFirestore db;

    private String eventOrigin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db =FirebaseFirestore.getInstance();

        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isNightMode =shp.getBoolean("mode_night",false);
        AppCompatDelegate
                .setDefaultNightMode(isNightMode?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);
        getDelegate().applyDayNight();


        //Vérifier si la lecture est paramétrée
        setLectureSettings();

        llLectureMatin = findViewById(R.id.ll_lecture_matin);
        llLectureSoir = findViewById(R.id.ll_lecture_soir);
        llAudioMatin= findViewById(R.id.ll_audio_matin);
        llAudioSoir= findViewById(R.id.ll_audio_soir);
        llExhortationMatin = findViewById(R.id.ll_exhortation_matin);
        llExhortationSoir = findViewById(R.id.ll_exhortation_soir);
        mToolbar = findViewById(R.id.main_appbar);
        mToday = findViewById(R.id.tv_main_today);
        mLectureMatin = findViewById(R.id.tv_main_lecture_matin);
        mLectureSoir = findViewById(R.id.tv_main_lecture_soir);
        bottomNavigationView = findViewById(R.id.main_bottom_navigation);
        btnDons = findViewById(R.id.btn_main_don);
        btnReadBible = findViewById(R.id.btn_read_bible);
        imageViewAudioMatin = findViewById(R.id.imageView_audio_matin);
        imageViewAudioSoir = findViewById(R.id.imageView_audio_soir);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Plan de lecture Bible");

        bottomNavigationView.getMenu().getItem(0).setChecked(false);


        configureDrawerLayout();
        configureNavigationView();


        initTextToSpeech();

        llAudioMatin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventOrigin="matin";
                if(!textToSpeech.isSpeaking()){
                    imageViewAudioMatin.setImageResource(R.mipmap.ic_stop_big);
                    myTextToSpeech("nt");
                } else {
                    imageViewAudioMatin.setImageResource(R.mipmap.ic_audio);
                    textToSpeech.stop();
                    //textToSpeech.shutdown();

                }
//                menu.findItem(R.id.main_menu_item_share).setVisible(false);
//                menu.findItem(R.id.main_menu_item_stop).setVisible(true);

            }
        });

        llAudioSoir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventOrigin="soir";
                if(!textToSpeech.isSpeaking()){
                    imageViewAudioSoir.setImageResource(R.mipmap.ic_stop_big);
                    myTextToSpeech("ot");
                } else {
                    imageViewAudioSoir.setImageResource(R.mipmap.ic_audio);
                    textToSpeech.stop();

                }
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

        llExhortationMatin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ExhortationActivity.class);
                startActivity(intent);
            }
        });

        llExhortationSoir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ExhortationActivity.class);
                startActivity(intent);
            }
        });

        btnDons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,DonsActivity.class));

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.bottom_menu_item_lire_tout:
                        startActivity(new Intent(MainActivity.this,ListeLectureActivity.class));
                        break;

                    case R.id.bottom_menu_item_about:
                        startActivity(new Intent(MainActivity.this,AboutActivity.class));
                        break;

                    case R.id.bottom_menu_item_email:
                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"frederic.agneketom@gmail.com"});
                        email.setType("message/rfc822");
                        startActivity(Intent.createChooser(email, "Choisir votre application de messagerie"));
                        break;
                }
                return false;
            }
        });

        btnReadBible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,TestamentsActivity.class));
            }
        });

        // gestion message exhortation disponible
        SharedPreferences shpf =getSharedPreferences("com.agnekdev.bibleunan.prefs",MODE_PRIVATE);
        Boolean exho_deja = shpf.getBoolean("exho_deja",false);
        if(exho_deja==false){
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            final View view = layoutInflater.inflate(R.layout.exhortation_dispo, null);
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Exhortation disponible");
            alertDialog.setCancelable(false);

            Button btnIntegrer = view.findViewById(R.id.btn_integrer_gp2);
            btnIntegrer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences shpf =getSharedPreferences("com.agnekdev.bibleunan.prefs",MODE_PRIVATE);
                    shpf.edit().putBoolean("exho_deja",true).commit();
                    String url = "https://chat.whatsapp.com/ENf2YRVXISGBbLWqPuQ4AA";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences shpf =getSharedPreferences("com.agnekdev.bibleunan.prefs",MODE_PRIVATE);
                    shpf.edit().putBoolean("exho_deja",true).commit();
                }
            });
            alertDialog.setView(view);
            alertDialog.show();
        }

    }

    public Context setupTheme(Context context) {

        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isNightMode =shp.getBoolean("mode_night",false);
        AppCompatDelegate
                .setDefaultNightMode(isNightMode?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);

        Resources res = context.getResources();
        int mode = res.getConfiguration().uiMode;
        Configuration config = new Configuration(res.getConfiguration());
        config.uiMode = mode;
        if (Build.VERSION.SDK_INT >= 17) {
            context = context.createConfigurationContext(config);
        } else {
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
        return context;
    }

    private void initTextToSpeech() {
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

    }

    private void myTextToSpeech (String testament){

        List<Bible> bibleList= new ArrayList<>();
        String book = testament.equals("ot") ? lecture1.getLivreSoir():lecture1.getLivreMatin();
        String chapters =testament.equals("ot") ? lecture1.getChapitreSoir() : lecture1.getChapitreMatin();
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
                chapter = Integer.parseInt(chaptersList.get(0).replaceAll("\\s",""));
                int verseStart = Integer.parseInt(chaptersList.get(1).replaceAll("\\s",""));
                int verseEnd = Integer.parseInt(chaptersList.get(2));
                bibleList = Bible.getOneChapter(MainActivity.this, book, chapter,verseStart,verseEnd,testament);
                break;
        }

        StringBuilder stringBuilder = new StringBuilder();
        // Le passage
        stringBuilder.append(book);
        stringBuilder.append(chapters);

        textToSpeech.speak(stringBuilder.toString(),TextToSpeech.QUEUE_ADD,null);
        for(Bible bible:bibleList){
            textToSpeech.speak(bible.getVerseText(),TextToSpeech.QUEUE_ADD,null);
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

        agnekLog(" le jour:"+String.valueOf(lecture1.getJour2()));
        if(lecture1.getLivreSoir()!=null){
            mLectureSoir.setText("Soir: "+ passagesSoir);
        } else {
            mLectureSoir.setText("Libre aujourd'hui");
        }

    }

    // override methods *******************************************************


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.main_menu_item_mode:
                SharedPreferences sp =PreferenceManager.getDefaultSharedPreferences(this);
                boolean isNightMode=!sp.getBoolean("mode_night",false);
                AppCompatDelegate
                        .setDefaultNightMode(isNightMode?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);

                SharedPreferences.Editor editor=sp.edit();
                editor.putBoolean("mode_night",isNightMode);
                editor.commit();
                break;

            case R.id.main_menu_item_share:
                String todayStr=mToday.getText().toString();
                String morningPassage = mLectureMatin.getText().toString();
                String eveningPassage = mLectureSoir.getText().toString();

                StringBuilder stringBuilder= new StringBuilder();
                stringBuilder.append("*"+todayStr+"*\n\n");
                stringBuilder.append("Lecture "+morningPassage+"\n");
                stringBuilder.append("Lecture "+eveningPassage);

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                break;

            case R.id.main_menu_item_stop:
                textToSpeech.stop();
                menu.findItem(R.id.main_menu_item_share).setVisible(true);
                menu.findItem(R.id.main_menu_item_stop).setVisible(false);
                break;
        }
        return super.onOptionsItemSelected(item);
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
                break;

            case R.id.navigation_item_bible:
                startActivity(new Intent(MainActivity.this,TestamentsActivity.class));
                break;
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
        SharedPreferences shpf =PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        float rate = (float) shpf.getInt("audio_rate",100)/100;
        textToSpeech.setSpeechRate(rate);
        textToSpeech.setPitch(rate);
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

    @Override
    protected void onDestroy() {
        if(textToSpeech!=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

}
