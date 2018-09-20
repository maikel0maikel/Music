package com.sinohb.music.ui.player;

import com.sinohb.base.BasePresenter;
import com.sinohb.base.BaseView;

import java.util.List;

public interface PlayerContract {

    interface PlayView<T> extends BaseView{
        void displayProgress(int progress);
        void displaySong(T song,int progress);
        void showSongInfo(T song,int progress);
        void notifyPlayPrepared();
        void notifyPlayComplete();
        void notifyPlayError();
        void notifyPause(int progress);
        void notifySeekCompleted(int progress,boolean isPlaying);
        void notifyPlay(int progress);
        void notifyLoopMode(boolean forceToast);
        void notifyRandomMode(boolean forceToast);
        void notifySingleMode(boolean forceToast);
        void notifyBroseMode(boolean forceToast);
        void notifyCancelCollect(boolean success);
        void notifyCollect(boolean success);
        void displayCollected();
        void displayNotCollect();
        void notifyViewReset();
    }

    interface Presenter<T> extends BasePresenter{

        void collect();

        void play();

        void playNext();

        void playPre();

        void pause();

        void resume();

        void setMode();

        int getMode();

        boolean isPlaying();

//        void removePlay(T t);

        void seekTo(int position);

        int getCurrentPos();

        T getCurrentPlay();

        void pushPlayQueue( List<T> songs);

        List<T> getPlaySongs();

        void destroy();
    }


}
