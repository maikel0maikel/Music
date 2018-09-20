package com.sinohb.music.sdk.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.sinohb.music.sdk.data.db.collect.CollectDataRepository;
import com.sinohb.music.sdk.data.db.collect.DBCollectSource;
import com.sinohb.music.sdk.player.MusicPlayer;
import com.sinohb.music.sdk.player.SongPlayerManager;
import com.sinohb.music.sdk.tools.InjectionTools;


public class MusicService extends Service implements ClientDeathRecipient.ClientDeathListener {

    private ClientDeathRecipient clientDeathRecipient;
    private MusicPlayServiceManager musicPlayServiceManager;

    @Override
    public void onCreate() {
        super.onCreate();
        clientDeathRecipient = new ClientDeathRecipient(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (musicPlayServiceManager == null) {
            musicPlayServiceManager = MusicPlayServiceManager.getInstance(SongPlayerManager.getInstance(MusicPlayer.getMusicPlayer()),
                    clientDeathRecipient, this, InjectionTools.provideCollectDataRespository(this),
                    InjectionTools.provideMusicDataRespository(this));
        }
        return musicPlayServiceManager;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (musicPlayServiceManager != null) {
            musicPlayServiceManager.destroy();
        }
    }

    @Override
    public void onDied() {
        stopSelf();
    }
}
