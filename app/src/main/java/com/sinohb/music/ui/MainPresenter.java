package com.sinohb.music.ui;


import android.support.annotation.NonNull;

import com.sinohb.base.BaseView;
import com.sinohb.music.play.IMediaChangeListener;
import com.sinohb.music.play.IMusicPlayListener;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.play.ISongDeleteListener;
import com.sinohb.music.sdk.entities.Song;
import com.sinohb.music.sdk.player.PlayingSongListener;

import java.util.List;

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View mView;
    private IMusicPlayManager musicPlayManager;

    //private List<IMusicPlayListener> listeners = new ArrayList<>();
    public MainPresenter(@NonNull IMusicPlayManager musicPlayManager) {
        this.musicPlayManager = musicPlayManager;
    }

    @Override
    public void takeView(BaseView view) {
        mView = (MainContract.View) view;
    }

    @Override
    public void dropView() {
        musicPlayManager.destroy();
//        listeners.clear();
//        listeners = null;
        musicPlayManager = null;
        mView = null;
    }


    @Override
    public void play(Song song) {
        musicPlayManager.play(song);
    }

    @Override
    public void playNext() {
        musicPlayManager.playNext();
    }

    @Override
    public void playPre() {
        musicPlayManager.playPre();
    }

    @Override
    public boolean pause() {
        return musicPlayManager.pause();
    }

    @Override
    public boolean resume() {
        return musicPlayManager.resume();
    }

    @Override
    public void setMode(int mode) {
        musicPlayManager.setMode(mode);
    }

    @Override
    public int getMode() {
        return musicPlayManager.getMode();
    }

    @Override
    public boolean isPlaying() {
        return musicPlayManager.isPlaying();
    }

    @Override
    public void seekTo(int position) {
        musicPlayManager.seekTo(position);
    }

    @Override
    public int getCurrentPos() {
        return musicPlayManager.getCurrentPos();
    }

    @Override
    public int getAudioSessionId() {
        return musicPlayManager.getAudioSessionId();
    }

    @Override
    public void removePlaySong(Song song) {
        musicPlayManager.removePlaySong(song);
    }

    @Override
    public void addSongToPlay(Song song) {
        musicPlayManager.addSongToPlay(song);
    }

    @Override
    public void removeSongs(List<Song> songs) {
        musicPlayManager.removeSongs(songs);
    }

    @Override
    public void clearPlayQueue() {
        musicPlayManager.clearPlayQueue();
    }

    @Override
    public void onSongDeleted(int type) {
        musicPlayManager.onSongDeleted(type);
    }

    @Override
    public Song getCurrentPlay() {
        return musicPlayManager.getCurrentPlay();
    }

    @Override
    public long getCurrentPlayId() {
        return musicPlayManager.getCurrentPlayId();
    }

    @Override
    public void pushPlayQueue(List<Song> songs) {
        musicPlayManager.pushPlayQueue(songs);
    }

    @Override
    public void pushPlayQueue(List<Song> songs, int pos) {
        musicPlayManager.pushPlayQueue(songs, pos);
    }

    @Override
    public void randamPlay(List<Song> songs) {
        musicPlayManager.randamPlay(songs);
    }

    @Override
    public List<Song> getPlaySongs() {
        return musicPlayManager.getPlaySongs();
    }


    @Override
    public void setMusicPlayListener(IMusicPlayListener listener) {
//        if (!listeners.contains(listener)){
//            listeners.add(listener);
//        }
        musicPlayManager.setMusicPlayListener(listener);

    }

    @Override
    public void removeMusicPlayListtener(IMusicPlayListener listener) {
        musicPlayManager.removeMusicPlayListtener(listener);
    }

    @Override
    public void addMediaChangedListener(IMediaChangeListener listener) {
        musicPlayManager.setMediaChangedListener(listener);
    }

    @Override
    public void removeMediaChangedListener(IMediaChangeListener listener) {
        musicPlayManager.removeMediaChangedListener(listener);
    }

    @Override
    public void addSongDeleteListener(ISongDeleteListener listener) {
        musicPlayManager.addSongDeleteListener(listener);
    }

    @Override
    public void removeSongdeleteListener(ISongDeleteListener listener) {
        musicPlayManager.removeSongdeleteListener(listener);
    }

    @Override
    public void addPlayingSongListener(PlayingSongListener listener) {
        musicPlayManager.addPlayingSongListener(listener);
    }

    @Override
    public void removePlayingSongListener(PlayingSongListener listener) {
        musicPlayManager.removePlayingSongListener(listener);
    }


//    @Override
//    public void onPrepared() {
//        for (IMusicPlayListener listener:listeners){
//            listener.onPrepared();
//        }
//    }
//
//    @Override
//    public void onPlaying(Song song) {
//        for (IMusicPlayListener listener:listeners){
//            listener.onPlaying(song);
//        }
//    }
//
//    @Override
//    public void onPlayProgress(int progress) {
//        for (IMusicPlayListener listener:listeners){
//            listener.onPlayProgress(progress);
//        }
//    }
//
//    @Override
//    public void onPlayComplete(Song song) {
//        for (IMusicPlayListener listener:listeners){
//            listener.onPlayComplete(song);
//        }
//    }
//
//    @Override
//    public void onPlayError(Song song) {
//        for (IMusicPlayListener listener:listeners){
//            listener.onPlayError(song);
//        }
//    }
//
//    @Override
//    public void onPlayModeChanged(int mode) {
//        for (IMusicPlayListener listener:listeners){
//            listener.onPlayModeChanged(mode);
//        }
//    }
}
