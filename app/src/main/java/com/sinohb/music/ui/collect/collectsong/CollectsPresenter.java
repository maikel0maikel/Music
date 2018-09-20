package com.sinohb.music.ui.collect.collectsong;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.sinohb.music.base.BasePlayContactPresenter;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.data.db.android.DataSource;
import com.sinohb.music.sdk.data.db.collect.CollectSorce;
import com.sinohb.music.sdk.entities.Song;
import com.sinohb.music.sdk.service.MusicPlayServiceManager;


import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CollectsPresenter extends BasePlayContactPresenter implements CollectSorce.DataChangeListener {
    private boolean isRemove = false;

    public CollectsPresenter(DataSource dataSource, CollectSorce collectSorce,
                             @NonNull IMusicPlayManager playManager, Context context) {
        super(dataSource, collectSorce, playManager,context);
        collectSorce.setDataChangeListener(this);
    }

    @Override
    protected void loadData() {
        Disposable disposable = mCollectSorce.getAllSongs().flatMap(Flowable::fromIterable).
                toList().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(list -> {
                    process(list);
                    findPlayPos();
                });
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onDataChange() {
        mCompositeDisposable.clear();
        Disposable disposable = Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            e.onNext(true);
            e.onComplete();
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(b -> {
            mCurrentPos = -1;
            loadDataSource();
        });
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onMediaEventChanged(int event) {
        switch (event) {
            case MusicPlayServiceManager.MEDIA_EJECT:
                isRemove = true;
                break;
            case MusicPlayServiceManager.MEDIA_SCANNER_FINISHED:
                if (isRemove && datas != null) {
                    isRemove = false;
                    mCompositeDisposable.clear();
                    Disposable disposable = Observable.fromIterable(datas)
                            .flatMap((Function<Song, ObservableSource<Long>>) song -> mDataSource.hasSong(song.getId()))
                            .toList().flatMapObservable(longs -> mCollectSorce.deleteSongByIds(longs))
                            .subscribe(aBoolean -> Log.e("onMediaEventChanged", "aBoolean:" + aBoolean));
                    mCompositeDisposable.add(disposable);
                } else {
                    loadDataSource();
                }
                break;
            case MusicPlayServiceManager.MEDIA_MOUNTED:
                isRemove = false;
                break;
            default:
                break;
        }

    }

    @Override
    public void dropView() {
        mCollectSorce.removeChangeListener(this);
        super.dropView();
    }

    @Override
    public void onDeleted(int type) {
        loadDataSource();
    }
}
