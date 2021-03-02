package utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static androidx.core.content.ContextCompat.getSystemService;

public abstract class Functions {

    public static String getTodayFormated(){
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM");
        Date today = new Date();
        String todayFormated =sdf.format(today);
        return todayFormated;
    }
    // obtenir le numéro du jour dans le mois par exemple 31
    public static int getcurrentDayNumberInMonth(){
        SimpleDateFormat sdf = new SimpleDateFormat("d");
        Date today = new Date();
        String todayFormated =sdf.format(today);
        return Integer.parseInt(todayFormated);
    }

    // Obtenir le numéro du mois actuel
    public static String getCurrentMonthNumber(){

        int currentMonthNumber= Calendar.getInstance().get(Calendar.MONTH)+1;
        return  String.valueOf(currentMonthNumber);
    }

    public static int getNombreAnnees(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final int nombreAnnee = Integer.parseInt(sp.getString("plan","0"));
        return nombreAnnee;
    }

    public static int getRangAnnee(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final int plan = Integer.parseInt(sp.getString("plan","0"));
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

    public static String pluralize(int number,String text){

        String response = number <= 1 ? text : text+"s";
        return response;
    }

    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
