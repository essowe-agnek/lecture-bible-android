package adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.agnekdev.planlecturebible.R;

import java.util.List;

import models.Lecture;
import utilities.Functions;

public class LectureAdapter extends RecyclerView.Adapter<LectureAdapter.LectureHolder> {
    List<Lecture> lectureList;

    public LectureAdapter(List<Lecture> lectureList){
        this.lectureList=lectureList;

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
        Lecture lecture=lectureList.get(position);
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
        public LectureHolder(@NonNull View itemView) {
            super(itemView);
            mJour= itemView.findViewById(R.id.tv_item_jour);
            mLectureMatin=itemView.findViewById(R.id.tv_item_lecture_matin);
            mLectureSoir=itemView.findViewById(R.id.tv_item_lecture_soir);
            mDayName = itemView.findViewById(R.id.tv_item_lecture_day);
            cvSoir = itemView.findViewById(R.id.cardview_item_lecture_soir);
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
