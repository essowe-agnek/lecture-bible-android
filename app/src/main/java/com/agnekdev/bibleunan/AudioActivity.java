package com.agnekdev.bibleunan;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static utilities.Functions.hasActiveInternetConnection;
import static utilities.Functions.isNetworkAvailable;

public class AudioActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 100;
    Toolbar mToolbar;
    Button btnPlay;
    ProgressBar mProgressBar;

    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        btnPlay = findViewById(R.id.btn_play);
        mProgressBar = findViewById(R.id.play_progressBar);
        mToolbar = findViewById(R.id.audio_appbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Ecouter exhortation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

            }
        });
    }

    private void download() throws IOException {
        // get audio file ref from firestore
        if(!isNetworkAvailable(this) || !hasActiveInternetConnection(this)){
            Toast.makeText(AudioActivity.this,"Veuillez vous connecter à internet " +
                    "d'abord",Toast.LENGTH_LONG).show();
            return;
        }
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("ExhoMatin");
        DocumentReference docRef = colRef.document(getDayMonth());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc =task.getResult();
                    if(doc.exists() && doc !=null){
                        String audioName = doc.getString("audio");
                        downLoadfromStorage(audioName);

                    } else {
                        Toast.makeText(AudioActivity.this,"L'exhortation n'est pas encore disponible",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    private void downLoadfromStorage(String fileName){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef=storage.getReference();
        StorageReference audioRef=storageRef.child("matin").child(fileName);

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
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("ExhoMatin");
        DocumentReference docRef = colRef.document(getDayMonth());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc =task.getResult();
                    if(doc.exists() && doc !=null){
                        File rootDir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                        File dir = new File(rootDir,"PlanLectureBible");
                        fileName = doc.getString("audio");
                        File localFile = new File(dir,fileName);
                        if(localFile.exists()){
                            Uri uri = FileProvider.getUriForFile(AudioActivity.this, getApplicationContext().getPackageName() + ".provider", localFile);
                            Intent viewMediaIntent = new Intent();
                            viewMediaIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            viewMediaIntent.setAction(android.content.Intent.ACTION_VIEW);
                            viewMediaIntent.setDataAndType(uri, "audio/*");
                            viewMediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(viewMediaIntent);

                        } else {
                            try {
                                download();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Toast.makeText(AudioActivity.this,"L'exhortation du jour n'est " +
                                "pas encore disponible",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    static String getDayMonth(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM");
        String dayMonth = sdf.format(new Date());

        return dayMonth;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}