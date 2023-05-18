package com.example.mymusic;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.widget.ImageButton;
//import android.support.v4.media.session.MediaSessionCompat;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {
    private static final String CHANNEL_ID = "MusicPlayerNotificationChannel";
    private static final int NOTIFICATION_ID = 110;
    private MyReceiver rec;
    CharSequence channelName = "My Channel";
    String musicName;
    private File[] musicFiles;
    private MediaPlayer mediaPlayer;
    private int currentPosition;
    private List<String> mSongs;
    String mPath;

    private ImageButton playButton;

    private int mCurrentSongIndex;
    public String trackname;


    private BroadcastReceiver musicActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("action");
            if (action != null) {
                if (action.equals("Next")) {
                    playNextTrack();
                } else if (action.equals("Previous")) {
                    playPreviousTrack();
                }else if (action.equals("PlayPause")) {
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                    }else{
                        mediaPlayer.start();
                    }
                }
            }
        }
    };

    private BroadcastReceiver FavouriteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPath = intent.getStringExtra("fPlay");
            if (mPath != null) {
                System.out.println("this is the path"+ mPath);
                playMusic(mPath);
                updateNotification(trackname);

//                    mediaPlayer.setDataSource(action);
//                    mediaPlayer.prepare();
//                    mediaPlayer.start();

            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
          trackname = getMusicNameAtPosition(currentPosition);

        currentPosition = 0;
            mSongs = new ArrayList<>();
            musicName = "music1";
//            get all music and put them in mSongs list
            mSongs = getAllMusicFiles();
//            String musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath();
//            File musicFolder = new File(musicDirectory);
//            musicFiles = musicFolder.listFiles();
//            String path = musicDirectory + "/"+ musicName+".mp3";
            mediaPlayer = new MediaPlayer();
            playTrack(currentPosition);

            try {
//                mediaPlayer.setDataSource(musicFiles[currentPosition].getPath());
                rec = new MyReceiver();
                registerReceiver(rec,new IntentFilter("PlayPause"));
                registerReceiver(rec,new IntentFilter("Next"));
                registerReceiver(rec,new IntentFilter("Previous"));
                IntentFilter intentFilter = new IntentFilter("MusicActionBroadcast");
                IntentFilter intentFilter1 = new IntentFilter("PLayFavouriteBroadcast");
                LocalBroadcastManager.getInstance(this).registerReceiver(musicActionReceiver, intentFilter);
                LocalBroadcastManager.getInstance(this).registerReceiver(FavouriteReceiver, intentFilter1);

//                mediaPlayer.prepare();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Exception of type : " + e.toString());
                e.printStackTrace();
            }




    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        PendingIntent pPPendingIntent = PendingIntent.getBroadcast(this, 0, new
                Intent("PlayPause"), PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pPPendingIntent1 = PendingIntent.getBroadcast(this, 0, new
                Intent("Next"), PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pPPendingIntent2 = PendingIntent.getBroadcast(this, 0, new
                Intent("Previous"), PendingIntent.FLAG_IMMUTABLE);

//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        String channelId = "my_channel_id";
        CharSequence channelName = "My Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Notification notification =
                new NotificationCompat.Builder(this, channelId)
                        .setContentTitle("Music PLayer Ayhem")
                        .setSmallIcon(R.drawable.musical_note)
                        .setContentText(trackname)
                        .setSound(null)
                        .addAction(R.drawable.musical_note,"Previous",pPPendingIntent2)
                        .addAction(R.drawable.musical_note,"PlayPause",pPPendingIntent)
                        .addAction(R.drawable.musical_note,"Next",pPPendingIntent1)
                        .setContentIntent(pendingIntent)
                        .build();
//        startForeground(1, notification);
        notificationManager.notify(1, notification);
        sendMusicNameBroadcast(trackname);
        mediaPlayer.start();
        return START_STICKY;
    }
    public class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();



            if (action.equals("PlayPause")){
                if (mediaPlayer.isPlaying()){
//                    conditionalIcon("pause");
                    mediaPlayer.pause();
                }else{
                    mediaPlayer.start();
//                    conditionalIcon("play");
                }
            }
//            ----------nEXT--------------
            if (action.equals("Next")){
                playNextTrack();

            }

//            ------------------Previous----------------

            if (action.equals("Previous")){
                playPreviousTrack();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(musicActionReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(FavouriteReceiver);
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        unregisterReceiver(rec);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }





    public List<String> getAllMusicFiles() {
        List<String> musicFiles = new ArrayList<>();

        // Get the root directory of your music files
        File musicDirectory = Environment.getExternalStorageDirectory();

//        System.out.println(musicDirectory.getPath());

        // Recursive function to traverse the directory and its subdirectories
        findMusicFiles(musicDirectory, musicFiles);

        return musicFiles;
    }

    private void findMusicFiles(File directory, List<String> musicFiles) {
        // Get all files and directories within the current directory
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // If it's a directory, recursively call the function
                    findMusicFiles(file, musicFiles);
                } else {
                    // If it's a file, check if it has an MP3 extension
                    if (file.getName().endsWith(".mp3") || file.getName().endsWith(".m4a")) {
                        musicFiles.add(file.getAbsolutePath());
                    }
                }
            }
        }
    }


    private void playTrack(int trackIndex) {
        if (trackIndex < 0 || trackIndex >= mSongs.size()) {
            // Invalid track index, do nothing
            return;
        }

        try {
            // Reset the MediaPlayer
            mediaPlayer.reset();

            // Set the data source to the selected music file
            mediaPlayer.setDataSource(mSongs.get(trackIndex));

            // Prepare the MediaPlayer
            mediaPlayer.prepare();

            // Start playback
            mediaPlayer.start();

            // Update the current track index
            currentPosition = trackIndex;


        } catch (IOException e) {
            e.printStackTrace();
            // Handle any errors that occur during playback
        }
    }

    private void playNextTrack() {
        int totalTracks = mSongs.size();

        if (totalTracks > 0) {
            // Increment the current track index
            currentPosition++;

            if (currentPosition >= totalTracks) {
                // If the current track index exceeds the total number of tracks,
                // wrap around to the first track
                currentPosition = 0;
            }

            // Play the next track
            playTrack(currentPosition);
            String musicName = getMusicNameAtPosition(currentPosition);
            sendMusicNameBroadcast(musicName);
            updateNotification(musicName);
            sendPathProdcast(getMusicPathAtPosition(currentPosition));
        }
    }


    private void playPreviousTrack() {
        int totalTracks = mSongs.size();

        if (totalTracks > 0) {
            // Decrement the current track index
            currentPosition--;

            if (currentPosition < 0) {
                // If the current track index is less than 0,
                // wrap around to the last track
                currentPosition = totalTracks - 1;
            }

            // Play the previous track
            playTrack(currentPosition);
            String musicName = getMusicNameAtPosition(currentPosition);
            sendMusicNameBroadcast(musicName);
            updateNotification(musicName);
            sendPathProdcast(getMusicPathAtPosition(currentPosition));

        }
    }

    private List<String> getAllMusicNames() {
        List<String> musicNames = new ArrayList<>();

        // Retrieve all music files
        List<String> musicFilePaths = getAllMusicFiles();

        // Extract music names from file paths
        for (String filePath : musicFilePaths) {
            File file = new File(filePath);
            String musicName = file.getName();
            String musicNameWithoutExtension = musicName.substring(0, musicName.lastIndexOf('.'));
            musicNames.add(musicNameWithoutExtension);
        }

        return musicNames;
    }




    private String getMusicNameAtPosition(int position) {
        List<String> musicNames = getAllMusicNames();

        if (position >= 0 && position < musicNames.size()) {
            return musicNames.get(position);
        }

        return null;  // Return null if the position is out of bounds
    }


    private String getMusicPathAtPosition(int position) {
        List<String> musicPaths = getAllMusicFiles();

        if (position >= 0 && position < musicPaths.size()) {
            return musicPaths.get(position);
        }

        return null;  // Return null if the position is out of bounds
    }

    private String getMusicNameAtCurrentTrack() {
        List<String> musicNames = getAllMusicNames();

        if (currentPosition >= 0 && currentPosition < musicNames.size()) {
            return mSongs.get(currentPosition);
        }

        return null; // Return null if the current track index is out of bounds
    }

    private void sendMusicNameBroadcast(String musicName) {
        Intent intent = new Intent("MusicNameBroadcast");
        intent.putExtra("musicName", musicName);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    private void sendPathProdcast(String musicName) {
        Intent intent = new Intent("PathBroadcast");
        intent.putExtra("musicPath", musicName);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void conditionalIcon(String condition) {
        Intent intent = new Intent("ConditionBroadcast");
        intent.putExtra("condition", condition);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void playMusic(String filePath) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            } else {
                mediaPlayer.reset();
            }

            mediaPlayer.setDataSource(filePath);

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });

            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any errors that occur during playback initialization
        }
    }

    @SuppressLint("MissingPermission")
    public void updateNotification(String musicName) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        PendingIntent pPPendingIntent = PendingIntent.getBroadcast(this, 0, new
                Intent("PlayPause"), PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pPPendingIntent1 = PendingIntent.getBroadcast(this, 0, new
                Intent("Next"), PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pPPendingIntent2 = PendingIntent.getBroadcast(this, 0, new
                Intent("Previous"), PendingIntent.FLAG_IMMUTABLE);

//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        String channelId = "my_channel_id";
        CharSequence channelName = "My Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        Notification notification =
                new NotificationCompat.Builder(this, channelId)
                        .setContentTitle("Music Player Ayhem")
                        .setSmallIcon(R.drawable.musical_note)
                        .setContentText(musicName)
                        .addAction(R.drawable.ic_play, "Previous", pPPendingIntent2)
                        .addAction(R.drawable.ic_play, "PlayPause", pPPendingIntent)
                        .setSound(Uri.parse(NotificationCompat.GROUP_KEY_SILENT))
                        .addAction(R.drawable.ic_play, "Next", pPPendingIntent1)
                        .setContentIntent(pendingIntent)
                        .build();

        NotificationManagerCompat mn = NotificationManagerCompat.from(this);
        mn.notify(1, notification);
    }



}


