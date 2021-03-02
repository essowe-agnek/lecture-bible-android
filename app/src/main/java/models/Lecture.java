package models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import utilities.Functions;

public class Lecture {
    private long id;
    private String livreMatin;
    private String livreSoir;
    private String chapitreMatin;
    private String chapitreSoir;
    private String jour;
    private int nombreAnnees;
    private int rangAnnee;
    private String mois;
    private int jour2;

    public Lecture(Cursor cursor){
        this.id=cursor.getInt(cursor.getColumnIndex("ID"));
        this.livreMatin=cursor.getString(cursor.getColumnIndex("LIVRE_MATIN"));
        this.livreSoir=cursor.getString(cursor.getColumnIndex("LIVRE_SOIR"));
        this.chapitreMatin=cursor.getString(cursor.getColumnIndex("CHAPITRE_MATIN"));
        this.chapitreSoir=cursor.getString(cursor.getColumnIndex("CHAPITRE_SOIR"));
        this.jour=cursor.getString(cursor.getColumnIndex("JOUR"));
        this.nombreAnnees=cursor.getInt(cursor.getColumnIndex("NB_ANNEE"));
        this.rangAnnee=cursor.getInt(cursor.getColumnIndex("RANG_ANNEE"));
        this.mois=cursor.getString(cursor.getColumnIndex("MOIS"));
        this.jour2 = cursor.getInt(cursor.getColumnIndex("JOUR2"));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLivreMatin() {
        return livreMatin;
    }

    public void setLivreMatin(String livreMatin) {
        this.livreMatin = livreMatin;
    }

    public String getLivreSoir() {
        return livreSoir;
    }

    public void setLivreSoir(String livreSoir) {
        this.livreSoir = livreSoir;
    }

    public String getChapitreMatin() {
        return chapitreMatin;
    }

    public void setChapitreMatin(String chapitreMatin) {
        this.chapitreMatin = chapitreMatin;
    }

    public String getChapitreSoir() {
        return chapitreSoir;
    }

    public void setChapitreSoir(String chapitreSoir) {
        this.chapitreSoir = chapitreSoir;
    }

    public String getJour() {
        return jour;
    }

    public void setJour(String jour) {
        this.jour = jour;
    }

    public int getNombreAnnees() {
        return nombreAnnees;
    }

    public void setNombreAnnees(int nombreAnnees) {
        this.nombreAnnees = nombreAnnees;
    }

    public int getRangAnnee() {
        return rangAnnee;
    }

    public void setRangAnnee(int rangAnnee) {
        this.rangAnnee = rangAnnee;
    }

    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }

    public int getJour2() {
        return jour2;
    }

    public void setJour2(int jour2) {
        this.jour2 = jour2;
    }
    //****************************************************************************************

    public static Lecture getTodayLecture(Context context, int nombreAnnees, int rangAnnee){
        MyDatabaseHelper myDatabaseHelper= new MyDatabaseHelper(context);
        SQLiteDatabase database= myDatabaseHelper.getReadableDatabase();
        String query="";
        Cursor cursor=null;

        switch (nombreAnnees){
            case 1:
                query = "SELECT *, 1 AS NB_ANNEE,1 AS RANG_ANNEE FROM lecture where MOIS=? AND JOUR2=?";
                String[] args1 ={Functions.getCurrentMonthNumber(),String.valueOf(Functions.getcurrentDayNumberInMonth())};
                cursor = database.rawQuery(query,args1);
                break;

            case 2:
                query ="SELECT ln.ID,ln.MOIS,ln.JOUR,ln.JOUR2,l.livre_matin,l.CHAPITRE_MATIN,b.name_fr " +
                        "AS LIVRE_SOIR,ln.CHAPITRE_SOIR,ln.NB_ANNEE,ln.RANG_ANNEE " +
                        "FROM lecture_2ans ln JOIN lecture l ON ln.JOUR=l.JOUR " +
                        "LEFT JOIN book_en b ON ln.livre_soir=b.name_en " +
                        "WHERE ln.MOIS=? AND ln.JOUR2=? AND ln.RANG_ANNEE=?";
                String[] args2 = {Functions.getCurrentMonthNumber(),
                        String.valueOf(Functions.getcurrentDayNumberInMonth()),
                        String.valueOf(rangAnnee)};
                cursor=database.rawQuery(query,args2);
                break;

            case 3:
                query ="SELECT ln.ID,ln.MOIS,ln.JOUR,ln.JOUR2,l.livre_matin,l.CHAPITRE_MATIN,b.name_fr " +
                        "AS LIVRE_SOIR,ln.CHAPITRE_SOIR,ln.NB_ANNEE,ln.RANG_ANNEE " +
                        "FROM lecture_3ans ln JOIN lecture l ON ln.JOUR=l.JOUR " +
                        "LEFT JOIN book_en b ON ln.livre_soir=b.name_en " +
                        "WHERE ln.MOIS=? AND ln.JOUR2=? AND ln.RANG_ANNEE=?";
                String[] args3 = {Functions.getCurrentMonthNumber(),
                        String.valueOf(Functions.getcurrentDayNumberInMonth()),
                        String.valueOf(rangAnnee)};
                cursor=database.rawQuery(query,args3);
                break;
        }

        Lecture lecture=null;
        if(cursor.moveToFirst()){
            lecture = new Lecture(cursor);
        }
        cursor.close();
        database.close();
        return lecture;
    }

