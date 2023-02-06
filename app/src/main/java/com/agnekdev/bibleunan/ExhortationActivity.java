package com.agnekdev.bibleunan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ExhortationActivity extends AppCompatActivity {
    Toolbar toolbar;
    Button btnIntegrer;
    Button btnSendMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhortation);

        toolbar = findViewById(R.id.exhortation_appbar);
        btnIntegrer = findViewById(R.id.btn_integrer_groupe);
        btnSendMsg = findViewById(R.id.btn_send_msg);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ecouter les exhortations");

        btnIntegrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://chat.whatsapp.com/ENf2YRVXISGBbLWqPuQ4AA";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PackageManager pm=getPackageManager();
                try {

                    String url = "https://api.whatsapp.com/send/?phone=22899751567";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                } catch (Exception e) {
                    Toast.makeText(ExhortationActivity.this, "Problème survenu. Veuillez contacter le concepteur de l'application", Toast.LENGTH_SHORT).show();
                }
            }


        });
    }
}