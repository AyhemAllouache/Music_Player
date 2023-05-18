package com.example.mymusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class Download extends AppCompatActivity {


    Button download;
    private EditText link;
    private TextView directory;

    static MediaPlayer mediaPlayer;
    String condition;
    static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        download = findViewById(R.id.dw);
        directory = findViewById(R.id.directory);
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        link = findViewById(R.id.thelink);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        MusicDao musicDao = MusicDatabase.getInstance(getApplicationContext()).musicDao();
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = link.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String filePath = DownloadUtils.downloadFile(url,directory);
                        Music music = new Music(filePath);
                        musicDao.insert(music);
                    }
                }).start();


            }
        });

    }
    public class DownloadUtils {
//        private File internalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        private static final String MUSIC_DIRECTORY = Environment.DIRECTORY_MUSIC;

        public static String downloadFile(String fileUrl, TextView textView) {
            try {
                File internalDir = Environment.getExternalStoragePublicDirectory(MUSIC_DIRECTORY);
                String fileName = extractFileName(fileUrl);
                File outputFile = new File(internalDir, fileName);
                String  musicPath = outputFile.getAbsolutePath();
                System.out.println(musicPath);

                URL url = new URL(fileUrl);
                HttpURLConnection connection;
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.connect();
                if (url.getProtocol().equalsIgnoreCase("https")) {
                    connection = (HttpsURLConnection) url.openConnection();
                } else {
                    connection = (HttpURLConnection) url.openConnection();
                }

                int fileLength = connection.getContentLength();

                InputStream input = connection.getInputStream();
                FileOutputStream output = new FileOutputStream(outputFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalBytesRead = 0;

                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    int progress = (int) ((totalBytesRead * 100) / fileLength);
                }

                output.close();
                input.close();


                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(musicPath + "   click to play ");

                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                playFavourite(musicPath);
                            }
                        });
                    }
                });
                return musicPath;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private static String extractFileName(String fileUrl) {
            int index = fileUrl.lastIndexOf("/");
            if (index != -1) {
                return fileUrl.substring(index + 1);
            }
            return "file";
        }

        private static void playFavourite(String fplay) {
            Intent intent = new Intent("PLayFavouriteBroadcast");
            intent.putExtra("fPlay", fplay);
            LocalBroadcastManager.getInstance(progressBar.getContext()).sendBroadcast(intent);
        }



    }


//    public static void playMusic(String filePath) {
//        try {
//            if (mediaPlayer == null) {
//                mediaPlayer = new MediaPlayer();
//            } else {
//                mediaPlayer.reset();
//            }
//
//            mediaPlayer.setDataSource(filePath);
//
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    mediaPlayer.start();
//                }
//            });
//
//            mediaPlayer.prepareAsync();
//        } catch (IOException e) {
//            e.printStackTrace();
//            // Handle any errors that occur during playback initialization
//        }
//    }


}