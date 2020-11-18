package com.agnekdev.bibleunan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.jean.jcplayer.JcPlayerManagerListener;
import com.example.jean.jcplayer.general.JcStatus;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.ArrayList;
import java.util.Date;

import static utilities.Functions.haveNetworkConnection;

public class AudioActivity extends AppCompatActivity implements JcPlayerManagerListener {
    private static final int REQUEST_CODE = 100;
    Toolbar mToolbar;
    ProgressBar mProgressBar;
    JcPlayerView jcPlayerView;
    FloatingActionButton fab;
    TextView mDownload;
    ImageView imageViewSuccess;
    TextView mExhoTitle;

    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        mProgressBar = findViewById(R.id.play_progressBar);
        mToolbar = findViewById(R.id.audio_appbar);
        jcPlayerView =findViewById(R.id.jcplayer);
        fab=findViewById(R.id.fab_download);
        mDownload = findViewById(R.id.audio_tv_download);
        imageViewSuccess = findViewById(R.id.audio_im_success);
        mExhoTitle = findViewById(R.id.audio_tv_exho_title);

        mExhoTitle.setText(getExhortationTitle());

        jcPlayerView.setJcPlayerManagerListener(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Ecouter exhortation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgressBar.setVisibility(View.GONE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    download();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getExhortationTitle() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM yyyy");
        return "Exhortation du "+sdf.format(new Date());
    }

    private void checkAudioAvailable() {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("ExhoMatin");
        DocumentReference docRef = colRef.document(getCurrentDateFormated());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc =task.getResult();
                    if(doc.exists() && doc !=null){
                        String audioName = doc.getString("audio");
                       if(audioIsDownLoaded(audioName)){
                           mDownload.setText("Déjà téléchargé");
                           imageViewSuccess.setVisibility(View.VISIBLE);
                           initJcPlayer(audioName);
                           fab.hide();
                       } else {
                           mDownload.setText("Veuillez télécharger");
                           fab.show();
                       }

                    } else {
                        mDownload.setText("Pas encore disponible");
                        Toast.makeText(AudioActivity.this,"L'exhortation n'est pas encore disponible",Toast.LENGTH_LONG).show();
                    }
                } else {
                    mDownload.setText("Pas de connexion internet");
                    Toast.makeText(AudioActivity.this,"Il semble que vous n'êtes pas connectés à internet",Toast.LENGTH_LONG).show();
                }
            }

            private boolean audioIsDownLoaded(String audioName) {
                File musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                File audioFile = new File(musicDir,"PlanlectureBible/"+audioName);
                return audioFile.exists();
            }
        });

    }

    private void initJcPlayer(String audio){
        ArrayList<JcAudio> jcAudios = new ArrayList<>();
        File musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory,"PlanLectureBible/"+audio);
        jcAudios.add(JcAudio.createFromFilePath(file.toString()));
        jcPlayerView.initPlaylist(jcAudios, null);
        jcPlayerView.createNotification();
    }

    private void download() throws IOException {
        // get audio file ref from firestore

        if(!haveNetworkConnection(this)){
            Toast.makeText(AudioActivity.this,"Veuillez vous connecter à internet " +
                    "d'abord",Toast.LENGTH_LONG).show();
            return;
        }


        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("ExhoMatin");
        DocumentReference docRef = colRef.document(getCurrentDateFormated());
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
                imageViewSuccess.setVisibility(View.VISIBLE);
                onResume();

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
                        //permissions pour accéder à la mémoire
                        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
                            String permission=Manifest.permission.WRITE_EXTERNAL_STORAGE;
                            if(ContextCompat.checkSelfPermission(AudioActivity.this,permission) == PackageManager.PERMISSION_DENIED){
                                String[] arrPermissions={Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                ActivityCompat.requestPermissions(AudioActivity.this,arrPermissions,1);
                                return;
                            }
                        }
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
                } else {
                    Toast.makeText(AudioActivity.this,"Il semble que vous n'êtes pas connectés à " +
                            "internet",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    static String getDayMonth(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM");
        String dayMonth = sdf.format(new Date());

        return dayMonth;
    }

    String getCurrentDateFormated(){
        String response=null;
        Intent intent = getIntent();
        String origine= intent.getStringExtra("origine");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(origine.equals("liste")){
            response = intent.getStringExtra("date_sent");
        } else {
            response = sdf.format(new Date());
        }

        return response;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                try {
                    playAudio();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Autorisation requise");
                builder.setMessage("L'application a besoin d'avoir accès à votre mémoire de stockage" +
                        " pour pouvoir y  stocker l'exhortation afin de la jouer");
                builder.setPositiveButton("Ok",null);
                builder.show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAudioAvailable();
    }

    // ********************************************* jcplayer implement methods
    @Override
    public void onCompletedAudio() {


    }

    @Override
    public void onContinueAudio(JcStatus jcStatus) {

    }

    @Override
    public void onJcpError(Throwable throwable) {

    }

    @Override
    public void onPaused(JcStatus jcStatus) {

    }

    @Override
    public void onPlaying(JcStatus jcStatus) {

    }

    @Override
    public void onPreparedAudio(JcStatus jcStatus) {

    }

    @Override
    public void onStopped(JcStatus jcStatus) {

    }

    @Override
    public void onTimeChanged(JcStatus jcStatus) {

    }
}