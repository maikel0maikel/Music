package com.sinohb.music.sdk.player;

import java.io.IOException;

public interface IMusicPlayer {

    void play(String url,boolean forcePause) throws IOException;

    void playAsync(String url,boolean forcePause) throws IOException;

    boolean pause();

    boolean resume();

    void resumeSavePlay(String url,boolean pause,int progress) throws IOException;

    boolean seekto(int position);

    boolean isPlaying();

    int getCurrentPosition();

    int getAudioSessionId();

    void setMusicPlayerListener(MusicPlayerListener listener);

    void stop();

    void destroy();


    interface MusicPlayerListener {
        void onPlayComplete();

        void onPlayPrepared();

        void onPlayError();

        void onSeekComplete();

        void onPlayStop();

    }

}
