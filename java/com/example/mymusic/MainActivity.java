package com.example.mymusic;

//import static android.Manifest.permission.POST_NOTIFICATIONS;
//import static android.Manifest.permission.READ_MEDIA_AUDIO;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int REQUEST_CODE = 1;
    private MediaPlayer mediaPlayer;
    private File[] musicFiles;
    private ImageButton playButton;
    private int currentPosition;
    private ImageButton nextButton;
    private ImageButton fav;

    private ImageButton pauseButton;
//    private Button download;
//
//    private Button favourites;


    String musicName;
    private ImageButton previousButton;

    private TextView musicTitle;

    //    -----------------
    public static final int STORAGE_PERMISSION = 0,
            ALL_FILES_PERMISSION = 2;
    String musicPath;
    String condition;
    private boolean isButtonClicked = false;

//    -----------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playButton = findViewById(R.id.play);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Music Player");
        setSupportActionBar(toolbar);
        nextButton = findViewById(R.id.next);
//        favourites = findViewById(R.id.favorites);
//        download = findViewById(R.id.download);
        pauseButton = findViewById(R.id.pause);
        fav = findViewById(R.id.fav);
        previousButton = findViewById(R.id.prev);
        musicTitle = findViewById(R.id.musicTitle);


        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
        musicName = "music1";
        currentPosition = 0;
        String musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath();
        File musicFolder = new File(musicDirectory);
        musicFiles = musicFolder.listFiles();
//        String path = musicDirectory + "/"+ musicName+".mp3";
        mediaPlayer = new MediaPlayer();

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startService(new Intent(getApplicationContext(), MusicService.class));
                playButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);

            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isButtonClicked = !isButtonClicked;
                sendActionToMusicService("PlayPause");
                if (isButtonClicked) {
                    pauseButton.setImageResource(R.drawable.ic_play);
                } else {
                    pauseButton.setImageResource(R.drawable.ic_pause);
                }


            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActionToMusicService("Next");

            }
        });


        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActionToMusicService("Previous");

            }
        });


        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Music added to favourite", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MusicDao musicDao = MusicDatabase.getInstance(getApplicationContext()).musicDao();
                        Music music = new Music(musicPath);
                        musicDao.insert(music);
                    }
                }).start();


            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getApplicationContext(), MusicService.class));
    }

    private BroadcastReceiver musicNameReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String musicName = intent.getStringExtra("musicName");

            if (musicName != null) {
                musicTitle.setText(musicName);
            }
        }
    };

    private BroadcastReceiver pathReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            musicPath = intent.getStringExtra("musicPath");
            if (musicName != null) {
//                System.out.println("this is music path : "+ musicPath);

            }
        }
    };


    private BroadcastReceiver ConditionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            condition = intent.getStringExtra("condition");

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("MusicNameBroadcast");
        IntentFilter intentFilter1 = new IntentFilter("PathBroadcast");
        IntentFilter intentFilter2 = new IntentFilter("ConditionBroadcast");

        LocalBroadcastManager.getInstance(this).registerReceiver(musicNameReceiver, intentFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(pathReceiver, intentFilter1);
        LocalBroadcastManager.getInstance(this).registerReceiver(pathReceiver, intentFilter2);

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(musicNameReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(pathReceiver);

    }

    private void sendActionToMusicService(String action) {
        Intent intent = new Intent("MusicActionBroadcast");
        intent.putExtra("action", action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.downl) {

            Intent i = new Intent(getApplicationContext(), Download.class);
            startActivity(i);

        }
        if (item.getItemId() == R.id.favo) {

            Intent j = new Intent(getApplicationContext(), Favourite.class);
            startActivity(j);

        }

        return false;
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }
}