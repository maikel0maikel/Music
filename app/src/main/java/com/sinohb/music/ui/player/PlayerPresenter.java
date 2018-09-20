package com.sinohb.music.ui.player;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.sinohb.base.BaseView;
import com.sinohb.music.play.IMediaChangeListener;
import com.sinohb.music.play.IMusicPlayListener;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.data.db.collect.CollectSorce;
import com.sinohb.music.sdk.entities.Song;
import com.sinohb.music.sdk.player.IMusicPlayerManager;
import com.sinohb.music.sdk.service.MusicPlayServiceManager;
import com.sinohb.music.sdk.tools.ConstantTools;
import com.sinohb.music.sdk.tools.SharedPreferencesTools;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PlayerPresenter implements PlayerContract.Presenter<Song>, IMusicPlayListener, IMediaChangeListener {
    private IMusicPlayManager playManager;
    private PlayerContract.PlayView<Song> mView;
    private int mode = IMusicPlayerManager.MODE_LOOP;
    private CollectSorce mCollectSource;
    private CompositeDisposable mCompositeDisposable;
    private static final int COLLECT_CANCEL = 1;
    private static final int COLLECT_SAVE = 2;
    private int mCollectType = -1;
    private Context mContext;

    public PlayerPresenter(@NonNull IMusicPlayManager playManager,
                           @NonNull CollectSorce collectSorce,
                           @NonNull Context context) {
        this.playManager = playManager;
        this.playManager.setMusicPlayListener(this);
        this.playManager.setMediaChangedListener(this);
        mCollectSource = collectSorce;
        mCompositeDisposable = new CompositeDisposable();
        this.mContext = context;
    }

    @Override
    public void collect() {
        if (playManager != null) {
            Song song = playManager.getCurrentPlay();
            if (song != null) {
                mCompositeDisposable.clear();
                Disposable disposable = mCollectSource.hasSong(song.getId()).subscribeOn(Schedulers.io()).
                        observeOn(Schedulers.io()).map(aBoolean -> aBoolean ? COLLECT_CANCEL : COLLECT_SAVE).flatMap(integer -> {
                    mCollectType = integer;
                    if (integer == COLLECT_CANCEL)
                        return mCollectSource.deleteSong(song.getId());
                    else return mCollectSource.saveSong(song);
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(aBoolean -> {
                    switch (mCollectType) {
                        case COLLECT_CANCEL:
                            if (mView != null)
                                mView.notifyCancelCollect(aBoolean);
                            break;
                        case COLLECT_SAVE:
                            if (mView != null)
                                mView.notifyCollect(aBoolean);
                            break;
                    }
                    mCollectType = -1;
                });
                mCompositeDisposable.add(disposable);
            }
        }

    }

    @Override
    public void play() {
        if (playManager != null) {
            if (playManager.isPlaying() && playManager.pause()) {
                mView.notifyPause(playManager.getCurrentPos());
            } else if (playManager.resume()) {
                mView.notifyPlay(playManager.getCurrentPos());
            }
        }
    }

    @Override
    public void playNext() {
        if (playManager != null) {
            playManager.playNext();
        }
    }

    @Override
    public void playPre() {
        if (playManager != null) {
            playManager.playPre();
        }
    }

    @Override
    public void pause() {
        if (playManager != null && playManager.isPlaying()) {
            playManager.pause();
            mView.notifyPause(playManager.getCurrentPos());
        }
    }

    @Override
    public void resume() {
        if (playManager != null) {
            playManager.resume();
        }
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
        notifyMode(true);

    }

    private void notifyMode(boolean forceToast) {
        if (mView != null) {
            switch (mode) {
                case IMusicPlayerManager.MODE_LOOP:
                    mView.notifyLoopMode(forceToast);
                    break;
                case IMusicPlayerManager.MODE_RANDOM:
                    mView.notifyRandomMode(forceToast);
                    break;
                case IMusicPlayerManager.MODE_SINGLE:
                    mView.notifySingleMode(forceToast);
                    break;
                case IMusicPlayerManager.MODE_BROWSE:
                    mView.notifyBroseMode(forceToast);
                    break;
            }
        }
    }

    @Override
    public int getMode() {
        return playManager == null ? -1 : playManager.getMode();
    }

    @Override
    public boolean isPlaying() {
        return playManager != null && playManager.isPlaying();
    }


    @Override
    public void seekTo(int position) {
        if (playManager != null) {
            playManager.seekTo(position);
        }
    }

    @Override
    public int getCurrentPos() {
        return playManager == null ? 0 : playManager.getCurrentPos();
    }

    @Override
    public Song getCurrentPlay() {
        return playManager == null ? null : playManager.getCurrentPlay();
    }

    @Override
    public List<Song> getPlaySongs() {
        return playManager == null ? null : playManager.getPlaySongs();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void pushPlayQueue(List list) {
        if (playManager != null) {
            playManager.pushPlayQueue(list);
        }
    }

    @Override
    public void takeView(BaseView view) {
        mView = (PlayerContract.PlayView<Song>) view;
        initPlay();
    }

    private void initPlay() {
        Observable.create((ObservableOnSubscribe<Song>) e -> {
            Song song = playManager.getCurrentPlay();
            if (song != null) {
                e.onNext(song);
            } else {
                e.onError(new Throwable());
            }
            e.onComplete();

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Song>() {
            Disposable disposable;
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Song song) {
                if (mView != null)
                    mView.displaySong(song,playManager.getCurrentPos());
                isCollect(song);
            }

            @Override
            public void onError(Throwable e) {
               if (mView!=null&&!playManager.isPlaying())mView.notifyPause(playManager.getCurrentPos());
                disposable.dispose();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
        int playMode = SharedPreferencesTools.getInt(mContext, ConstantTools.KEY_PLAY_MODE, 0);
        if (playMode != 0) {
            mode = playMode;
            playManager.setMode(playMode);
        } else {
            if (playManager != null) {
               int mode = playManager.getMode();
                if (mode <= 0) {
                    playManager.setMode(this.mode);
                }else {
                    this.mode = mode;
                }
            }
        }
        notifyMode(false);
    }

    @Override
    public void dropView() {
        mView = null;
    }

    @Override
    public void onPrepared() {
        if (mView != null&&playManager.isPlaying())
            mView.notifyPlayPrepared();
    }

    @Override
    public void onPlaying(Song song) {
        if (mView != null) {
            if (playManager.isPlaying()){
                mView.displaySong(song,playManager.getCurrentPos());
            }else {
                mView.showSongInfo(song,playManager.getCurrentPos());
            }
//            mView.notifyPlay();
        }
        isCollect(song);
    }

    private void isCollect(Song song) {
        if (song == null) return;
        mCompositeDisposable.clear();
        Disposable disposable = mCollectSource.hasSong(song.getId()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(aBoolean -> {
                    if (aBoolean) mView.displayCollected();
                    else mView.displayNotCollect();
                });
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onPlayProgress(int progress) {
        if (mView != null&&playManager.isPlaying())
            mView.displayProgress(progress);

        //Log.e("music---","onPlayProgress");
    }

    @Override
    public void onSeekComplete(int progress) {
        if (mView!=null){
            mView.notifySeekCompleted(progress,playManager.isPlaying());
        }
    }

    @Override
    public void onPlayComplete(Song song) {
        if (mView != null)
            mView.notifyPlayComplete();

    }

    @Override
    public void onPlayError(Song song) {
        if (mView != null)
            mView.notifyPlayError();
    }

    @Override
    public void onPlayModeChanged(int mode) {
        if (this.mode == mode) return;
        this.mode = mode;
        notifyMode(false);
    }

    @Override
    public void onPlayStop(Song song) {
        if (mView != null)
            mView.notifyPlayComplete();

    }

    @Override
    public void onPause() {
        if (mView != null)
            mView.notifyPause(playManager.getCurrentPos());
    }

    @Override
    public void onResume() {
        if (mView != null)
            mView.notifyPlay(playManager.getCurrentPos());
    }

    @Override
    public void onMediaEventChanged(int event) {
        if (event == MusicPlayServiceManager.MEDIA_EJECT && mView != null) {
            mView.notifyViewReset();
        }
    }
}
