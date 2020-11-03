package com.agnekdev.bibleunan;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.List;

import adapters.LectureAdapter;
import models.Lecture;
import utilities.Functions;

public class ListeLectureActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    LectureAdapter adapter;
    Toolbar mToolbar;
    Spinner mListMois;
    private LinearLayoutManager layoutManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_lecture);

        mRecyclerView=findViewById(R.id.recyclerview_liste_lecture);
        mToolbar=findViewById(R.id.liste_lecture_appbar);
        mListMois=findViewById(R.id.sp_list_mois);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        mListMois.setSelection(currentMonth);

        listeDuMois();
        goToTodayPostion();

    }

    private void goToTodayPostion() {
        int todayPosition = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        mRecyclerView.getLayoutManager().scrollToPosition(todayPosition-1);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    void listeDuMois(){
        mListMois.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                final int nombreAnnes = Functions.getNombreAnnees(getApplicationContext());
                final int rangAnnee = Functions.getRangAnnee(getApplicationContext());
                List<Lecture> lectureList=Lecture.getMonthLecture(getApplicationContext(),String.valueOf(i+1),nombreAnnes,rangAnnee);
                adapter = new LectureAdapter(ListeLectureActivity.this,lectureList);

                mRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
