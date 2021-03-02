package models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Exhortation {
    private String audio;
    private int mois;
    private int annee;
    private Date dateExho;
    private String desc;
    private int isDownloaded;


    public Exhortation(){}

    public Exhortation(String audio, int mois,int annee,Date dateExho,String desc,int isDownloaded) {
        this.audio = audio;
        this.mois = mois;
        this.dateExho = dateExho;
        this.desc = desc;
        this.annee = annee;
        this.isDownloaded = isDownloaded;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public int getMois() {
        return mois;
    }

    public void setMois(int mois) {
        this.mois = mois;
    }

    @ServerTimestamp
    public Date getDateExho() {
        return dateExho;
    }

    public void setDateExho(Date dateExho) {
        this.dateExho = dateExho;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getIsDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(int isDownloaded) {
        this.isDownloaded = isDownloaded;
    }
}
