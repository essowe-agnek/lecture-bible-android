package adapters;

import android.app.Activity;
import android.content.Context;
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

import com.agnekdev.planlecturebible.R;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;

import java.util.List;

import models.Bible;

public class BibleAdapter extends RecyclerView.Adapter<BibleAdapter.BibleHolder> {
    List<Bible> bibleList;
    Activity mActivity;

    MyCallBack myCallBack;

    public interface MyCallBack{
        void listenerMethod(int position);
    }
    public BibleAdapter(Activity activity,List<Bible> bibleList,MyCallBack myCallBack){
        this.bibleList=bibleList;
        mActivity=activity;
        this.myCallBack=myCallBack;

    }
    @NonNull
    @Override
    public BibleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bible,parent,false);
        return new BibleHolder(layout);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull BibleHolder holder, final int position) {
        final Bible bible=bibleList.get(position);
        holder.mVerse.setText(String.valueOf(bible.getVerse()));
        holder.mversetext.setText(bible.getVerseText());
        holder.mversetext.setBackgroundColor(bible.getBgColor());

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

                            case R.id.bible_menu_item_highlight:
                                ColorPickerDialog.Builder colorPickerDialog=ColorPickerDialog.newBuilder();
                                colorPickerDialog.setColor(Color.YELLOW).show((FragmentActivity) mActivity);

                                myCallBack.listenerMethod(position);
                                break;

                            case R.id.bible_menu_item_copy:
                                final String book = bible.getEveningBook()!=null ? bible.getEveningBook() : bible.getMorningBook();
                                final int chapter = bible.getChapter();
                                final int verse= bible.getVerse();
                                final String verseText = bible.getVerseText();
                                String passage = String.format("%s %d : %d\n%s",book,chapter,verse,verseText);
                                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                                    clipboard.setText(passage);
                                } else {
                                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", passage);
                                    clipboard.setPrimaryClip(clip);
                                }
                                Toast.makeText(mActivity,"Verset copié avec succès",Toast.LENGTH_LONG).show();
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
