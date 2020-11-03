package adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.agnekdev.bibleunan.BibleActivity;
import com.agnekdev.bibleunan.R;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.Bible;
import utilities.Functions;

import static utilities.Functions.getChapters;

public class BibleSearchAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> booksList;
    private HashMap<String, List<Bible>> verseExpanded;
    private String query;

    public BibleSearchAdapter(Context context,List<String> booksList,HashMap<String,List<Bible>> verseExpanded,String query){
        this.context=context;
        this.booksList=booksList;
        this.verseExpanded=verseExpanded;
        this.query = query;
    }

    @Override
    public int getGroupCount() {
        return booksList.size();
    }

    @Override
    public int getChildrenCount(int bookPosition) {
        return this.verseExpanded.get(this.booksList.get(bookPosition))
                .size();
    }

    @Override
    public Object getGroup(int i) {
        return booksList.get(i);
    }

    @Override
    public Object getChild(int bookListPosition, int verseExpandedPosition) {
        return verseExpanded.
                get(booksList.get(bookListPosition)).
                get(verseExpandedPosition);
    }

    @Override
    public long getGroupId(int bookPosition) {
        return bookPosition;
    }

    @Override
    public long getChildId(int bookListPosition, int verseExpandedPosition) {
        return verseExpandedPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {
        final String book=(String)getGroup(i);
        if(convertView == null){
            LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_book_search,null);
        }

        TextView mBook =convertView.findViewById(R.id.item_book_search_name);
        TextView mCpte =convertView.findViewById(R.id.item_book_search_nb_verses);

        String verseFormated = Functions.pluralize(getChildrenCount(i),"verset");
        String strCpt =String.format("( %d %s ) ",getChildrenCount(i),verseFormated);
        mBook.setText(book);
        mCpte.setText(strCpt);
        return convertView;
    }

    @Override
    public View getChildView(int bookListPosition, final int verseExpandedPosition, boolean isLastchild, View convertView, ViewGroup viewGroup) {
        final Bible bible = (Bible) getChild(bookListPosition, verseExpandedPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_verse_search, null);
        }
        TextView mVerseText = (TextView) convertView.findViewById(R.id.item_verse_search_verse_text);
        TextView mPassage = (TextView) convertView.findViewById(R.id.item_verse_search_passage);

        final String testament = bible.getEveningBook() != null ? "ot" : "nt";

        final String book=bible.getEveningBook()!=null?bible.getEveningBook():bible.getMorningBook();
        final int chapter =bible.getChapter();
        final int verse = bible.getVerse();
        final String passageFormated =String.format("%s %d : %d",book,chapter,verse);
        final String passages = book+" "+String.valueOf(chapter);

        String fulltextStriped = stripAccents(bible.getVerseText().toLowerCase());
        int start = fulltextStriped.indexOf(query);
        int endPos= query.length()+start;

        SpannableString ss = new SpannableString(bible.getVerseText());
        ColorStateList color = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.parseColor("#D81B60")});
        TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, color, null);
        ss.setSpan(highlightSpan,start,endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mVerseText.setText(ss);
        mPassage.setText(passageFormated);

        mVerseText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context,String.valueOf(bible.getVerse()),Toast.LENGTH_LONG).show();
                ArrayList<String> chaptersAndVerses = getChapters(String.valueOf(chapter));
                Intent intent = new Intent(context, BibleActivity.class);
                intent.putStringArrayListExtra("chapters_verses",chaptersAndVerses);
                intent.putExtra("book",book);
                intent.putExtra("passages",passages);
                intent.putExtra("testament",testament);
                intent.putExtra("cat","bible");
                intent.putExtra("position",verse);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public static String stripAccents(String s)
    {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

}
