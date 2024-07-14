package com.example.toysshop.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.toysshop.model.User;
import com.example.toysshop.model.User1;

public class UserRepository {

    private DatabaseHelper dbHelper;

    public UserRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void insertUser(String phone, String address) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PHONE, phone);
        values.put(DatabaseHelper.COLUMN_ADDRESS, address);

        db.insert(DatabaseHelper.TABLE_USERS, null, values);
        db.close();
    }

    public User1 getUserByPhone(String phone) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseHelper.COLUMN_PHONE,
                DatabaseHelper.COLUMN_ADDRESS
        };

        String selection = DatabaseHelper.COLUMN_PHONE + " = ?";
        String[] selectionArgs = { phone };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        User1 user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User1(
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADDRESS))
            );
            cursor.close();
        }

        db.close();

        return user;
    }

    public String getAddress(String phone) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseHelper.COLUMN_ADDRESS
        };

        String selection = DatabaseHelper.COLUMN_PHONE + " = ?";
        String[] selectionArgs = { phone };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        String address = null;
        if (cursor != null && cursor.moveToFirst()) {
            address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADDRESS));
            cursor.close();
        }

        db.close();

        return address;
    }


}
