package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.agnekdev.bibleunan.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import models.Bible;
import utilities.Functions;

public class BibleAdapter extends RecyclerView.Adapter<BibleAdapter.BibleHolder> {
    List<Bible> bibleList;
    Activity mActivity;

    MyCallBack myCallBack;
    BottomNavigationView btnv;

    NavigableMap<String,Bible> mapBible = new TreeMap<>();

    public interface MyCallBack{
        void listenerMethod(int position);
    }
    public BibleAdapter(Activity activity,List<Bible> bibleList,MyCallBack myCallBack,BottomNavigationView btnv){
        this.bibleList=bibleList;
        mActivity=activity;
        this.myCallBack=myCallBack;
        this.btnv=btnv;

    }
    @NonNull
    @Override
    public BibleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bible,parent,false);
        return new BibleHolder(layout);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull final BibleHolder holder, final int position) {
        final Bible bible=bibleList.get(position);
        holder.mVerse.setText(String.valueOf(bible.getVerse()));
        holder.mversetext.setText(bible.getVerseText());
        int rawBgColoor = mActivity.getResources().getColor(R.color.bgColor);
        int rawTxtColoor = mActivity.getResources().getColor(R.color.textColor);
        holder.mversetext.setBackgroundColor(bible.getBgColor()==-1?rawBgColoor:bible.getBgColor());
        holder.mversetext.setTextColor(bible.getBgColor()==-1?rawTxtColoor:Color.BLACK);

        holder.imageViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopuMenu(view,bible,position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPopuMenu(view,bible,position);
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSelected=!bible.isSelected();
                bible.setSelected(isSelected);
                int bgColor= isSelected ? mActivity.getResources().getColor(R.color.selectColor):
                        mActivity.getResources().getColor(R.color.bgColor);
                holder.mversetext.setBackgroundColor(bgColor);

                btnv.setVisibility(Bible.getNbSelected()>0 ? View.VISIBLE : View.GONE);

                if(isSelected){
                    mapBible.put(bible.getId(),bible);
                } else {
                    mapBible.remove(bible.getId());
                }
            }
        });

        btnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.bible_bottom_navigation_menuitem_copy:
                        String headerAndContent = getBibleRangeSelected();
                        copyToClipboard(headerAndContent);
                        Toast.makeText(mActivity,"Contenu copié dans presse papier",Toast.LENGTH_LONG).show();
                        break;

                    case R.id.bible_bottom_navigation_menuitem_share:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, getBibleRangeSelected());
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        mActivity.startActivity(shareIntent);
                        break;

                }
                return false;
            }
        });

    }

    private String getBibleRangeSelected() {
        int i=0;
        String book="";
        Integer chapter=null;
        Integer verseStart=null;
        Integer verseEnd=null;
        String fullContent="";
        for(String key:mapBible.keySet()){
            Bible currentBible= mapBible.get(key);
            verseEnd = currentBible.getVerse();
            ++i;
            if(i==1){
               book=currentBible.getEveningBook()!=null ?currentBible.getEveningBook():currentBible.getMorningBook();
               chapter=currentBible.getChapter();
               verseStart=currentBible.getVerse();
            }
            int verse =currentBible.getVerse();
            String verseText = currentBible.getVerseText();
            fullContent+=String.valueOf(verseEnd)+" "+verseText+"\n";
        }
        String header = String.format("%s %d : verset %d à %d\n\n",book,chapter,verseStart,verseEnd);
        return header+fullContent;
    }

    private void showPopuMenu(View view, final Bible bible, final int position){
        PopupMenu popupMenu = new PopupMenu(mActivity,view);
        popupMenu.inflate(R.menu.bible_menu_popup);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.bible_menu_item_share:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, getVerseToCopyAndShare());
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        mActivity.startActivity(shareIntent);
                        break;

                    case R.id.bible_menu_item_highlight:
                        ColorPickerDialog.Builder colorPickerDialog=ColorPickerDialog.newBuilder();
                        colorPickerDialog.setColor(Color.YELLOW).show((FragmentActivity) mActivity);

                        myCallBack.listenerMethod(position);
                        break;

                    case R.id.bible_menu_item_copy:

                        copyToClipboard(getVerseToCopyAndShare());
                        Toast.makeText(mActivity,"Verset copié avec succès",Toast.LENGTH_LONG).show();
                        break;
                }
                return false;
            }

            private String getVerseToCopyAndShare() {
                final String book = bible.getEveningBook()!=null ? bible.getEveningBook() : bible.getMorningBook();
                final int chapter = bible.getChapter();
                final int verse= bible.getVerse();
                final String verseText = bible.getVerseText();
                return String.format("%s %d : %d\n%s",book,chapter,verse,verseText);
            }
        });
    }

    private void copyToClipboard(String passage){
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(passage);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", passage);
            clipboard.setPrimaryClip(clip);
        }
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
