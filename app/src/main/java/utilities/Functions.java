package utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
}
