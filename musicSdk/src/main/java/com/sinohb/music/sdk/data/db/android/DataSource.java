package com.sinohb.music.sdk.data.db.android;


import com.sinohb.music.sdk.entities.Album;
import com.sinohb.music.sdk.entities.Artist;
import com.sinohb.music.sdk.entities.MusicFolderInfo;
import com.sinohb.music.sdk.entities.Song;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;

public interface DataSource {

    Flowable<List<MusicFolderInfo>> getFolderMusicInfo();

    Flowable<List<Song>> getSongsByFolderPath(String path);

    Flowable<List<Song>> getQueueSongs();

    Flowable<List<Song>> getAllSongs();

    Flowable<List<Album>> getAllAlbums();

    Flowable<List<Artist>> getAllArtists();

    Flowable<List<Song>> getSongsForAlbum(long albumID);

    Flowable<List<Song>> getSongsForArtist(long artistID);

    Observable<Long> hasSong(long songId);

    Flowable<List<Song>> hasSongs(List<Song> songs);

    Flowable<List<Song>> hasSongsByIds(List<Long> songs);

    Observable<Song> getSong(long songId);

    Observable<Boolean> deleteSong(Song song);

    Observable<Boolean> deleteSong(long songId);

    Observable<Boolean> deleteSongs(List<Song> list);

    Observable<Boolean> deleteSongs(long[] songIds);

    Flowable<List<Song>> deleteArtist(Artist artist);

    Flowable<List<Song>> deleteArtist(long artistID);

    Flowable<List<Song>> deleteAlbum(Album album);

    Flowable<List<Song>> deleteAlbum(long albumId);

    Flowable<List<Song>> deleteFolder(String path);
}
