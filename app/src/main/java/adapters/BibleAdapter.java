package adapters;

import android.app.Activity;
import android.app.AlertDialog;
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
import androidx.recyclerview.widget.RecyclerView;

import com.agnekdev.planlecturebible.R;

import java.util.List;

import models.Bible;
import utilities.Functions;

public class BibleAdapter extends RecyclerView.Adapter<BibleAdapter.BibleHolder>{
    List<Bible> bibleList;
    Activity mActivity;
    public BibleAdapter(Activity activity,List<Bible> bibleList){
        this.bibleList=bibleList;
        mActivity=activity;

    }
    @NonNull
    @Override
    public BibleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bible,parent,false);
        return new BibleHolder(layout);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull BibleHolder holder, int position) {
        final Bible bible=bibleList.get(position);
        holder.mVerse.setText(String.valueOf(bible.getVerse()));
        holder.mversetext.setText(bible.getVerseText());

        holder.imageViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mActivity,view);
                popupMenu.inflate(R.menu.bible_menu_popup);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.bible_menu_item_share:
                                Toast.makeText(mActivity,bible.getVerseText(),Toast.LENGTH_LONG).show();
                                break;
                        }
                        return false;
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return bibleList.size();
    }


    class BibleHolder extends RecyclerView.ViewHolder {
        TextView mVerse;
        TextView mversetext;
        ImageView imageViewMore;
        public BibleHolder(@NonNull View itemView) {
            super(itemView);
            mVerse= itemView.findViewById(R.id.item_bible_verse);
            mversetext= itemView.findViewById(R.id.item_bible_verse_text);
            imageViewMore = itemView.findViewById(R.id.item_bible_more);
        }
    }

}
