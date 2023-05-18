package com.example.mymusic;

public class MusicF {
    int idMusic;

    String musicPath;

    public MusicF(int idMusic,String musicPath) {
        this.idMusic = idMusic;
        this.musicPath = musicPath;
    }

    public int getIdMusic() {
        return idMusic;
    }

    public String getMusicPath() {
        return musicPath;
    }


}
