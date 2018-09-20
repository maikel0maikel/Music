package com.sinohb.music.sdk.data.db.android;

import com.sinohb.music.sdk.entities.Album;
import com.sinohb.music.sdk.entities.Artist;
import com.sinohb.music.sdk.entities.MusicFolderInfo;
import com.sinohb.music.sdk.entities.Song;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;

public class MusicDataRepository implements DataSource {

    private DataSource dataSource;

    private static DataSource INSTANCE;

    private MusicDataRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static DataSource getInstance(DataSource source) {
        if (INSTANCE == null) {
            synchronized (MusicDataRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MusicDataRepository(source);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Flowable<List<MusicFolderInfo>> getFolderMusicInfo() {
        return dataSource.getFolderMusicInfo();
    }

    @Override
    public Flowable<List<Song>> getSongsByFolderPath(String path) {
        return dataSource.getSongsByFolderPath(path);
    }

    @Override
    public Flowable<List<Song>> getQueueSongs() {
        return dataSource.getQueueSongs();
    }

    @Override
    public Flowable<List<Song>> getAllSongs() {
        return dataSource.getAllSongs();
    }

    @Override
    public Flowable<List<Album>> getAllAlbums() {
        return dataSource.getAllAlbums();
    }

    @Override
    public Flowable<List<Artist>> getAllArtists() {
        return dataSource.getAllArtists();
    }

    @Override
    public Flowable<List<Song>> getSongsForAlbum(long albumID) {
        return dataSource.getSongsForAlbum(albumID);
    }

    @Override
    public Flowable<List<Song>> getSongsForArtist(long artistID) {
        return dataSource.getSongsForArtist(artistID);
    }

    @Override
    public Observable<Long> hasSong(long songId) {
        return dataSource.hasSong(songId);
    }

    @Override
    public Flowable<List<Song>> hasSongs(List<Song> songs) {
        return dataSource.hasSongs(songs);
    }

    @Override
    public Flowable<List<Song>> hasSongsByIds(List<Long> songs) {
        return dataSource.hasSongsByIds(songs);
    }

    @Override
    public Observable<Song> getSong(long songId) {
        return dataSource.getSong(songId);
    }

    @Override
    public Observable<Boolean> deleteSong(Song song) {
        return dataSource.deleteSong(song);
    }

    @Override
    public Observable<Boolean> deleteSong(long songId) {
        return dataSource.deleteSong(songId);
    }

    @Override
    public Observable<Boolean> deleteSongs(List<Song> songs) {
        return dataSource.deleteSongs(songs);
    }

    @Override
    public Observable<Boolean> deleteSongs(long[] songIds) {
        return dataSource.deleteSongs(songIds);
    }

    @Override
    public  Flowable<List<Song>> deleteArtist(Artist artist) {
        return dataSource.deleteArtist(artist);
    }

    @Override
    public Flowable<List<Song>> deleteArtist(long artistID) {
        return dataSource.deleteArtist(artistID);
    }

    @Override
    public Flowable<List<Song>> deleteAlbum(Album album) {
        return dataSource.deleteAlbum(album);
    }

    @Override
    public Flowable<List<Song>> deleteAlbum(long albumId) {
        return dataSource.deleteAlbum(albumId);
    }

    @Override
    public Flowable<List<Song>> deleteFolder(String path) {
        return dataSource.deleteFolder(path);
    }
}
