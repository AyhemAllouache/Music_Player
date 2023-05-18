package com.example.mymusic;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_AUDIO;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.File;
import java.io.IOException;

public class MainActivityTest extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private int currentPosition;
    private File[] musicFiles;
    private ImageButton playButton;
    private ImageButton nextButton;
    private ImageButton previousButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playButton = findViewById(R.id.play);
        nextButton = findViewById(R.id.next);
        previousButton = findViewById(R.id.prev);
        // Check if the app has permission to read external storage
        if ((ContextCompat.checkSelfPermission(MainActivityTest.this,READ_MEDIA_AUDIO)| ContextCompat.checkSelfPermission(MainActivityTest.this,POST_NOTIFICATIONS)) != PackageManager.PERMISSION_GRANTED) {

            // Request permission if it hasn't been granted
            ActivityCompat.requestPermissions(MainActivityTest.this,
                    new String[]{READ_MEDIA_AUDIO},
                    1);
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{POST_NOTIFICATIONS},
//                    101);
        }

//        if (ContextCompat.checkSelfPermission(MainActivity.this,POST_NOTIFICATIONS)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Request permission if it hasn't been granted
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{POST_NOTIFICATIONS},
//                    101);
//        }


        String musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath();
        File musicFolder = new File(musicDirectory);
        musicFiles = musicFolder.listFiles();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Play next song when current song finishes
                playNextSong();
            }
        });

        // Set up initial song
        currentPosition = 0;
        setUpSong();

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getApplicationContext(), MusicService.class));
//                if (mediaPlayer.isPlaying()) {
//                    // Pause music if currently playing
//                    mediaPlayer.pause();
//                } else {
//                    // Start playing music if not playing
//                    mediaPlayer.start();
//                }
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Play next song
                playNextSong();
            }
        });


        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Play previous song
                playPreviousSong();
            }
        });

    }
    private void setUpSong() {
        try {
            // Set up media player with current song
            mediaPlayer.reset();
            mediaPlayer.setDataSource(musicFiles[currentPosition].getPath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.e("MainActivity", "Error setting up song: " + e.getMessage());
        }
    }

    private void playNextSong() {
        // Increment current position and play next song
        currentPosition = (currentPosition + 1) % musicFiles.length;
        setUpSong();
        mediaPlayer.start();
    }

    private void playPreviousSong() {
        // Decrement current position and play previous song
        currentPosition = (currentPosition - 1 + musicFiles.length) % musicFiles.length;
        setUpSong();
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }


}