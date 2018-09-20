package com.sinohb.music.widget.popup;

import android.support.annotation.NonNull;
import android.util.Log;

import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.play.ISongDeleteListener;
import com.sinohb.music.sdk.data.db.android.DataSource;
import com.sinohb.music.sdk.data.db.collect.CollectSorce;
import com.sinohb.music.sdk.entities.Album;
import com.sinohb.music.sdk.entities.Song;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class AlbumItemPopupPresenter extends ItemMenuPopupPresenter<Album> {

    public AlbumItemPopupPresenter(@NonNull DataSource dataSource,
                                   @NonNull CollectSorce collectSorce,
                                   @NonNull IMusicPlayManager playManager, int pos) {
        super(dataSource, collectSorce, playManager, pos);
    }

    @Override
    public void addSongToQueue(Album album) {
        if (album == null) return;
        compositeDisposable.clear();
        Disposable disposable = mDataSource.getSongsForAlbum(album.id).flatMap(Flowable::fromIterable)
                .toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::process);
        compositeDisposable.add(disposable);
    }

    @Override
    public void deleteSong(Album album) {
        if (album != null) {
            mCollectSource.deleteAlbum(album.id)
                    .flatMap((Function<Boolean, ObservableSource<List<Song>>>) aBoolean ->
                            mDataSource.deleteAlbum(album.id).toObservable())
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
                            playManager.onSongDeleted(ISongDeleteListener.TYPE_ALBUM_DELETE);
                            playManager.removeSongs(songs);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("deleteSong", "error");
                            if (mView != null)
                                mView.notifyDeleteSuccess(pos);
                            playManager.onSongDeleted(ISongDeleteListener.TYPE_ALBUM_DELETE);
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
