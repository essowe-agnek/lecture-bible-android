package utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.agnekdev.planlecturebible.TestamentsActivity;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Bible;

import static android.content.Context.MODE_PRIVATE;

public abstract class Functions {

    public static String getTodayFormated(){
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM");
        Date today = new Date();
        String todayFormated =sdf.format(today);
        return todayFormated;
    }

    public static int getNombreAnnees(Context context){
        SharedPreferences sp = context.getSharedPreferences("com.agnekdev.lecture",MODE_PRIVATE);
        final int nombreAnnee = sp.getInt("plan",0);
        return nombreAnnee;
    }

    public static int getRangAnnee(Context context) {
        SharedPreferences sp = context.getSharedPreferences("com.agnekdev.lecture",MODE_PRIVATE);
        final int plan = sp.getInt("plan",0);
        final int yearOfSettings = sp.getInt("year_of_settings",0);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int difference = currentYear-yearOfSettings;

        int yearRank=1;

        // Cas de 2ans
        switch (plan){
            // 1 an
            case 1:
                yearRank = 1;
                break;

            // 2ans
            case 2:
                if(difference == 1){
                    yearRank = 2;
                }
                break;

            // 3 ans
            case 3:
                if(difference==1){
                    yearRank = 2;
                } else if(difference == 2){
                    yearRank = 3;
                }
                break;
        }

        return yearRank;
    }

    public static String getDayName(String month,String day){
        String dayName="";
        int currentyear=Calendar.getInstance().get(Calendar.YEAR);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        String dateStr = String.valueOf(currentyear)+"-"+month+"-"+day;
        try {
            Date date = sdf.parse(dateStr);
            dayName = new SimpleDateFormat("EEE").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dayName;

    }

    public static void agnekLog(String content){
        Log.i("adev",content);
    }

    public static ArrayList<String> getChapters(String rawChapter){
        ArrayList<String> response= new ArrayList<>();
        // plusieurs chapitres
        if(rawChapter.contains(",")){
            String[] arrayChapters= rawChapter.split(",");
            String chapterStart =arrayChapters[0];
            String chapterEnd=arrayChapters[arrayChapters.length-1];
            response.add(chapterStart);
            response.add(chapterEnd);

            // 1 chapitre et quelques versets
        } else if(rawChapter.contains(":")){
            String[] arrayChapterVerse=rawChapter.split(":");
            String chapter= arrayChapterVerse[0];
            String verses=arrayChapterVerse[1];

            String[] arrayVerses = verses.split("-");
            String verseStart =arrayVerses[0];
            String verseEnd =arrayVerses[1];

            response.add(chapter);
            response.add(verseStart);
            response.add(verseEnd);

            //un chapitre
        } else {
            response.add(rawChapter);
        }

        return response;

    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++ A utilisser après
    void searchVersesSystem(Context context){

        List<Bible> bibleList =Bible.searchVerses(context,"Paul","nt");
        List<String> books= Bible.getBooks();

        Map<String,List<Bible>> map = new HashMap<>();
        for(String book:books){
            List<Bible> newBibleList = new ArrayList<>();
            for(Bible bible:bibleList){
                if(book.equals(bible.getMorningBook())){
                    Functions.agnekLog(bible.getMorningBook());
                    newBibleList.add(bible);
                }
            }
            map.put(book,newBibleList);
        }
        //Functions.agnekLog(String.valueOf(books.size()));
        Functions.agnekLog(map.get("Colossiens").toString());
    }

    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }
}
