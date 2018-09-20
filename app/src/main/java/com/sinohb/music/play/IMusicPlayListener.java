package com.sinohb.music.play;

import com.sinohb.music.sdk.entities.Song;

public interface IMusicPlayListener {

    void onPrepared();

    void onPlaying(Song song);

    void onPlayProgress(int progress);

    void onSeekComplete(int progress);

    void onPlayComplete(Song song);

    void onPlayError(Song song);

    void onPlayModeChanged(int mode);

    void onPlayStop(Song song);

    void onPause();

    void onResume();

}
