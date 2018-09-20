package com.sinohb.music.sdk.player;


public interface PlayingSongListener {

    void onPlayingSongDeleted(int pos);

    void onPlayingSongPlayed(int pos);

    void onPlayingSongsDeleted();

    void onSongQueueEmpty();
}
