package models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class MyDatabaseHelper extends SQLiteAssetHelper {
    static final String DB_NAME="bibleunanv2.db";
    static final int DB_VERSION=4;
    public MyDatabaseHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
        setForcedUpgrade();
    }
}
