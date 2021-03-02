package com.agnekdev.bibleunan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;

import models.Exhortation;

public class ListeExhortationsActivity extends AppCompatActivity {
    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    Spinner monthSpinner;
    FirebaseFirestore db;
    private FirestoreRecyclerAdapter firestoreRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_exhortations);

        mToolbar = findViewById(R.id.list_exho_appbar);
        mRecyclerView = findViewById(R.id.list_exho_recyclerview);
        monthSpinner = findViewById(R.id.list_exho_spinner_month);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Exhortations de l'année");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        /*populateRecyclerview(currentMonth+1);*/

        monthSpinner.setSelection(currentMonth);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

               populateRecyclerview(position+1);
               firestoreRecyclerAdapter.startListening();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }
    private void populateRecyclerview(int month){
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Query query = db.collection("ExhoMatin")
                .whereEqualTo("annee",currentYear)
                .whereEqualTo("mois",month)
                .orderBy("dateExho", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Exhortation> options = new FirestoreRecyclerOptions.Builder<Exhortation>()
                .setQuery(query,Exhortation.class)
                .build();
        firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Exhortation,ExhortationHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ExhortationHolder holder, int position, @NonNull final Exhortation model) {
                holder.mDesc.setText(model.getDesc());
                int isDownloaded = model.getIsDownloaded();
                if(isDownloaded==1){
                    holder.imDownload.setImageResource(R.drawable.ic_success);
                } else {
                    holder.imDownload.setImageResource(R.drawable.ic_download);
                }

                holder.imDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(ListeExhortationsActivity.this,String.valueOf(model.isDownloaded()),Toast.LENGTH_LONG).show();
                    }
                });
            }

            @NonNull
            @Override
            public ExhortationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exhortation,parent,false);
                return new ExhortationHolder(layout);
            }
        };

        mRecyclerView.setAdapter(firestoreRecyclerAdapter);
    }

    class ExhortationHolder extends RecyclerView.ViewHolder {
        TextView mDesc;
        ImageView imDownload;
        public ExhortationHolder(@NonNull View itemView) {
            super(itemView);
            mDesc = itemView.findViewById(R.id.item_exho_desc);
            imDownload = itemView.findViewById(R.id.item_exho_im_download);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        /*firestoreRecyclerAdapter.startListening();*/
        super.onStart();
    }

    @Override
    protected void onStop() {
        firestoreRecyclerAdapter.stopListening();
        super.onStop();
    }
}