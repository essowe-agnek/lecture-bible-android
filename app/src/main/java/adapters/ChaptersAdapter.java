package adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.agnekdev.planlecturebible.BibleActivity;
import com.agnekdev.planlecturebible.BooksActivity;
import com.agnekdev.planlecturebible.ChaptersActivity;
import com.agnekdev.planlecturebible.R;

import java.util.ArrayList;
import java.util.List;

import static utilities.Functions.getChapters;

public class ChaptersAdapter extends RecyclerView.Adapter<ChaptersAdapter.ChapterHolder>{
    List<Integer> chapterList;
    Context context;

    public ChaptersAdapter(Context context, List<Integer> chapterList){
        this.chapterList =chapterList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter,parent,false);
        return new ChapterHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterHolder holder, int position) {
        final Integer chapter = chapterList.get(position);
        holder.mChapter.setText(String.valueOf(chapter));

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BibleActivity.class);

                String testament = BooksActivity.getTestament();
                String book= ChaptersActivity.getBook();
                ArrayList<String> chaptersAndVerses = getChapters(String.valueOf(chapter));
                String passages = book+" "+String.valueOf(chapter);

                intent.putStringArrayListExtra("chapters_verses",chaptersAndVerses);
                intent.putExtra("book",book);
                intent.putExtra("passages",passages);
                //intent.putExtra("passages_matin",passagesMatin);
                intent.putExtra("testament",testament);
                intent.putExtra("cat","bible");
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    class ChapterHolder extends RecyclerView.ViewHolder {
        TextView mChapter;
        CardView mCardView;
        public ChapterHolder(@NonNull View itemView) {
            super(itemView);
            mChapter = itemView.findViewById(R.id.item_chapter_name);
            mCardView = itemView.findViewById(R.id.item_chapter_cv);
            //btnBook = itemView.findViewById(R.id.btn_bible_book);
        }
    }
}
