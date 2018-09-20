package com.sinohb.music.sdk.data.db.android;


import android.database.Cursor;

import com.sinohb.music.sdk.entities.Album;
import com.sinohb.music.sdk.entities.Artist;
import com.sinohb.music.sdk.entities.Song;

import java.util.List;


public interface CursorSource {

    Cursor getFolderMusicInfoCursor();

    Cursor getSongsByFolderPathCursor(String path);

    Cursor getQueueSongsCursor();

    Cursor getAllSongsCursor();

    Cursor getAllAlbumsCursor();

    Cursor getAllArtistsCursor();

    Cursor getSongsForAlbumCursor(long albumID);

    Cursor getSongsForArtistCursor(long artistID);

    Cursor getSongById(long songId);

    Cursor getSongsByIds(List<Song> songs);

    Cursor getSongsByIds(long[] songIds);

    Cursor getSongsBySelection(String selection, String[] args);

    boolean deleteSong(Song song);

    boolean deleteSong(long songId);

    boolean deleteSongs(List<Song> list);

    boolean deleteSongs(long[] songIds);

    boolean deleteArtist(Artist artist);

    boolean deleteArtist(long artistID);

    boolean deleteAlbum(Album album);

    boolean deleteAlbum(long albumId);

}
