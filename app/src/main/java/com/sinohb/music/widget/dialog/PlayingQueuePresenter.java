package com.sinohb.music.widget.dialog;

import android.support.annotation.NonNull;

import com.sinohb.base.BaseView;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.entities.Song;
import com.sinohb.music.sdk.player.IMusicPlayerManager;
import com.sinohb.music.sdk.player.PlayingSongListener;
import com.sinohb.music.utils.EmptyUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class PlayingQueuePresenter implements PlayQueueContract.Presenter<Song>, PlayingSongListener {

    private PlayQueueContract.View<Song> mView;
    private int mCurrentPos = -1;
    private IMusicPlayManager playManager;
    private int mode;
    private CompositeDisposable compositeDisposable;
    public PlayingQueuePresenter(@NonNull IMusicPlayManager playManager) {
        this.playManager = playManager;
        playManager.addPlayingSongListener(this);
        compositeDisposable = new CompositeDisposable();
    }


    @Override
    public void onItemClick(Song data, int position) {
//        if (mCurrentPos != position) {
            data.setPlaying(true);
            playManager.play(data);
//        }
    }


    protected void notifyPlayingItem(int position) {
        if (position == mCurrentPos && position != -1) {
            if (mView != null)
                mView.notifyItemPlaying(mCurrentPos);
            return;
        }
        if (mCurrentPos != -1 && mView != null) {
            mView.getData(mCurrentPos).setPlaying(false);
            mView.notifyItemNormal(mCurrentPos);
        }
        mCurrentPos = position;
        if (mView != null && position != -1)
            mView.notifyItemPlaying(mCurrentPos);
    }

    @Override
    public void loadDataSource() {
        List<Song> songs = playManager.getPlaySongs();
        if (EmptyUtils.isEmpty(songs) && mView != null) {
            mView.showEmptyView();
            mView.dismissLoding();
        } else {
            process(songs);
        }
    }

    private void process(List<Song> songs) {
        if (mView != null) {
            mView.dismissLoding();
            mView.hideEmptyView();
            mView.showSongsCount(songs.size());
            mView.showDataSource(songs);
            if (mCurrentPos != -1&&mCurrentPos<songs.size())
                mView.notifyItemPlaying(mCurrentPos);
        }
        findPlayPos(songs);
    }

    protected void findPlayPos(List<Song> songs) {
        compositeDisposable.clear();
        Disposable disposable = Observable.create((ObservableOnSubscribe<Integer>) e -> {
            long playId = playManager.getCurrentPlayId();
            int findPos = -1;
            if (songs != null && playId != -1) {
                int i = 0;
                for (Song song : songs) {
                    if (song.getId() == playId) {
                        song.setPlaying(true);
                        findPos = i;
                        break;
                    }
                    i++;
                }

            } else {
                mCurrentPos = -1;
            }
            e.onNext(findPos);
            e.onComplete();

        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
            if (mCurrentPos >= songs.size()) {
                mCurrentPos = -1;
            }
            notifyPlayingItem(integer);
        });
        compositeDisposable.add(disposable);
    }
    @Override
    public void takeView(@NonNull BaseView view) {
        mView = (PlayQueueContract.View<Song>) view;
        int mode = playManager.getMode();
        if (mode == 0) {
            mode = IMusicPlayerManager.MODE_LOOP;
        }
        this.mode = mode;
        notifyMode(mode);
    }


    @Override
    public void setMode() {
        if (playManager != null) {
            switch (mode) {
                case IMusicPlayerManager.MODE_LOOP:
                    mode = IMusicPlayerManager.MODE_BROWSE;
                    playManager.setMode(IMusicPlayerManager.MODE_BROWSE);
                    break;
                case IMusicPlayerManager.MODE_RANDOM:
                    mode = IMusicPlayerManager.MODE_SINGLE;
                    playManager.setMode(IMusicPlayerManager.MODE_SINGLE);
                    break;
                case IMusicPlayerManager.MODE_SINGLE:
                    mode = IMusicPlayerManager.MODE_LOOP;
                    playManager.setMode(IMusicPlayerManager.MODE_LOOP);
                    break;
                case IMusicPlayerManager.MODE_BROWSE:
                    mode = IMusicPlayerManager.MODE_RANDOM;
                    playManager.setMode(IMusicPlayerManager.MODE_RANDOM);
                    break;
            }
        }
        notifyMode(mode);
    }

    private void notifyMode(int mode) {
        if (mView != null) {
            switch (mode) {
                case IMusicPlayerManager.MODE_LOOP:
                    mView.notifyLoopMode();
                    break;
                case IMusicPlayerManager.MODE_RANDOM:
                    mView.notifyRandomMode();
                    break;
                case IMusicPlayerManager.MODE_SINGLE:
                    mView.notifySingleMode();
                    break;
                case IMusicPlayerManager.MODE_BROWSE:
                    mView.notifyBroseMode();
                    break;
            }
        }
    }

    @Override
    public void clearQueue() {
        playManager.clearPlayQueue();
        if (mView != null)
            mView.showEmptyView();
    }


    @Override
    public void dropView() {
        playManager.removePlayingSongListener(this);
        compositeDisposable.clear();
        mView = null;
    }

    @Override
    public void onPlayingSongDeleted(int pos) {
        if (mView != null) {
            List<Song> songs = playManager.getPlaySongs();
            int count = songs == null ? 0 : songs.size();
            mView.notifySongDeleted(pos, count);
        }
    }

    @Override
    public void onPlayingSongPlayed(int pos) {
        //notifyPlayingItem(pos);
//        List<Song> songs = playManager.getPlaySongs();
//        if (mCurrentPos != -1 && mCurrentPos < songs.size()) {
////            songs.get(mCurrentPos).setPlaying(false);
//        }
        mCurrentPos = pos;
//        if (songs != null) {
//            if (pos < songs.size()) {
////                songs.get(pos).setPlaying(true);
//            }
//            if (mView != null) {
//                mView.showSongsCount(songs.size());
//                // mView.notifyItemPlaying(pos);
//            }
//        }
        if (mView != null) {
            mView.notifyPlayQueue(pos);
        }
    }

    @Override
    public void onPlayingSongsDeleted() {
        loadDataSource();
    }

    @Override
    public void onSongQueueEmpty() {
        if (mView != null) {
            mView.showEmptyView();
        }
    }
}