    public static Lecture getTodayLecture_old(Context context, int nombreAnnees, int rangAnnee){
        MyDatabaseHelper myDatabaseHelper= new MyDatabaseHelper(context);
        SQLiteDatabase database= myDatabaseHelper.getReadableDatabase();
        String query="";
        Cursor cursor=null;

        switch (nombreAnnees){
            case 1:
                query = "SELECT *, 1 AS NB_ANNEE,1 AS RANG_ANNEE FROM lecture where JOUR=?";
                String[] args1 ={Functions.getTodayFormated()};
                cursor = database.rawQuery(query,args1);
                break;

            case 2:
                query ="SELECT ln.ID,ln.MOIS,ln.JOUR,l.livre_matin,l.CHAPITRE_MATIN,b.name_fr " +
                        "AS LIVRE_SOIR,ln.CHAPITRE_SOIR,ln.NB_ANNEE,ln.RANG_ANNEE " +
                        "FROM lecture_2ans ln JOIN lecture l ON ln.JOUR=l.JOUR " +
                        "LEFT JOIN book_en b ON ln.livre_soir=b.name_en " +
                        "WHERE ln.JOUR=? AND ln.RANG_ANNEE=?";
                String[] args2 = {Functions.getTodayFormated(),String.valueOf(rangAnnee)};
                cursor=database.rawQuery(query,args2);
                break;

            case 3:
                query ="SELECT ln.ID,ln.MOIS,ln.JOUR,l.livre_matin,l.CHAPITRE_MATIN,b.name_fr " +
                        "AS LIVRE_SOIR,ln.CHAPITRE_SOIR,ln.NB_ANNEE,ln.RANG_ANNEE " +
                        "FROM lecture_3ans ln JOIN lecture l ON ln.JOUR=l.JOUR " +
                        "LEFT JOIN book_en b ON ln.livre_soir=b.name_en " +
                        "WHERE ln.JOUR=? AND ln.RANG_ANNEE=?";
                String[] args3 = {Functions.getTodayFormated(),String.valueOf(rangAnnee)};
                cursor=database.rawQuery(query,args3);
                break;
        }

        Lecture lecture=null;
        if(cursor.moveToFirst()){
            lecture = new Lecture(cursor);
        }
        cursor.close();
        database.close();
        return lecture;
    }

