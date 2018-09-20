package com.sinohb.music.ui.music.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.sinohb.music.base.BasePlayContactPresenter;
import com.sinohb.music.base.BasePlayView;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.play.ISongDeleteListener;
import com.sinohb.music.sdk.data.db.android.DataSource;
import com.sinohb.music.sdk.data.db.collect.CollectSorce;
import com.sinohb.music.utils.Constants;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DetailPresenter extends BasePlayContactPresenter {
    private String title;
    private int type;
    private Object value;

    public DetailPresenter(DataSource dataSource, CollectSorce collectSorce,
                           @NonNull IMusicPlayManager playManager, Context context, @NonNull Bundle args) {
        super(dataSource, collectSorce, playManager,context);
        type = args.getInt(Constants.KEY_EXTRA_TYPE);
        title = args.getString(Constants.KEY_EXTRA_TITLE);
        value = args.get(Constants.KEY_EXTRA_VALUE);
    }

    @Override
    protected void loadData() {
        Disposable disposable = null;
        switch (type) {
            case Constants.TYPE_ARTISTS:
                disposable = mDataSource.getSongsForArtist((Long) value).flatMap(Flowable::fromIterable)
                        .toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(list -> {
                            process(list);
                            findPlayPos();

                        });
                break;
            case Constants.TYPE_ALBUM:
                disposable = mDataSource.getSongsForAlbum((Long) value).flatMap(Flowable::fromIterable)
                        .toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(list -> {
                            process(list);
                            findPlayPos();
                        });
                break;
            case Constants.TYPE_FOLDER:
                disposable = mDataSource.getSongsByFolderPath((String) value).flatMap(Flowable::fromIterable)
                        .toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(list -> {
                            process(list);
                            findPlayPos();
                        });
                break;
            default:
                break;
        }
        if (disposable != null) mCompositeDisposable.add(disposable);
        if (mView != null) {
            ((BasePlayView) mView).showTitle(title);
        }
    }

//    @Override
//    public void onDeleted(int type) {
//        if (type != ISongDeleteListener.TYPE_DETAIL_DELETE) {
//            loadDataSource();
//        }
//        if (datas==null||datas.isEmpty()){
//            mView.showEmptyView();
//        }else {
//            mView.hideEmptyView();
//        }
//    }
}
