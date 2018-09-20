package com.sinohb.music.ui;

import com.sinohb.base.BasePresenter;
import com.sinohb.base.BaseView;
import com.sinohb.music.play.IMediaChangeListener;
import com.sinohb.music.play.IMusicPlayListener;
import com.sinohb.music.play.ISongDeleteListener;
import com.sinohb.music.sdk.entities.Song;
import com.sinohb.music.sdk.player.PlayingSongListener;

import java.util.List;

public interface MainContract {

    interface View extends BaseView{

    }


    interface Presenter extends BasePresenter{

        void play( Song song);

        void playNext();

        void playPre();

        boolean pause();

        boolean resume();

        void setMode(int mode);

        int getMode();

        boolean isPlaying();

        void seekTo(int position);

        int getCurrentPos();

        int getAudioSessionId();

        void removePlaySong(Song song);

        void addSongToPlay(Song song);

        void removeSongs(List<Song> songs);

        void clearPlayQueue();

        void onSongDeleted(int type);

        Song getCurrentPlay();

        long getCurrentPlayId();

        void pushPlayQueue( List<Song> songs);

        void pushPlayQueue( List<Song> songs,int pos);

        void randamPlay(List<Song> songs);

        List<Song> getPlaySongs();

        void setMusicPlayListener(IMusicPlayListener listener);

        void removeMusicPlayListtener(IMusicPlayListener listener);

        void addMediaChangedListener(IMediaChangeListener listener);

        void removeMediaChangedListener(IMediaChangeListener listener);

        void addSongDeleteListener(ISongDeleteListener listener);

        void removeSongdeleteListener(ISongDeleteListener listener);

        void addPlayingSongListener(PlayingSongListener listener);

        void removePlayingSongListener(PlayingSongListener listener);
    }

}
