package com.example.toysshop.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "searchHistory.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "search_history";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_QUERY = "query";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_QUERY + " TEXT" +
                    ");";

    public SearchHistoryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertQuery(String query) {
        if (!isQueryExists(query)) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_QUERY, query);
            db.insert(TABLE_NAME, null, values);
        }
    }

    public List<String> getAllQueries() {
        List<String> queries = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_QUERY}, null, null, null, null, null);

        while (cursor.moveToNext()) {
            queries.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUERY)));
        }
        cursor.close();
        return queries;
    }

    public void deleteQuery(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void clearHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }
    public boolean isQueryExists(String query) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = COLUMN_QUERY + " = ?";
        String[] selectionArgs = { query };
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_QUERY}, selection, selectionArgs, null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

}
