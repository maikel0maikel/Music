package com.sinohb.music.ui;

import android.os.Binder;
import android.os.Bundle;

import com.sinohb.base.BaseActivity;
import com.sinohb.base.tools.ActivityTools;
import com.sinohb.music.play.IMediaChangeListener;
import com.sinohb.music.play.IMusicPlayListener;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.play.ISongDeleteListener;
import com.sinohb.music.sdk.entities.Song;
import com.sinohb.music.sdk.player.PlayingSongListener;
import com.sinohb.music.utils.Injection;

import java.util.List;

public class MainActivity extends BaseActivity implements IMusicPlayManager, MainContract.View {

    private Binder mToken = new Binder();

    private MainContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MainPresenter(Injection.providePlayManager(mToken, this));
        mPresenter.takeView(this);
        MainFragment mainFragment =
                (MainFragment) getSupportFragmentManager().findFragmentById(com.sinohb.base.R.id.content);
        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance();
            ActivityTools.addFragmentToActivity(
                    getSupportFragmentManager(), mainFragment, com.sinohb.base.R.id.content);
        }
    }

    @Override
    public void play(Song song) {
        mPresenter.play(song);
    }

    @Override
    public void playNext() {
        mPresenter.playNext();
    }

    @Override
    public void playPre() {
        mPresenter.playPre();
    }

    @Override
    public boolean pause() {
        return mPresenter.pause();
    }

    @Override
    public boolean resume() {
        return mPresenter.resume();
    }

    @Override
    public void setMode(int mode) {
        mPresenter.setMode(mode);
    }

    @Override
    public int getMode() {
        return mPresenter.getMode();
    }

    @Override
    public boolean isPlaying() {
        return mPresenter.isPlaying();
    }

    @Override
    public void seekTo(int position) {
        mPresenter.seekTo(position);
    }

    @Override
    public int getCurrentPos() {
        return mPresenter.getCurrentPos();
    }

    @Override
    public void removePlaySong(Song song) {
        mPresenter.removePlaySong(song);
    }

    @Override
    public Song getCurrentPlay() {
        return mPresenter.getCurrentPlay();
    }

    @Override
    public long getCurrentPlayId() {
        return mPresenter.getCurrentPlayId();
    }

    @Override
    public void pushPlayQueue(List<Song> songs) {
        mPresenter.pushPlayQueue(songs);
    }

    @Override
    public void pushPlayQueue(List<Song> songs, int playPos) {
        mPresenter.pushPlayQueue(songs, playPos);
    }

    @Override
    public void randamPlay(List<Song> songs) {
        mPresenter.randamPlay(songs);
    }

    @Override
    public void addSongToPlay(Song song) {
        mPresenter.addSongToPlay(song);
    }

    @Override
    public int getAudioSessionId() {
        return mPresenter.getCurrentPos();
    }

    @Override
    public void removeSongs(List<Song> songs) {
        mPresenter.removeSongs(songs);
    }

    @Override
    public void clearPlayQueue() {
        mPresenter.clearPlayQueue();
    }

    @Override
    public void onSongDeleted(int type) {
        mPresenter.onSongDeleted(type);
    }

    @Override
    public List<Song> getPlaySongs() {
        return mPresenter.getPlaySongs();
    }

    @Override
    public void destroy() {

    }


    @Override
    public void setMusicPlayListener(IMusicPlayListener listener) {
        mPresenter.setMusicPlayListener(listener);
    }

    @Override
    public void removeMusicPlayListtener(IMusicPlayListener listener) {
        mPresenter.removeMusicPlayListtener(listener);
    }

    @Override
    public void setMediaChangedListener(IMediaChangeListener listener) {
        mPresenter.addMediaChangedListener(listener);
    }

    @Override
    public void removeMediaChangedListener(IMediaChangeListener listener) {
        mPresenter.removeMediaChangedListener(listener);
    }

    @Override
    public void addSongDeleteListener(ISongDeleteListener listener) {
        mPresenter.addSongDeleteListener(listener);
    }

    @Override
    public void removeSongdeleteListener(ISongDeleteListener listener) {
        mPresenter.removeSongdeleteListener(listener);
    }

    @Override
    public void addPlayingSongListener(PlayingSongListener listener) {
        mPresenter.addPlayingSongListener(listener);
    }

    @Override
    public void removePlayingSongListener(PlayingSongListener listener) {
        mPresenter.removePlayingSongListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.dropView();
    }
}
