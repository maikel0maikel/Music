package com.sinohb.music.base;

import android.content.Context;
import android.support.annotation.NonNull;

import com.sinohb.music.play.IMusicPlayListener;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.data.db.android.DataSource;
import com.sinohb.music.sdk.data.db.collect.CollectSorce;
import com.sinohb.music.sdk.entities.Song;
import com.sinohb.music.utils.EmptyUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public abstract class BasePlayContactPresenter extends BaseContactPresenter<Song> implements IMusicPlayListener {

    protected int mCurrentPos = -1;

    public BasePlayContactPresenter(DataSource dataSource, CollectSorce collectSorce,
                                    @NonNull IMusicPlayManager playManager, Context context) {
        super(dataSource, collectSorce, playManager,context);
        playManager.setMusicPlayListener(this);
    }

    @Override
    public void onItemClick(Song data, int position) {
        if (mCurrentPos != position) {
            playManager.play(data);
            data.setPlaying(true);
            notifyPlayingItem(position);
        }
        if (datas != null)
            playManager.pushPlayQueue(datas, position);
    }

    protected void notifyPlayingItem(int position) {
        if (position == mCurrentPos && position != -1) {
            if (mView != null)
                ((BasePlayView<Song>) mView).notifyItemPlaying(mCurrentPos);
            return;
        }
        if (mCurrentPos != -1 && mView != null) {
            ((BasePlayView<Song>) mView).getData(mCurrentPos).setPlaying(false);
            ((BasePlayView<Song>) mView).notifyItemNormal(mCurrentPos);
        }
        mCurrentPos = position;
        if (mView != null && position != -1)
            ((BasePlayView<Song>) mView).notifyItemPlaying(mCurrentPos);
    }

    @Override
    public void onPrepared() {
        findPlayPos();
    }


    @Override
    public void onPlaying(Song song) {
        if (song != null) {
            song.setPlaying(true);
           // findPlayPos();
        }
    }


    @Override
    public void onPlayProgress(int progress) {

    }

    @Override
    public void onSeekComplete(int progress) {

    }

    @Override
    public void onPlayComplete(Song song) {
        onPause();
        mCurrentPos = -1;
    }

    @Override
    public void onPlayStop(Song song) {
        onPause();
        mCurrentPos = -1;
    }

    @Override
    public void onPlayError(Song song) {
        if (song != null) {
            song.setPlaying(false);
            if (mView != null && mCurrentPos != -1)
                ((BasePlayView<Song>) mView).notifyItemNormal(mCurrentPos);
        }
        mCurrentPos = -1;
    }

    protected void findPlayPos() {
        mCompositeDisposable.clear();
        Disposable disposable = Observable.create((ObservableOnSubscribe<Integer>) e -> {
            long playId = playManager.getCurrentPlayId();
            int findPos = -1;
            if (datas != null && playId != -1) {
                int i = 0;
                for (Song song : datas) {
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
            if (mCurrentPos >= datas.size()) {
                mCurrentPos = -1;
            }
            notifyPlayingItem(integer);
        });
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onPlayModeChanged(int mode) {

    }

    @Override
    public void onPause() {
        if (!EmptyUtils.isEmpty(datas) && mCurrentPos != -1 && mCurrentPos < datas.size()) {
            if (mView != null) {
                ((BasePlayView<Song>) mView).notifyItemNormal(mCurrentPos);
            }
        }
    }

    @Override
    public void onResume() {
        if (!EmptyUtils.isEmpty(datas) && mCurrentPos != -1 && mCurrentPos < datas.size()) {
            if (mView != null) {
                ((BasePlayView<Song>) mView).notifyItemPlaying(mCurrentPos);
            }
        }
    }

    @Override
    public void dropView() {
        super.dropView();
        playManager.removeMusicPlayListtener(this);
    }
}
