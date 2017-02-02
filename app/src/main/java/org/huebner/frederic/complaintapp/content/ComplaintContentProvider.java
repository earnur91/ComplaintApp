package org.huebner.frederic.complaintapp.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import static android.content.ContentResolver.CURSOR_DIR_BASE_TYPE;
import static android.content.ContentResolver.CURSOR_ITEM_BASE_TYPE;

/**
 *
 */
public class ComplaintContentProvider extends ContentProvider {

    private static final int COMPLAINTS = 10;
    private static final int COMPLAINT_ID = 20;

    public static final String BASE_PATH = "complaints";
    public static final String AUTHORITY = "org.huebner.frederic.complaintapp.content";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    private static final String CONTENT_TYPE = CURSOR_DIR_BASE_TYPE + "/complaints";
    private static final String CONTENT_ITEM_TYPE = CURSOR_ITEM_BASE_TYPE + "/complaint";

    private static final UriMatcher URIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URIMatcher.addURI(AUTHORITY, BASE_PATH, COMPLAINTS);
        URIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", COMPLAINT_ID);
    }

    public static Uri getUri(Long id) {
        return Uri.parse(CONTENT_URI + "/" + id);
    }

    private SQLiteHelper databaseHelper;

    @Override
    public boolean onCreate() {
        databaseHelper = new SQLiteHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        int uriType = URIMatcher.match(uri);

        switch (uriType) {
            case COMPLAINTS:
                return CONTENT_TYPE;
            case COMPLAINT_ID:
                return CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Set the table
        queryBuilder.setTables(Complaint.TABLE_NAME);

        int uriType = URIMatcher.match(uri);
        switch (uriType) {
            case COMPLAINTS:
                break;
            case COMPLAINT_ID:
                // Adding the ID to the original query
                queryBuilder.appendWhere(Complaint.ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        // cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int uriType = URIMatcher.match(uri);

        long id = 0;
        switch (uriType) {
            case COMPLAINTS:
                id = db.insert(Complaint.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // In case of existing Android observer, tell UI that something has changed:
        // getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int uriType = URIMatcher.match(uri);

        int rowsDeleted = 0;

        switch (uriType) {
            case COMPLAINTS:
                rowsDeleted = db.delete(Complaint.TABLE_NAME, selection, selectionArgs);
                break;

            case COMPLAINT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(Complaint.TABLE_NAME, Complaint.ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(Complaint.TABLE_NAME, Complaint.ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // In case of existing Android observer, tell UI that something has changed:
        // getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int uriType = URIMatcher.match(uri);

        int rowsUpdated = 0;

        switch (uriType) {
            case COMPLAINTS:
                rowsUpdated = db.update(Complaint.TABLE_NAME, values, selection, selectionArgs);
                break;

            case COMPLAINT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(Complaint.TABLE_NAME, values, Complaint.ID + "=" + id, null);

                } else {
                    rowsUpdated = db.update(Complaint.TABLE_NAME, values, Complaint.ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // In case of existing Android observer, tell UI that something has changed:
        // getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
