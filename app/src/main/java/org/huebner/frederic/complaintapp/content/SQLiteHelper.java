package org.huebner.frederic.complaintapp.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "complaintapp.db";

    static final int DATABASE_VERSION = 3;

    SQLiteHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
