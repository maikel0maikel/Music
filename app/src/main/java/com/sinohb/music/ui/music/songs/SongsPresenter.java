package com.sinohb.music.ui.music.songs;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.sinohb.music.base.BasePlayContactPresenter;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.data.db.android.DataSource;
import com.sinohb.music.sdk.data.db.collect.CollectSorce;
import com.sinohb.music.sdk.entities.Song;
import com.sinohb.music.sdk.player.IMusicPlayerManager;


import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SongsPresenter extends BasePlayContactPresenter implements SongsContractPresenter {
    public SongsPresenter(DataSource dataSource, CollectSorce collectSorce,
                          @NonNull IMusicPlayManager playManager, Context context) {
        super(dataSource, collectSorce, playManager,context);
    }

    @Override
    protected void loadData() {
        mDataSource.getAllSongs().flatMap(Flowable::fromIterable).
                toList().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<List<Song>>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onSuccess(List<Song> songs) {
                process(songs);
                findPlayPos();
                disposable.dispose();
            }

            @Override
            public void onError(Throwable e) {
                disposable.dispose();
            }
        });
    }

    @Override
    public void randanPlayAll() {
        if (playManager.getMode() != IMusicPlayerManager.MODE_RANDOM){
            playManager.setMode(IMusicPlayerManager.MODE_RANDOM);
        }
        playManager.randamPlay(datas);
    }

//    @Override
//    public void onDeleted(int type) {
//        if (type != ISongDeleteListener.TYPE_SONG_DELETE) {
//            loadDataSource();
//        }
//        if (datas==null||datas.isEmpty()){
//            mView.showEmptyView();
//        }else {
//            mView.hideEmptyView();
//        }
//    }
}
