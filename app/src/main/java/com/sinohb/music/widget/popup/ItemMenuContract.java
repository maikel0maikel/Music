package com.sinohb.music.widget.popup;

import com.sinohb.base.BasePresenter;
import com.sinohb.base.BaseView;


public interface ItemMenuContract {
    interface View extends BaseView {
        void notifyDeleteSuccess(int pos);
        void notifyDeleteFailure(int pos);
        void notifySongAdd(int cout);
    }

    interface Presenter<T> extends BasePresenter {

        void addSongToQueue(T song);

        void deleteSong(T t);

    }
}
