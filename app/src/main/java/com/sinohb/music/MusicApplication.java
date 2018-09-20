package com.sinohb.music;

import android.app.Application;
import android.os.Debug;


public class MusicApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        //Debug.startMethodTracing("music");
        //getLogger().setDebug(true);
    }
}
