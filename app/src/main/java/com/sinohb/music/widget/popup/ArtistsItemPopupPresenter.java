package com.sinohb.music.widget.popup;

import android.support.annotation.NonNull;
import android.util.Log;

import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.play.ISongDeleteListener;
import com.sinohb.music.sdk.data.db.android.DataSource;
import com.sinohb.music.sdk.data.db.collect.CollectSorce;
import com.sinohb.music.sdk.entities.Artist;
import com.sinohb.music.sdk.entities.Song;


import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ArtistsItemPopupPresenter extends ItemMenuPopupPresenter<Artist> {

    public ArtistsItemPopupPresenter(@NonNull DataSource dataSource,
                                     @NonNull CollectSorce collectSorce,
                                     @NonNull IMusicPlayManager playManager, int pos) {
        super(dataSource, collectSorce, playManager, pos);
    }

    @Override
    public void addSongToQueue(Artist artist) {
        if (artist == null) return;
        compositeDisposable.clear();
        Disposable disposable = mDataSource.getSongsForArtist(artist.id).flatMap(Flowable::fromIterable)
                .toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::process);
        compositeDisposable.add(disposable);
    }

    @Override
    public void deleteSong(Artist artist) {
        if (artist != null) {
            mCollectSource.deleteAritst(artist.id)
                    .flatMap((Function<Boolean, ObservableSource<List<Song>>>) aBoolean ->
                            mDataSource.deleteArtist(artist.id).toObservable())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Song>>() {
                        Disposable disposable;

                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable = d;
                        }

                        @Override
                        public void onNext(List<Song> songs) {
                            if (mView != null)
                                mView.notifyDeleteSuccess(pos);
                            playManager.removeSongs(songs);//,ISongDeleteListener.TYPE_ARTIST_DELETE
                            playManager.onSongDeleted(ISongDeleteListener.TYPE_ARTIST_DELETE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("deleteSong", "error");
                            if (mView != null)
                                mView.notifyDeleteSuccess(pos);
                            playManager.onSongDeleted(ISongDeleteListener.TYPE_ARTIST_DELETE);
                            disposable.dispose();
                        }

                        @Override
                        public void onComplete() {
                            disposable.dispose();
                        }
                    });
        }
    }
}
