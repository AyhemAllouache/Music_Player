package com.example.mymusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Favourite extends AppCompatActivity {
    RecyclerView recyclerView;
    MusicAdapter musicAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        recyclerView = findViewById(R.id.myRecycle);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Music> musicList = MusicDatabase.getInstance(getApplicationContext()).musicDao().getAllMusic();

                // Pass the music list to the UI thread to update the RecyclerView
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Create an instance of the RecyclerView adapter
                        MusicDao musicDao = MusicDatabase.getInstance(getApplicationContext()).musicDao();
                        MusicAdapter mAdapter = new MusicAdapter(musicList, getApplicationContext(), musicDao);

                        // Set the adapter on the RecyclerView
                        RecyclerView recyclerView = findViewById(R.id.myRecycle);
                        recyclerView.setAdapter(mAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }
                });
            }
        }).start();


    }



}