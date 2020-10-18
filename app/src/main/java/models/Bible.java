package models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Bible {
    private String id;
    private String morningBook;
    private String eveningBook;
    private int chapter;
    private int verse;
    private String verseText;
    private int bgColor;
    private static List<String> books;

    public Bible(Cursor cursor){
        this.id=cursor.getString(cursor.getColumnIndex("verseID"));
        this.morningBook=cursor.getString(cursor.getColumnIndex("morningBook"));
        this.eveningBook=cursor.getString(cursor.getColumnIndex("eveningBook"));
        this.chapter=cursor.getInt(cursor.getColumnIndex("chapter"));
        this.verse=cursor.getInt(cursor.getColumnIndex("startVerse"));
        this.verseText=cursor.getString(cursor.getColumnIndex("verseText"));
        this.bgColor =cursor.getInt(cursor.getColumnIndex("bgColor"));
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

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public static List<String> getBooks() {
        return books;
    }


    // ******************************************************************************************************


    @Override
    public String toString() {
        return this.verseText;
    }

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

    public static List<Bible> getManyChapters(Context context, String book, int chapterStart, int chapterEnd, String testament){
        MyDatabaseHelper myDatabaseHelper= new MyDatabaseHelper(context);
        SQLiteDatabase database = myDatabaseHelper.getReadableDatabase();

        final String queryOt="SELECT * FROM bible WHERE eveningBook=? AND chapter BETWEEN ? AND ?";
        final String queryNt="SELECT * FROM bible WHERE morningBook=? AND chapter BETWEEN ? AND ?";
        final String query = testament.equals("ot") ? queryOt : queryNt;
        String[] args ={book,String.valueOf(chapterStart),String.valueOf(chapterEnd)};
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

    public static long highLightBible(Context context,String verseId,int color){
        MyDatabaseHelper myDatabaseHelper= new MyDatabaseHelper(context);
        SQLiteDatabase database = myDatabaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("bgColor",color);
        String where ="verseID=?";
        String[] args = {verseId};
        long resultat=database.update("bible",contentValues,where,args);
        database.close();
        return resultat;

    }

    public static List<String> getBooks(Context context,String testament){
        MyDatabaseHelper myDatabaseHelper= new MyDatabaseHelper(context);
        SQLiteDatabase database = myDatabaseHelper.getReadableDatabase();
        final String queryOt="SELECT distinct eveningBook FROM bible WHERE eveningBook IS NOT NULL";
        final String queryNt="SELECT distinct morningBook FROM bible WHERE morningBook IS NOT NULL";
        final String query = testament.equals("ot") ? queryOt : queryNt;
        Cursor cursor = database.rawQuery(query,null);

        List<String> bookList = new ArrayList<>();
        while (cursor.moveToNext()){
            String bookName = cursor.getString(0);
            bookList.add(bookName);
        }

        cursor.close();
        database.close();
        return bookList;

    }

    public static List<Integer> getChapters(Context context,String testament,String book){
        MyDatabaseHelper myDatabaseHelper= new MyDatabaseHelper(context);
        SQLiteDatabase database = myDatabaseHelper.getReadableDatabase();
        final String queryOt="SELECT distinct chapter FROM bible WHERE eveningBook = ?";
        final String queryNt="SELECT distinct chapter FROM bible WHERE morningBook = ?";
        final String query = testament.equals("ot") ? queryOt : queryNt;
        String[] args = {book};
        Cursor cursor = database.rawQuery(query,args);

        List<Integer> chapterList = new ArrayList<>();
        while (cursor.moveToNext()){
            int chapter = cursor.getInt(0);
            chapterList.add(chapter);
        }

        cursor.close();
        database.close();
        return chapterList;

    }

    public static List<Bible> searchVerses(Context context,String textSearch,String testament){
        MyDatabaseHelper myDatabaseHelper= new MyDatabaseHelper(context);
        SQLiteDatabase database = myDatabaseHelper.getReadableDatabase();

        final String queryOt="SELECT * FROM bible WHERE eveningBook IS NOT NULL AND verseText like ?";
        final String queryNt="SELECT * FROM bible WHERE morningBook IS NOT NULL AND verseText like ?";
        final String query = testament.equals("ot") ? queryOt : queryNt;
        String[] args ={"%"+textSearch+"%"};
        Cursor cursor = database.rawQuery(query,args);

        List<Bible> bibleList = new ArrayList<>();
        Set<String> booksSetList = new LinkedHashSet<>();
        List<String> booksList = new ArrayList<>();
        while (cursor.moveToNext()){
            Bible bible = new Bible(cursor);
            bibleList.add(bible);
            String book = testament.equals("ot") ? bible.getEveningBook(): bible.getMorningBook();
            booksSetList.add(book);
        }
        cursor.close();
        database.close();
        booksList.addAll(booksSetList);
        books=booksList;
        return bibleList;
    }


}
