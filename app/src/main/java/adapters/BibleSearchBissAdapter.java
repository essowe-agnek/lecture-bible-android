package adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agnekdev.planlecturebible.R;

import java.util.List;

import models.Bible;

import static utilities.Functions.stripAccents;

public class BibleSearchBissAdapter extends RecyclerView.Adapter<BibleSearchBissAdapter.MyVH>{
    List<Bible> bibleList;
    Context context;
    String query;

    public BibleSearchBissAdapter(Context context,List<Bible> bibleList,String query){
        this.context=context;
        this.bibleList=bibleList;
        this.query = query;
    }

    @NonNull
    @Override
    public MyVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verse_search,parent,false);
        return new MyVH(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVH holder, int position) {
        Bible bible= bibleList.get(position);
        final String book=bible.getEveningBook()!=null?bible.getEveningBook():bible.getMorningBook();
        final int chapter =bible.getChapter();
        final int verse = bible.getVerse();
        String passageFormated =String.format("%s %d : %d",book,chapter,verse);

        //***
        String fulltext = bible.getVerseText();
        String fulltextStriped = stripAccents(bible.getVerseText().toLowerCase());
        int start = fulltextStriped.indexOf(query);
        int endPos= query.length()+start;

        SpannableString ss = new SpannableString(bible.getVerseText());
        ColorStateList color = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.parseColor("#D81B60")});
        TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, color, null);
        ss.setSpan(highlightSpan,start,endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.mversetext.setText(ss);
        holder.mPassage.setText(passageFormated);

    }

    @Override
    public int getItemCount() {
        return bibleList.size();
    }

    class MyVH extends RecyclerView.ViewHolder {
        TextView mPassage;
        TextView mversetext;
        public MyVH(@NonNull View itemView) {
            super(itemView);
            mPassage= itemView.findViewById(R.id.item_verse_search_passage);
            mversetext= itemView.findViewById(R.id.item_verse_search_verse_text);

        }
    }
}
