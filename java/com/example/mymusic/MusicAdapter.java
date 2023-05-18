package com.example.mymusic;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicViewHolder> {
    private final List<Music> mData;
    private final Context context;
    private MusicDao musicDao;

    MediaPlayer mediaPlayer;


    public MusicAdapter(List<Music> mData, Context context, MusicDao musicDao) {
        this.mData = mData;
        this.context = context;
        this.musicDao = musicDao;
    }


    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_music, parent, false);

        return new MusicViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        final Music music = mData.get(position);
        File file = new File(music.getFilePath());
        String musicName = file.getName();
        String musicNameWithoutExtension = file.getName().replaceFirst("[.][^.]+$", "");


        if (mData != null && position >= 0 && position < mData.size()) {
            // Set the item on the ViewHolder
//            holder.setItem(mData.get(position));
            holder.musicName.setText(musicNameWithoutExtension);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, music.getFilePath(), Toast.LENGTH_SHORT).show();
//                playTrackS(music.getFilePath());
                context.startService(new Intent(context, MusicService.class));

                playFavourite(music.getFilePath());
//                MusicService musicService = new MusicService();
//                musicService.updateNotification(musicName);


            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                deleteMusic(music);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void deleteMusic(Music music) {
        // Remove music from the list
        int position = mData.indexOf(music);
        mData.remove(position);
        notifyItemRemoved(position);

        // Delete music from the database
        new Thread(new Runnable() {
            @Override
            public void run() {
                musicDao.delete(music);
            }
        }).start();
    }
    private void playFavourite(String fplay) {
        Intent intent = new Intent("PLayFavouriteBroadcast");
        intent.putExtra("fPlay", fplay);
        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
    }



}
