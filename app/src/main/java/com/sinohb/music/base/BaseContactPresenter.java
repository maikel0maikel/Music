package com.sinohb.music.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.sinohb.base.BaseView;
import com.sinohb.music.play.IMediaChangeListener;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.play.ISongDeleteListener;
import com.sinohb.music.sdk.data.db.android.DataSource;
import com.sinohb.music.sdk.data.db.collect.CollectSorce;
import com.sinohb.music.sdk.service.MusicPlayServiceManager;
import com.sinohb.music.utils.EmptyUtils;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseContactPresenter<T> implements BaseContact.Presenter<T>, IMediaChangeListener, ISongDeleteListener {

    protected BaseContact.View<T> mView;

    protected DataSource mDataSource;
    protected CollectSorce mCollectSorce;
    protected IMusicPlayManager playManager;
    @NonNull
    protected CompositeDisposable mCompositeDisposable;
    protected List<T> datas;
    private boolean mAlreadyStart = true;
    private Context mContext;

    public BaseContactPresenter(DataSource dataSource,
                                CollectSorce collectSorce,
                                IMusicPlayManager playManager,
                                Context context) {
        this.mDataSource = dataSource;
        this.mCollectSorce = collectSorce;
        this.playManager = playManager;
        this.mContext = context;
        playManager.setMediaChangedListener(this);
        playManager.addSongDeleteListener(this);
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void startGlide(boolean start) {
        if (mAlreadyStart) {
            if (start) {
                return;
            }
            mAlreadyStart = false;
            Glide.with(mContext).pauseRequests();
        } else {
            if (!start) {
                return;
            }
            mAlreadyStart = true;
            Glide.with(mContext).resumeRequests();
        }
    }

    @Override
    public void takeView(BaseView view) {
        mView = (BaseContact.View<T>) view;
    }

    @Override
    public void loadDataSource() {
        mCompositeDisposable.clear();
        if (mView != null)
            mView.showLoding();
        loadData();
    }

    protected void process(List<T> list) {
        if (mView != null) {
            Log.e("process", "process view!=null");
            mView.dismissLoding();
            if (EmptyUtils.isEmpty(list)) {
                mView.showEmptyView();
            } else {
                mView.hideEmptyView();
            }
            mView.showDataSource(list);
        }
        Log.e("process", "process finish");
        datas = list;
    }

    @Override
    public void onMediaEventChanged(int event) {
        switch (event) {
            case MusicPlayServiceManager.MEDIA_SCANNER_FINISHED:
                loadDataSource();
                break;
        }
    }

    @Override
    public void onDeleted(int type) {
        if (type == ISongDeleteListener.TYPE_PLAY_LIST_CLEAR || type == ISongDeleteListener.TYPE_PLAY_LIST_DELETE) {
            return;
        }
        loadDataSource();
//        if (mView != null) {
//            if (datas == null || datas.isEmpty()) {
//                mView.showEmptyView();
//            } else {
//                mView.hideEmptyView();
//            }
//        }
    }

    protected abstract void loadData();

    @Override
    public void dropView() {
        mCompositeDisposable.clear();
        playManager.removeMediaChangedListener(this);
        playManager.removeSongdeleteListener(this);
        mView = null;
    }
}
