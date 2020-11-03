package com.agnekdev.bibleunan;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class AudioActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 100;
    Button btnPlay;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        btnPlay = findViewById(R.id.btn_play);
        mProgressBar = findViewById(R.id.play_progressBar);
        mProgressBar.setVisibility(View.GONE);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                try {
                    playAudio();
                } catch (IOException e) {
                    e.printStackTrace();
                }
               /* Intent intent = getIntent();
                String url=intent.getStringExtra("audio_url");
                Functions.agnekLog(url);
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(
                       new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                );
                try {
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

            }
        });
    }

    private void download() throws IOException {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef=storage.getReference();
        StorageReference audioRef=storageRef.child("test-plb2.amr");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
        }

        mProgressBar.setVisibility(View.VISIBLE);
        File rootDir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        File dir = new File(rootDir,"PlanLectureBible");
        dir.mkdir();
        File localFile = new File(dir,audioRef.getName());
        audioRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                mProgressBar.setVisibility(View.INVISIBLE);
                try {
                    playAudio();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(AudioActivity.this,"Error",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void playAudio() throws IOException {
        File rootDir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        File dir = new File(rootDir,"PlanLectureBible");
        File localFile = new File(dir,"test-plb2.amr");
        if(localFile.exists()){
//        Uri uri =Uri.fromFile(localFile);
            Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", localFile);
            Intent viewMediaIntent = new Intent();
            viewMediaIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            viewMediaIntent.setAction(android.content.Intent.ACTION_VIEW);
            viewMediaIntent.setDataAndType(uri, "audio/*");
            viewMediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(viewMediaIntent);

        } else {
            download();
        }
    }


}