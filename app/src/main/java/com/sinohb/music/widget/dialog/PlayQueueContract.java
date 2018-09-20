package com.sinohb.music.widget.dialog;

import com.sinohb.base.BasePresenter;
import com.sinohb.music.base.BaseContact;
import com.sinohb.music.base.BasePlayView;

public interface PlayQueueContract {

    interface View<T> extends BasePlayView<T> {
        void notifyLoopMode();

        void notifyRandomMode();

        void notifySingleMode();

        void notifyBroseMode();

        void showSongsCount(int count);

        void notifySongDeleted(int pos, int songsCount);

        void notifyPlayQueue(int pos);
    }

    interface Presenter<T> extends BasePresenter {
        void setMode();

        void clearQueue();

        void loadDataSource();

        void onItemClick(T data, int position);
    }

}
