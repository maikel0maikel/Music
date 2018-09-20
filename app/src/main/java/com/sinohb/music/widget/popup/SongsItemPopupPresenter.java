package com.sinohb.music.widget.popup;

import android.support.annotation.NonNull;

import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.data.db.android.DataSource;
import com.sinohb.music.sdk.data.db.collect.CollectSorce;
import com.sinohb.music.sdk.entities.Song;

import java.util.List;


public class SongsItemPopupPresenter extends ItemMenuPopupPresenter<List<Song>> {

    public SongsItemPopupPresenter(@NonNull DataSource dataSource,
                                   @NonNull CollectSorce collectSorce,
                                   @NonNull IMusicPlayManager playManager,int pos) {
        super(dataSource, collectSorce, playManager,pos);
    }

    @Override
    public void addSongToQueue(List<Song> song) {

    }

    @Override
    public void deleteSong(List<Song> songs) {
//        if (songs != null) {
//            mCollectSource.deleteSongs(songs)
//                    .flatMap((Function<Boolean, ObservableSource<List<Song>>>) aBoolean ->
//                            mDataSource.deleteSongs(songs).toObservable())
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Observer<List<Song>>() {
//                        Disposable disposable;
//
//                        @Override
//                        public void onSubscribe(Disposable d) {
//                            disposable = d;
//                        }
//
//                        @Override
//                        public void onNext(List<Song> songs) {
//                            mView.notifyDeleteSuccess(pos);
//                            playManager.notifySongDeleted(ISongDeleteListener.TYPE_ARTIST_DELETE);
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            Log.e("deleteSong", "error");
//                            mView.notifyDeleteFailure(pos);
//                            disposable.dispose();
//                        }
//
//                        @Override
//                        public void onComplete() {
//                            disposable.dispose();
//                        }
//                    });
//        }
    }
}
