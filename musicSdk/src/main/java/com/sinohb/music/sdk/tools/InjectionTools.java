package com.sinohb.music.sdk.tools;

import android.content.Context;
import com.sinohb.music.sdk.data.db.android.DataSource;
import com.sinohb.music.sdk.data.db.android.LocalMusicDataSource;
import com.sinohb.music.sdk.data.db.android.MusicDataRepository;
import com.sinohb.music.sdk.data.db.collect.CollectDataRepository;
import com.sinohb.music.sdk.data.db.collect.CollectSorce;
import com.sinohb.music.sdk.data.db.collect.DBCollectSource;

public class InjectionTools {

    private InjectionTools() {
    }

    public static DataSource provideMusicDataRespository(Context context) {
        return MusicDataRepository.getInstance(LocalMusicDataSource.getDataSource(context));
    }

    public static CollectSorce provideCollectDataRespository(Context context) {
        return CollectDataRepository.getInstance(DBCollectSource.getInstance(context));
    }
}
