package com.sinohb.music.play;

public interface ISongDeleteListener {
    int TYPE_SONG_DELETE = 1;
    int TYPE_ARTIST_DELETE = 2;
    int TYPE_ALBUM_DELETE = 3;
    int TYPE_FOLDER_DELETE = 4;
    int TYPE_DETAIL_DELETE = 5;
    int TYPE_PLAY_LIST_DELETE = 6;
    int TYPE_PLAY_LIST_CLEAR = 7;

    void onDeleted(int type);
}
