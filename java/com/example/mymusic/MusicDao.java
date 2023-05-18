package com.example.mymusic;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MusicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Music music);

    @Query("SELECT * FROM favourites")
    List<Music> getAllMusic();

    @Delete
    void delete(Music music);
}
