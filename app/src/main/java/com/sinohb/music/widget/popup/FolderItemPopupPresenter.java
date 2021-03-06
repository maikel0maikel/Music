package com.sinohb.music.widget.popup;

import android.support.annotation.NonNull;
import android.util.Log;

import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.play.ISongDeleteListener;
import com.sinohb.music.sdk.data.db.android.DataSource;
import com.sinohb.music.sdk.data.db.collect.CollectSorce;
import com.sinohb.music.sdk.entities.MusicFolderInfo;
import com.sinohb.music.sdk.entities.Song;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class FolderItemPopupPresenter extends ItemMenuPopupPresenter<MusicFolderInfo> {

    public FolderItemPopupPresenter(@NonNull DataSource dataSource,
                                    @NonNull CollectSorce collectSorce,
                                    @NonNull IMusicPlayManager playManager, int pos) {
        super(dataSource, collectSorce, playManager, pos);
    }

    @Override
    public void addSongToQueue(MusicFolderInfo musicFolderInfo) {
        if (musicFolderInfo != null) {
            compositeDisposable.clear();
            Disposable disposable = mDataSource.getSongsByFolderPath(musicFolderInfo.folderPath).flatMap(Flowable::fromIterable)
                    .toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::process);
            compositeDisposable.add(disposable);
        }
    }

    @Override
    public void deleteSong(MusicFolderInfo musicFolderInfo) {
        if (musicFolderInfo != null) {
            mCollectSource.deleteFolder(musicFolderInfo.folderPath)
                    .flatMap((Function<Boolean, ObservableSource<List<Song>>>) aBoolean ->
                            mDataSource.deleteFolder(musicFolderInfo.folderPath).toObservable())
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
                            playManager.removeSongs(songs);//,ISongDeleteListener.TYPE_FOLDER_DELETE
                            playManager.onSongDeleted(ISongDeleteListener.TYPE_FOLDER_DELETE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("deleteSong", "error");
                            if (mView != null)
                                mView.notifyDeleteSuccess(pos);
                            playManager.onSongDeleted(ISongDeleteListener.TYPE_FOLDER_DELETE);
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
