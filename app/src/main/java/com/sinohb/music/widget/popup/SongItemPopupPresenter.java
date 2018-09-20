package com.sinohb.music.widget.popup;

import android.support.annotation.NonNull;

import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.play.ISongDeleteListener;
import com.sinohb.music.sdk.data.db.android.DataSource;
import com.sinohb.music.sdk.data.db.collect.CollectSorce;
import com.sinohb.music.sdk.entities.Song;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SongItemPopupPresenter extends ItemMenuPopupPresenter<Song> {
    public SongItemPopupPresenter(@NonNull DataSource dataSource,
                                  @NonNull CollectSorce collectSorce,
                                  @NonNull IMusicPlayManager playManager,
                                  int pos) {
        super(dataSource, collectSorce, playManager, pos);
    }

    @Override
    public void addSongToQueue(Song song) {
        if (song == null) return;
        playManager.addSongToPlay(song);
    }

    @Override
    public void deleteSong(Song song) {
        compositeDisposable.clear();
        Disposable disposable = mCollectSource.deleteSong(song)
                .flatMap((Function<Boolean, ObservableSource<Boolean>>) aBoolean -> mDataSource.deleteSong(song))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
//                    if (aBoolean) {
                    if (mView != null)
                        mView.notifyDeleteSuccess(pos);
                    playManager.removePlaySong(song);//,ISongDeleteListener.TYPE_SONG_DELETE
                    playManager.onSongDeleted(ISongDeleteListener.TYPE_SONG_DELETE);
//                    } else {
//                        mView.notifyDeleteSuccess(pos);
//                        playManager.onSongDeleted(ISongDeleteListener.TYPE_SONG_DELETE);
//                    }
                });
        compositeDisposable.add(disposable);
    }

}
