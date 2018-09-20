package com.sinohb.music.sdk.player;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class StringPlayerManager extends MusicPlayerManager<String> {

    public StringPlayerManager(MusicPlayer musicPlayer) {
        super(musicPlayer);
        mPlayQueue = new LinkedList<>();
    }

    @Override
    public void play(String url) {
        if (url.isEmpty()) {
            return;
        }
        if (!url.equals(mCurrentPlay)) {
            mCurrentPlay = url;
            if (mPlayListener != null) mPlayListener.onPlay(url);
            try {
                musicPlayer.play(url,false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void playAsync(String url) {
        if (url.isEmpty()) {
            return;
        }
        if (!url.equals(mCurrentPlay)) {
            mCurrentPlay = url;
            if (mPlayListener != null) mPlayListener.onPlay(url);
            try {
                musicPlayer.playAsync(url,false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void resumeSavedSong(int progress) {

    }

    //    @Override
//    protected void addSongsToQueue(List<String> datas) {
//
//    }
//
//    @Override
//    public void removeSongs(List<String> songs) {
//
//    }

}
