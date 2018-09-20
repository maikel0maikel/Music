package com.sinohb.music.sdk.data.db.collect;

import com.sinohb.music.sdk.entities.Song;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;

public class CollectDataRepository implements CollectSorce {

    private CollectSorce collectSorce;
    private static CollectSorce INSTANCE;

    public CollectDataRepository(CollectSorce collectSorce) {
        this.collectSorce = collectSorce;
    }

    public static CollectSorce getInstance(CollectSorce source) {
        if (INSTANCE == null) {
            synchronized (CollectDataRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CollectDataRepository(source);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Flowable<List<Song>> getAllSongs() {
        return collectSorce.getAllSongs();
    }

    @Override
    public Flowable<List<Long>> getPlaySongs() {
        return collectSorce.getPlaySongs();
    }

    @Override
    public Observable<Boolean> savePlaySongs(List<Long> songs) {
        return collectSorce.savePlaySongs(songs);
    }

    @Override
    public Observable<Boolean> deletePlaySongs(List<Long> list) {
        return collectSorce.deletePlaySongs(list);
    }

    @Override
    public Observable<Song> getSong(long songId) {
        return collectSorce.getSong(songId);
    }

    @Override
    public Observable<Boolean> hasSong(long songId) {
        return collectSorce.hasSong(songId);
    }

    @Override
    public Observable<Boolean> saveSong(Song song) {
        return collectSorce.saveSong(song);
    }

    @Override
    public Observable<Boolean> saveSongs(List<Song> songs) {
        return collectSorce.saveSongs(songs);
    }

    @Override
    public Observable<Boolean> updateSong(Song song) {
        return collectSorce.updateSong(song);
    }

    @Override
    public Observable<Boolean> updateSongs(List<Song> songs) {
        return collectSorce.updateSongs(songs);
    }

    @Override
    public Observable<Boolean> deleteSong(Song song) {
        return collectSorce.deleteSong(song);
    }

    @Override
    public Observable<Boolean> deleteSong(long songId) {
        return collectSorce.deleteSong(songId);
    }

    @Override
    public Observable<Boolean> deleteSongs(List<Song> list) {
        return collectSorce.deleteSongs(list);
    }

    @Override
    public Observable<Boolean> deleteSongs(long[] songIds) {
        return collectSorce.deleteSongs(songIds);
    }

    @Override
    public Observable<Boolean> deleteSongByIds(List<Long> ids) {
        return collectSorce.deleteSongByIds(ids);
    }

    @Override
    public Observable<Boolean> deleteAritst(long artistId) {
        return collectSorce.deleteAritst(artistId);
    }

    @Override
    public Observable<Boolean> deleteAlbum(long albumId) {
        return collectSorce.deleteAlbum(albumId);
    }

    @Override
    public Observable<Boolean> deleteFolder(String path) {
        return collectSorce.deleteFolder(path);
    }

    @Override
    public Observable clearAll() {
        return collectSorce.clearAll();
    }

    @Override
    public void close() {
        collectSorce.close();
    }

    @Override
    public void setDataChangeListener(DataChangeListener listener) {
        collectSorce.setDataChangeListener(listener);
    }

    @Override
    public void removeChangeListener(DataChangeListener listener) {
        collectSorce.removeChangeListener(listener);
    }
}
