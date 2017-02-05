package org.huebner.frederic.complaintapp.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class that creates and upgrades the local database.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "complaintapp.db";

    static final int DATABASE_VERSION = 3;

    SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Complaint.TABLE_NAME + " ("
                + Complaint.ID + " INTEGER PRIMARY KEY,"
                + Complaint.SERVER_ID + " LONG,"
                + Complaint.SERVER_VERSION + " LONG,"
                + Complaint.CONFLICT_SERVER_VERSION + " LONG,"
                + Complaint.SYNC_STATE + " TEXT,"
                + Complaint.NAME + " TEXT,"
                + Complaint.LOCATION + " TEXT,"
                + Complaint.COMPLAINT_TEXT + " TEXT,"
                + Complaint.PROCESSING_STATUS + " TEXT"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Complaint.TABLE_NAME);
        onCreate(db);
    }
}
