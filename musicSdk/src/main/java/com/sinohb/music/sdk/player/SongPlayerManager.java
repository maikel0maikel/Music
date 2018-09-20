package com.sinohb.music.sdk.player;

import com.sinohb.music.sdk.entities.Song;

import java.io.IOException;
import java.util.LinkedList;

public class SongPlayerManager extends MusicPlayerManager<Song> {

    private static MusicPlayerManager<Song> manager;

    private SongPlayerManager(MusicPlayer musicPlayer) {
        super(musicPlayer);
        mPlayQueue = new LinkedList<>();
    }

    public static MusicPlayerManager<Song> getInstance(MusicPlayer musicPlayer) {
        if (manager == null) {
            synchronized (SongPlayerManager.class) {
                if (manager == null)
                    manager = new SongPlayerManager(musicPlayer);
            }
        }
        return manager;
    }

    @Override
    public void play(Song song) {
        if (song == null || song.getPath().isEmpty()) {
            return;
        }
        if (song.equals(mCurrentPlay)) {
            if (!isPlaying()) {
                resume();
            }
            return;
        }
        try {
            musicPlayer.play(song.getPath(),false);
            mCurrentPlay = song;
            findPlayPos();
            if (mPlayListener != null) mPlayListener.onPlay(song);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playAsync(Song song) {
        if (song == null || song.getPath().isEmpty()) {
            return;
        }
        if (song.equals(mCurrentPlay)) {
            if (isPlaying()) return;
            else resume();
        }
        try {
            musicPlayer.playAsync(song.getPath(),false);
            findPlayPos();
            mCurrentPlay = song;
            if (mPlayListener != null) mPlayListener.onPlay(song);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void resumeSavedSong(int progress) {
        if (mCurrentPlay == null || mCurrentPlay.getPath().isEmpty()) {
            return;
        }
        try {
            musicPlayer.resumeSavePlay(mCurrentPlay.getPath(), true, progress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
