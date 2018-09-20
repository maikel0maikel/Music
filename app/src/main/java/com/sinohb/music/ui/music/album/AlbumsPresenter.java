package com.sinohb.music.ui.music.album;

import android.content.Context;

import com.sinohb.music.base.BaseContactPresenter;
import com.sinohb.music.base.BaseDetailView;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.play.ISongDeleteListener;
import com.sinohb.music.sdk.data.db.android.DataSource;
import com.sinohb.music.sdk.data.db.collect.CollectSorce;
import com.sinohb.music.sdk.entities.Album;
import com.sinohb.music.utils.Constants;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AlbumsPresenter extends BaseContactPresenter<Album> {

    public AlbumsPresenter(DataSource dataSource, CollectSorce collectSorce, IMusicPlayManager playManager, Context context) {
        super(dataSource, collectSorce, playManager,context);
    }

    @Override
    protected void loadData() {
        Disposable disposable = mDataSource.getAllAlbums().flatMap(Flowable::fromIterable).
                toList().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(this::process);
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onItemClick(Album data, int pos) {
        if (mView != null) {
            ((BaseDetailView) mView).showDetial(Constants.TYPE_ALBUM, data.title, data.id);
        }
    }

//    @Override
//    public void onDeleted(int type) {
//        if (type != ISongDeleteListener.TYPE_ALBUM_DELETE) {
//            loadDataSource();
//        }
//        super.onDeleted(type);
//    }
}