    public static List<Lecture> getListLecture(Context context, int nombreAnnees, boolean isToday){
        List<Lecture> lectureList = new ArrayList<>();
        MyDatabaseHelper myDatabaseHelper= new MyDatabaseHelper(context);
        SQLiteDatabase database= myDatabaseHelper.getReadableDatabase();
        String query="";
        Cursor cursor=null;

        // pour aujourd'hui
        if(isToday){

            String todayFormated = Functions.getTodayFormated();
            String[] args={todayFormated};
            if(nombreAnnees == 1){
                query="SELECT * FROM lecture";
            } else if(nombreAnnees==2){
                query ="SELECT ln.ID,ln.MOIS,ln.JOUR,l.livre_matin,l.CHAPITRE_MATIN,b.name_fr " +
                        "AS LIVRE_SOIR,ln.CHAPITRE_SOIR,ln.NB_ANNEE,ln.RANG_ANNEE " +
                        "FROM lecture_2ans ln JOIN lecture l ON ln.JOUR=l.JOUR " +
                        "LEFT JOIN book_en b ON ln.livre_soir=b.name_en " +
                        "WHERE ln.JOUR=?";
            } else if(nombreAnnees==3){
                query ="SELECT ln.ID,ln.MOIS,ln.JOUR,l.livre_matin,l.CHAPITRE_MATIN,b.name_fr " +
                        "AS LIVRE_SOIR,ln.CHAPITRE_SOIR,ln.NB_ANNEE,ln.RANG_ANNEE " +
                        "FROM lecture_3ans ln JOIN lecture l ON ln.JOUR=l.JOUR " +
                        "LEFT JOIN book_en b ON ln.livre_soir=b.name_en " +
                        "WHERE ln.JOUR=?";
            }
            cursor=database.rawQuery(query,args);
        } else

            // Récupérer toute la liste
        {
            if(nombreAnnees == 1){
                query="SELECT * FROM lecture";
            } else if(nombreAnnees==2){
                query ="SELECT ln.ID,ln.MOIS,ln.JOUR,l.livre_matin,l.CHAPITRE_MATIN,b.name_fr " +
                        "AS LIVRE_SOIR,ln.CHAPITRE_SOIR,ln.NB_ANNEE,ln.RANG_ANNEE " +
                        "FROM lecture_2ans ln JOIN lecture l ON ln.JOUR=l.JOUR " +
                        "LEFT JOIN book_en b ON ln.livre_soir=b.name_en";
            } else if(nombreAnnees==3){
                query ="SELECT ln.ID,ln.MOIS,ln.JOUR,l.livre_matin,l.CHAPITRE_MATIN,b.name_fr " +
                        "AS LIVRE_SOIR,ln.CHAPITRE_SOIR,ln.NB_ANNEE,ln.RANG_ANNEE " +
                        "FROM lecture_3ans ln JOIN lecture l ON ln.JOUR=l.JOUR " +
                        "LEFT JOIN book_en b ON ln.livre_soir=b.name_en";
            }
            cursor=database.rawQuery(query,null);

        }

        while(cursor.moveToNext()){
            Lecture lecture= new Lecture(cursor);
            lectureList.add(lecture);
        }
        cursor.close();
        database.close();
        return lectureList;
    }

    public static List<Lecture> getMonthLecture(Context context,String month,int nombreAnnees,int rangAnnee){
        List<Lecture> lectureList= new ArrayList<>();
        MyDatabaseHelper myDatabaseHelper= new MyDatabaseHelper(context);
        SQLiteDatabase database= myDatabaseHelper.getReadableDatabase();
        Cursor cursor= null;
        String query="";

        switch (nombreAnnees){
            case 1:
                String[] args = {month};
                query="SELECT *, 1 AS NB_ANNEE,1 AS RANG_ANNEE FROM lecture where MOIS=?";
                cursor=database.rawQuery(query,args);
                break;

            case 2:
                String[] args2 = {month,String.valueOf(rangAnnee)};
                query ="SELECT ln.ID,ln.MOIS,ln.JOUR,ln.JOUR2,l.livre_matin,l.CHAPITRE_MATIN,b.name_fr " +
                        "AS LIVRE_SOIR,ln.CHAPITRE_SOIR,ln.NB_ANNEE,ln.RANG_ANNEE " +
                        "FROM lecture_2ans ln JOIN lecture l ON ln.JOUR=l.JOUR " +
                        "LEFT JOIN book_en b ON ln.livre_soir=b.name_en " +
                        "WHERE ln.MOIS=? AND ln.RANG_ANNEE=?";
                cursor=database.rawQuery(query,args2);
                break;

            case 3:
                String[] args3 = {month,String.valueOf(rangAnnee)};
                query ="SELECT ln.ID,ln.MOIS,ln.JOUR,ln.JOUR2,l.livre_matin,l.CHAPITRE_MATIN,b.name_fr " +
                        "AS LIVRE_SOIR,ln.CHAPITRE_SOIR,ln.NB_ANNEE,ln.RANG_ANNEE " +
                        "FROM lecture_3ans ln JOIN lecture l ON ln.JOUR=l.JOUR " +
                        "LEFT JOIN book_en b ON ln.livre_soir=b.name_en " +
                        "WHERE ln.MOIS=? AND ln.RANG_ANNEE=?";
                cursor=database.rawQuery(query,args3);
                break;
        }

        while (cursor.moveToNext()){
            Lecture lecture = new Lecture(cursor);
            lectureList.add(lecture);
        }
        cursor.close();
        database.close();
        return lectureList;
    }
}
