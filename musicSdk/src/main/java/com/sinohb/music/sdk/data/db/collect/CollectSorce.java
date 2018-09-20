package com.sinohb.music.sdk.data.db.collect;


import com.sinohb.music.sdk.entities.Song;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;

public interface CollectSorce {
//    int TYPE_COLLECT = 1;
//    int TYPE_PLAY_BACKGROUND = 2;

    Flowable<List<Song>> getAllSongs();

    Flowable<List<Long>> getPlaySongs();

    Observable<Boolean> savePlaySongs(List<Long> songs);

    Observable<Boolean> deletePlaySongs(List<Long> list);

    Observable<Song> getSong(long songId);

    Observable<Boolean> hasSong(long songId);

    Observable<Boolean> saveSong(Song song);

    Observable<Boolean> saveSongs(List<Song> songs);

    Observable<Boolean> updateSong(Song song);

    Observable<Boolean> updateSongs(List<Song> songs);

    Observable<Boolean> deleteSong(Song song);

    Observable<Boolean> deleteSong(long songId);

    Observable<Boolean> deleteSongs(List<Song> list);

    Observable<Boolean> deleteSongs(long[] songIds);

    Observable<Boolean> deleteSongByIds(List<Long> ids);

    Observable<Boolean> deleteAritst(long artistId);

    Observable<Boolean> deleteAlbum(long albumId);

    Observable<Boolean> deleteFolder(String path);

    Observable clearAll();

    void close();

    void setDataChangeListener(DataChangeListener listener);

    void removeChangeListener(DataChangeListener listener);

    interface DataChangeListener {
        void onDataChange();
    }
}
