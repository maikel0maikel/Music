package com.sinohb.music.widget.popup;

import android.support.annotation.NonNull;

import com.sinohb.base.BaseView;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.data.db.android.DataSource;
import com.sinohb.music.sdk.data.db.collect.CollectSorce;
import com.sinohb.music.sdk.entities.Song;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public abstract class ItemMenuPopupPresenter<T> implements ItemMenuContract.Presenter<T> {
    protected DataSource mDataSource;
    protected CollectSorce mCollectSource;
    protected ItemMenuContract.View mView;
    protected IMusicPlayManager playManager;
    protected CompositeDisposable compositeDisposable;
    protected final int pos;

    public ItemMenuPopupPresenter(@NonNull DataSource dataSource,
                                  @NonNull CollectSorce collectSorce,
                                  @NonNull IMusicPlayManager playManager, int pos) {
        this.mDataSource = dataSource;
        this.mCollectSource = collectSorce;
        this.playManager = playManager;
        compositeDisposable = new CompositeDisposable();
        this.pos = pos;
    }

    @Override
    public void dropView() {
        compositeDisposable.clear();
        mView = null;
    }


    @Override
    public void takeView(BaseView view) {
        mView = (ItemMenuContract.View) view;
    }


    protected void process(List<Song> list){
        if (list != null && mView != null) {
            mView.notifySongAdd(list.size());
        }
        playManager.pushPlayQueue(list);
    }
}
