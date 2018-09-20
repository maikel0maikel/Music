package com.sinohb.music.sdk.player;


import java.util.List;

public interface IMusicPlayerManager<T> {
    public static final int MODE_SINGLE = 1;
    public static final int MODE_LOOP = 2;
    public static final int MODE_BROWSE = 3;
    public static final int MODE_RANDOM = 4;

    void play(T url);

    void playAsync(T url);

    void playNext(boolean force);

    void playPre(boolean force);

    void pushPlayQueue(List<T> lists);

    void resumePlayBackgroundQueue(List<T> list);

    void pushPlayQueue(List<T> lists, int playPos);

    void randamPlay(List<T> lists);

    void setMode(int mode);

    int getMode();

    boolean pause();

    boolean resume();

    void stop();

    void destroy();

    T getCurrentPlay();

    void resumePlayBackgroundSong(T t,int progress);

    int getAudioSessionId();

    List<T> getPlayQueuq();

    int getCurrentPlayPosition();

    boolean isPlaying();

    boolean seekTo(int position);

    void setPlayListener(PlayListener<T> listener);

    void setPlayingSongListener(PlayingSongListener listener);

    void clearPlayQueue();

    void addSongToPlay(T song);

    void removeSong(T song);

    void removeSongs(List<T> songs);

    interface PlayListener<T> extends IMusicPlayer.MusicPlayerListener {

        void onPlay(T t);

        void onAddSong(int added,int sourceCount);

    }
}
