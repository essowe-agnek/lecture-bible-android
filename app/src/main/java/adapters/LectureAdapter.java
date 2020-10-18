package adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.agnekdev.planlecturebible.BibleActivity;
import com.agnekdev.planlecturebible.MainActivity;
import com.agnekdev.planlecturebible.R;

import java.util.ArrayList;
import java.util.List;

import models.Lecture;
import utilities.Functions;

import static utilities.Functions.getChapters;

public class LectureAdapter extends RecyclerView.Adapter<LectureAdapter.LectureHolder> {
    List<Lecture> lectureList;
    Activity mActivity;

    public LectureAdapter(Activity activity,List<Lecture> lectureList){
        this.lectureList=lectureList;
        this.mActivity=activity;
    }
    @NonNull
    @Override
    public LectureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lecture,parent,false);
        return new LectureHolder(layout);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull LectureHolder holder, int position) {
        final Lecture lecture=lectureList.get(position);
        String livreChapitreMatin=lecture.getLivreMatin()+" "+lecture.getChapitreMatin();
        String livreChapitreSoir=lecture.getLivreSoir()+" "+lecture.getChapitreSoir();
        String[] arrayJour=lecture.getJour().split(" ");
        holder.mLectureMatin.setText(livreChapitreMatin);
        holder.mJour.setText(getJourFormated(Integer.parseInt(arrayJour[0])));

        //get day Name
        String dayName=Functions.getDayName(lecture.getMois(),lecture.getJour());
        holder.mDayName.setText(dayName);

        if(lecture.getLivreSoir() == null){
            holder.mLectureSoir.setText("Libre");
            holder.cvSoir.setBackgroundColor(Color.parseColor("#C0C0C0"));
        }
        else {
            holder.mLectureSoir.setText(livreChapitreSoir);

            holder.cvSoir.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        holder.imageViewMoreMatin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mActivity,view);
                popupMenu.inflate(R.menu.lecture_menu_popup);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch(menuItem.getItemId()){
                            case R.id.lecture_menu_item_read:
                                String book =lecture.getLivreMatin();
                                ArrayList<String> chaptersAndVerses =getChapters(lecture.getChapitreMatin());
                                String passages = lecture.getLivreMatin()+" "+lecture.getChapitreMatin();

                                Intent intent = new Intent(mActivity, BibleActivity.class);
                                intent.putStringArrayListExtra("chapters_verses",chaptersAndVerses);
                                intent.putExtra("passages",passages);
                                intent.putExtra("book",book);
                                //intent.putExtra("passages_matin",passagesMatin);
                                intent.putExtra("testament","nt");
                                intent.putExtra("cat","plan");
                                mActivity.startActivity(intent);
                                break;
                        }
                        return false;
                    }
                });
            }
        });

        holder.imageViewMoreSoir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mActivity,view);
                popupMenu.inflate(R.menu.lecture_menu_popup);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch(menuItem.getItemId()){
                            case R.id.lecture_menu_item_read:
                                String book = lecture.getLivreSoir();
                                ArrayList<String> chaptersAndVerses =getChapters(lecture.getChapitreSoir());
                                String passages = lecture.getLivreSoir()+" "+lecture.getChapitreSoir();

                                Intent intent = new Intent(mActivity,BibleActivity.class);
                                intent.putStringArrayListExtra("chapters_verses",chaptersAndVerses);
                                intent.putExtra("book",book);
                                intent.putExtra("passages",passages);
                                intent.putExtra("testament","ot");
                                intent.putExtra("cat","plan");
                                mActivity.startActivity(intent);
                        }
                        return false;
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return lectureList.size();
    }

    class LectureHolder extends RecyclerView.ViewHolder {
        TextView mJour;
        TextView mLectureMatin;
        TextView mLectureSoir;
        TextView mDayName;
        CardView cvSoir;
        ImageView imageViewMoreMatin;
        ImageView imageViewMoreSoir;
        public LectureHolder(@NonNull View itemView) {
            super(itemView);
            mJour= itemView.findViewById(R.id.tv_item_jour);
            mLectureMatin=itemView.findViewById(R.id.tv_item_lecture_matin);
            mLectureSoir=itemView.findViewById(R.id.tv_item_lecture_soir);
            mDayName = itemView.findViewById(R.id.tv_item_lecture_day);
            cvSoir = itemView.findViewById(R.id.cardview_item_lecture_soir);
            imageViewMoreMatin = itemView.findViewById(R.id.imageView_item_lecture_matin_more);
            imageViewMoreSoir = itemView.findViewById(R.id.imageView_item_lecture_soir_more);
        }
    }

    static String getJourFormated(int jour){
        String response="";
        if(jour<10){
            response="0"+String.valueOf(jour);
        } else {
            response =String.valueOf(jour);
        }
        return response;
    }
}
