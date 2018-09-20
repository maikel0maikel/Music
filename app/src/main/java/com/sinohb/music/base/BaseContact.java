package com.sinohb.music.base;

import com.sinohb.base.BasePresenter;
import com.sinohb.base.BaseView;

import java.util.List;

public interface BaseContact {

    interface View<T> extends BaseView {

        void showLoding();

        void dismissLoding();

        void showEmptyView();

        void hideEmptyView();

        void showDataSource(List<T> datas);

    }

    interface Presenter<T> extends BasePresenter {

        void loadDataSource();

        void onItemClick(T data,int position);

        void startGlide(boolean start);
    }

}
