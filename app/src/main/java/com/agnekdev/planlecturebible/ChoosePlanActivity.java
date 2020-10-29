package com.agnekdev.planlecturebible;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.Calendar;

public class ChoosePlanActivity extends AppCompatActivity {
    Toolbar mToolbar;
    RadioGroup rgChoosePlan;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_plan);

        mToolbar = findViewById(R.id.choose_plan_appbar);
        btnSave = findViewById(R.id.btn_save_choose_plan);
        rgChoosePlan = findViewById(R.id.radioGroup_choose_plan);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Choix du plan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(rgChoosePlan.getCheckedRadioButtonId() == -1){
                    Toast.makeText(getApplicationContext(),"Veuillez choisir un plan",Toast.LENGTH_LONG).show();
                    return;
                }
                int rbOneId=R.id.rb_1an;
                int rbTwoId=R.id.rb_2ans;
                int rbThreeId=R.id.rb_3ans;
                int choice= rgChoosePlan.getCheckedRadioButtonId();

                int yearofSettings= Calendar.getInstance().get(Calendar.YEAR);
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ChoosePlanActivity.this);
                SharedPreferences.Editor editor=sp.edit();
                String plan="1";

                if(choice == rbOneId){
                    plan="1";
                } else if(choice == rbTwoId){
                    plan="2";
                } else if(choice == rbThreeId){
                    plan="3";
                }
                editor.putString("plan",plan);
                editor.putBoolean("settings_is_set",true);
                editor.putInt("year_of_settings",yearofSettings);
                editor.commit();
                Toast.makeText(getApplicationContext(),"Vos paramètres sont enrégistrés avec succès",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
