package com.sinohb.music.sdk;

import com.sinohb.music.sdk.entities.Song;

interface IPlayCallbacker {
    void onPrepared();

    void onPlaying(out Song song);

    void onPlayProgress(int progress);

    void onPlayComplete(out Song song);

    void onSeekComplete(int progress);

    void onPlayError(out Song song);

    void onPlayModeChanged(int mode);

    void onMediaChanged(int event);

    void onPlayStop(out Song song);

    void onPause();

    void onResume();

    void onSongAdd(int added,int sourceCount);

    void onPlayingSongDeleted(int pos);

    void onPlayingSongPlayed(int pos);

    void onPlayingSongsDeleted();

    void onSongQueueEmpty();
}
