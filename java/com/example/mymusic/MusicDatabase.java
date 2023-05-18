package com.example.mymusic;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {Music.class}, version = 2, exportSchema = false)
public abstract class MusicDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "music_database";
    private static MusicDatabase instance;

    public static synchronized MusicDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            MusicDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract MusicDao musicDao();
}
