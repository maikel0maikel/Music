package com.sinohb.music.utils;

import android.content.Context;
import android.os.IBinder;

import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.play.MusicPlayManager;

public class Injection {

    private Injection() {
    }

    public static IMusicPlayManager providePlayManager(IBinder token, Context context) {
        return MusicPlayManager.getInstance(token, context);
    }
}
