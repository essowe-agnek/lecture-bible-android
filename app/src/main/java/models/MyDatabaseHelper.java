package models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class MyDatabaseHelper extends SQLiteAssetHelper {
    static final String DB_NAME="plb.db";
    static final int DB_VERSION=2;
    public MyDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
}
