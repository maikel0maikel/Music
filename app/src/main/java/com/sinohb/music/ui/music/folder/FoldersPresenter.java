package com.sinohb.music.ui.music.folder;


import android.content.Context;

import com.sinohb.music.base.BaseContactPresenter;
import com.sinohb.music.base.BaseDetailView;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.play.ISongDeleteListener;
import com.sinohb.music.sdk.data.db.android.DataSource;
import com.sinohb.music.sdk.data.db.collect.CollectSorce;
import com.sinohb.music.sdk.entities.MusicFolderInfo;
import com.sinohb.music.utils.Constants;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class FoldersPresenter extends BaseContactPresenter<MusicFolderInfo> {

    public FoldersPresenter(DataSource dataSource, CollectSorce collectSorce, IMusicPlayManager playManager, Context context) {
        super(dataSource,collectSorce,playManager,context);
    }

    @Override
    protected void loadData() {
        Disposable disposable = mDataSource.getFolderMusicInfo().flatMap(Flowable::fromIterable).
                toList().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(this::process);
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onItemClick(MusicFolderInfo data,int pos) {
        if (mView!=null) {
            ((BaseDetailView) mView).showDetial(Constants.TYPE_FOLDER, data.folderName, data.folderPath);
        }
    }

//    @Override
//    public void onDeleted(int type) {
//        if (type!= ISongDeleteListener.TYPE_FOLDER_DELETE){
//            loadDataSource();
//        }
//        if (datas==null||datas.isEmpty()){
//            mView.showEmptyView();
//        }else {
//            mView.hideEmptyView();
//        }
//    }
}
