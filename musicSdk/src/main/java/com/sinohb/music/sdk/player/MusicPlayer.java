package com.sinohb.music.sdk.player;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class MusicPlayer implements IMusicPlayer, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener
        , MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener {
    private MediaPlayer mMediaPlayer;
    private MusicPlayerListener mListener;

    private static MusicPlayer musicPlayer;

    private boolean isPause = false;
    private int savedProgress = 0;

    private MusicPlayer() {
        mMediaPlayer = new MediaPlayer();
    }

    public static MusicPlayer getMusicPlayer() {
        if (musicPlayer == null) {
            synchronized (MusicPlayer.class) {
                if (musicPlayer == null) {
                    musicPlayer = new MusicPlayer();
                }
            }
        }
        return musicPlayer;
    }


    @Override
    public void play(String url, boolean forcePause) throws IOException {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        isPause = forcePause;
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setDataSource(url);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.prepare();
    }

    @Override
    public void playAsync(String url, boolean forcePause) throws IOException {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        isPause = forcePause;
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setDataSource(url);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.prepareAsync();
    }


    @Override
    public boolean pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPause = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean resume() {
        if (isPause && mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            isPause = false;
            return true;
        }
        return false;
    }

    @Override
    public void resumeSavePlay(String url, boolean forcePause, int progress) throws IOException {
        playAsync(url, forcePause);
        savedProgress = progress;
    }

    @Override
    public boolean seekto(int position) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(position);
            mMediaPlayer.pause();
            return true;
        }
        return false;
    }

    @Override
    public boolean isPlaying() {
        return !isPause && mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer == null ? -1 : mMediaPlayer.getCurrentPosition();
    }

    @Override
    public int getAudioSessionId() {
        return mMediaPlayer == null ? 0 : mMediaPlayer.getAudioSessionId();
    }

    @Override
    public void setMusicPlayerListener(MusicPlayerListener listener) {
        mListener = listener;
    }

    @Override
    public void stop() {
        if (mMediaPlayer != null) {
            pause();
            mMediaPlayer.stop();
        }
    }

    @Override
    public void destroy() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) mMediaPlayer.pause();
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (mListener != null) mListener.onPlayComplete();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (mMediaPlayer != null) {
            if (savedProgress != 0) {
                mMediaPlayer.seekTo(savedProgress);
                savedProgress = 0;
            }
            if (!isPause && !mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
                if (mListener != null) mListener.onPlayPrepared();
            }
        }

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        if (mListener != null) mListener.onPlayError();
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        if (mListener != null) mListener.onSeekComplete();
        if (!isPause && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }
}
