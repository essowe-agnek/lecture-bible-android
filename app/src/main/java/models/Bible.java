package models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class Bible {
    private String id;
    private String morningBook;
    private String eveningBook;
    private int chapter;
    private int verse;
    private String verseText;

    public Bible(Cursor cursor){
        this.id=cursor.getString(cursor.getColumnIndex("verseID"));
        this.morningBook=cursor.getString(cursor.getColumnIndex("morningBook"));
        this.eveningBook=cursor.getString(cursor.getColumnIndex("eveningBook"));
        this.chapter=cursor.getInt(cursor.getColumnIndex("chapter"));
        this.verse=cursor.getInt(cursor.getColumnIndex("startVerse"));
        this.verseText=cursor.getString(cursor.getColumnIndex("verseText"));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMorningBook() {
        return morningBook;
    }

    public void setMorningBook(String morningBook) {
        this.morningBook = morningBook;
    }

    public String getEveningBook() {
        return eveningBook;
    }

    public void setEveningBook(String eveningBook) {
        this.eveningBook = eveningBook;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public int getVerse() {
        return verse;
    }

    public void setVerse(int verse) {
        this.verse = verse;
    }

    public String getVerseText() {
        return verseText;
    }

    public void setVerseText(String verseText) {
        this.verseText = verseText;
    }

// ******************************************************************************************************

    public static List<Bible> getOneChapter(Context context, String book, int chapter, String testament){
        MyDatabaseHelper myDatabaseHelper= new MyDatabaseHelper(context);
        SQLiteDatabase database = myDatabaseHelper.getReadableDatabase();
        final String queryOt="SELECT * FROM bible WHERE eveningBook=? AND chapter=?";
        final String queryNt="SELECT * FROM bible WHERE morningBook=? AND chapter=?";
        final String query= testament.equals("ot") ? queryOt : queryNt;
        String[] args ={book,String.valueOf(chapter)};
        Cursor cursor = database.rawQuery(query,args);

        List<Bible> bibleList = new ArrayList<>();
        while (cursor.moveToNext()){
            Bible bible = new Bible(cursor);
            bibleList.add(bible);
        }
        cursor.close();
        database.close();
        return bibleList;

    }

    public static List<Bible> getOneChapter(Context context, String book, int chapter, int verseStart, int verseEnd, String testament){
        MyDatabaseHelper myDatabaseHelper= new MyDatabaseHelper(context);
        SQLiteDatabase database = myDatabaseHelper.getReadableDatabase();
        final String queryOt="SELECT * FROM bible WHERE eveningBook=? AND chapter=? AND startVerse BETWEEN ? AND ?";
        final String queryNt="SELECT * FROM bible WHERE morningBook=? AND chapter=? AND startVerse BETWEEN ? AND ?";
        final String query = testament.equals("ot") ? queryOt : queryNt;
        String[] args ={book,String.valueOf(chapter),String.valueOf(verseStart),String.valueOf(verseEnd)};
        Cursor cursor = database.rawQuery(query,args);

        List<Bible> bibleList = new ArrayList<>();
        while (cursor.moveToNext()){
            Bible bible = new Bible(cursor);
            bibleList.add(bible);
        }
        cursor.close();
        database.close();
        return bibleList;

    }
}
