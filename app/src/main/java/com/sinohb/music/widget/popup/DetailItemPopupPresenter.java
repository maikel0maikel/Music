package com.sinohb.music.widget.popup;

import android.support.annotation.NonNull;

import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.data.db.android.DataSource;
import com.sinohb.music.sdk.data.db.collect.CollectSorce;

public class DetailItemPopupPresenter extends SongItemPopupPresenter {
    public DetailItemPopupPresenter(@NonNull DataSource dataSource,
                                    @NonNull CollectSorce collectSorce,
                                    @NonNull IMusicPlayManager playManager,
                                    int pos) {
        super(dataSource, collectSorce, playManager,pos);
    }

}
