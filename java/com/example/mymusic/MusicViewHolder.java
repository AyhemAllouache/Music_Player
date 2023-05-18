package com.example.mymusic;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class MusicViewHolder extends RecyclerView.ViewHolder {

    public ImageView musicImage;
    public TextView musicName;


    public MusicViewHolder(@NonNull View itemView) {
        super(itemView);
        findViews(itemView);
    }
    public void findViews(View view){
        musicImage = (ImageView) view.findViewById(R.id.musicImage);
        musicName = (TextView)view.findViewById(R.id.musicName);
    }

    public void setItem(final Music music){
        File file = new File(music.getFilePath());
//        String musicName = file.getName();
        String musicNameWithoutExtension = file.getName().replaceFirst("[.][^.]+$", "");

        musicName.setText(musicNameWithoutExtension);
//        musicName.setText(music.getFilePath());
            musicImage.setImageResource(R.drawable.musical_note);
    }


}
