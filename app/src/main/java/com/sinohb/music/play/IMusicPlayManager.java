package com.sinohb.music.play;


import com.sinohb.music.sdk.entities.Song;
import com.sinohb.music.sdk.player.PlayingSongListener;

import java.util.List;

public interface IMusicPlayManager {

    void play(Song song);

    void playNext();

    void playPre();

    boolean pause();

    boolean resume();

    void setMode(int mode);

    int getMode();

    boolean isPlaying();

    void seekTo(int position);

    int getCurrentPos();

    void removePlaySong(Song song);

    Song getCurrentPlay();

    long getCurrentPlayId();

    void pushPlayQueue(List<Song> songs);

    void pushPlayQueue(List<Song> songs, int playPos);

    void randamPlay(List<Song> songs);

    void addSongToPlay(Song song);

    int getAudioSessionId();

    void removeSongs(List<Song> songs);

    void clearPlayQueue();

    void onSongDeleted(int type);

    List<Song> getPlaySongs();

    void destroy();

    void setMusicPlayListener(IMusicPlayListener listener);

    void removeMusicPlayListtener(IMusicPlayListener listener);

    void setMediaChangedListener(IMediaChangeListener listener);

    void removeMediaChangedListener(IMediaChangeListener listener);

    void addSongDeleteListener(ISongDeleteListener listener);

    void removeSongdeleteListener(ISongDeleteListener listener);

    void addPlayingSongListener(PlayingSongListener listener);

    void removePlayingSongListener(PlayingSongListener listener);

}
