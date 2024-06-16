package com.example.toysshop.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.toysshop.model.CartModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {CartModel.class},version = 1,exportSchema = false)
public abstract class CartDatabase extends RoomDatabase {

    public abstract CartDao cartDao();

    private static volatile CartDatabase INSTANCE;

    public static CartDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (CartDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    CartDatabase.class, "cart_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}
