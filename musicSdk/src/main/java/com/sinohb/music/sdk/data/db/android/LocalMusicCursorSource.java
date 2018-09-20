package com.sinohb.music.sdk.data.db.android;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.sinohb.logger.LogTools;
import com.sinohb.music.sdk.entities.Album;
import com.sinohb.music.sdk.entities.Artist;
import com.sinohb.music.sdk.entities.Song;

import java.io.File;
import java.util.List;


public class LocalMusicCursorSource implements CursorSource {

    private Context mContext;
    private static final String[] PROJECTION_MUSICS_FOLDER = {MediaStore.Files.FileColumns.DATA,
            "count(" + MediaStore.Files.FileColumns.PARENT + ") as " + "num_of_songs"};

    private static final String[] PROJECTION_SONGS = {"_id", "title", "artist", "album", "duration",
            "track", "artist_id", "album_id", MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.SIZE};

    private static final String[] PROJECTION_ALBUMS = {"_id", "album", "artist", "artist_id", "numsongs", "minyear"};

    private static final String[] PROJECTION_ARTISTS = {"_id", "artist", "number_of_albums", "number_of_tracks"};

    private static CursorSource INSTANCE;

    private LocalMusicCursorSource(Context context) {
        mContext = context.getApplicationContext();
    }

    public static CursorSource getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LocalMusicCursorSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalMusicCursorSource(context);
                }
            }
        }
        return INSTANCE;
    }


    @Override
    public Cursor getFolderMusicInfoCursor() {
        final String selection = " is_music = 1 AND title !=  '' " + " ) " + " group by ( " + MediaStore.Files.FileColumns.PARENT;
        return mContext.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                PROJECTION_MUSICS_FOLDER, selection, null, null);
    }

    @Override
    public Cursor getSongsByFolderPathCursor(String path) {
        String selectionStatement = "is_music=1 AND title != '' AND " + MediaStore.Audio.Media.DATA + " LIKE ?";
//        return mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                PROJECTION_SONGS, selectionStatement, new String[]{path + "%"}, null);
        return getSongsBySelection(selectionStatement, new String[]{path + "%"});
    }

    @Override
    public Cursor getQueueSongsCursor() {
        return null;
    }

    @Override
    public Cursor getAllSongsCursor() {
        String selectionStatement = "is_music=1 AND title != ''";
//        return mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                PROJECTION_SONGS, selectionStatement, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        return getSongsBySelection(selectionStatement, null);
    }

    @Override
    public Cursor getAllAlbumsCursor() {
        return mContext.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, PROJECTION_ALBUMS,
                null, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
    }

    @Override
    public Cursor getAllArtistsCursor() {
        return mContext.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, PROJECTION_ARTISTS,
                null, null, MediaStore.Audio.Artists.DEFAULT_SORT_ORDER);
    }

    @Override
    public Cursor getSongsForAlbumCursor(long albumID) {
        String string = "is_music=1 AND title != '' AND album_id=" + albumID;
//        return mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, PROJECTION_ALBUM_SONGS,
//                string, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        return getSongsBySelection(string, null);
    }

    @Override
    public Cursor getSongsForArtistCursor(long artistID) {
        String string = "is_music=1 AND title != '' AND artist_id=" + artistID;
//        return mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, PROJECTION_ARTIST_SONGS, string,
//                null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        return getSongsBySelection(string, null);
    }

    @Override
    public Cursor getSongById(long songId) {
        return getSongsBySelection("_id=?", new String[]{String.valueOf(songId)});
    }

    @Override
    public Cursor getSongsByIds(List<Song> songs) {
        if (songs == null) return null;
        String selection = getSelectionBySongs(songs);
        return getSongsBySelection(selection, null);
    }


    @Override
    public Cursor getSongsByIds(long[] songIds) {
        StringBuilder selectionStatement = getSelectionByIds(songIds);
        return mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                PROJECTION_SONGS, selectionStatement.toString(), null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    }

    @Override
    public Cursor getSongsBySelection(String selection, String[] args) {
        return mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, PROJECTION_SONGS,
                selection, args, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    }

    @NonNull
    private StringBuilder getSelectionByIds(long[] songIds) {
        StringBuilder selectionStatement = new StringBuilder();
        selectionStatement.append("_id IN (");
        int i = 0;
        for (long id : songIds) {
            selectionStatement.append(id);
            if (i < songIds.length - 1) {
                selectionStatement.append(",");
            }
            i++;
        }
        selectionStatement.append(")");
        return selectionStatement;
    }

    @Override
    public boolean deleteSong(Song song) {
        if (song == null) return false;
        int ret = mContext.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "_id=?", new String[]{String.valueOf(song.getId())});
        if (ret >= 0) {
            File file = new File(song.getPath());
            if (file.delete()) {
                return true;
            } else {
                LogTools.e("LocalMusicCursorSource", "song not delete path=" + song.getPath());
            }
        }
        return false;
    }

    @Override
    public boolean deleteSong(long songId) {
        Cursor cursor = getSongById(songId);
        int ret = mContext.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "_id=?", new String[]{String.valueOf(songId)});
        if (ret >= 0 && cursor != null && cursor.moveToFirst()) {
            String path = cursor.getString(8);
            File file = new File(path);
            cursor.close();
            if (file.delete()) {
                return true;
            } else {
                LogTools.e("LocalMusicCursorSource", "song not delete path=" + path);
            }
        }
        return false;
    }


    @Override
    public boolean deleteSongs(List<Song> list) {
        if (list == null) return false;
        String selectionStatement = getSelectionBySongs(list);
        int ret = mContext.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selectionStatement, null);
        if (ret >= 0) {
            for (Song song : list) {
                File file = new File(song.getPath());
                if (!file.delete()) {
                    LogTools.e("LocalMusicCursorSource", "song not delete path=" + song.getPath());
                }
            }
            return true;
        }
        return false;
    }

    @NonNull
    private String getSelectionBySongs(List<Song> list) {
        StringBuilder selectionStatement = new StringBuilder();
        selectionStatement.append("_id IN (");
        int i = 0;
        for (Song song : list) {
            selectionStatement.append(song.getId());
            if (i < list.size() - 1) {
                selectionStatement.append(",");
            }
            i++;
        }
        return selectionStatement.toString();
    }

    @Override
    public boolean deleteSongs(long[] songIds) {
        if (songIds == null) return false;
        StringBuilder selectionStatement = getSelectionByIds(songIds);

        int ret = mContext.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selectionStatement.toString(), null);
        if (ret >= 0) {
            Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    PROJECTION_SONGS, selectionStatement.toString(), null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            return cursor != null && deleteSongs(cursor, 8);
        }
        return false;
    }

    private boolean deleteSongs(Cursor cursor, int index) {
        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            final String path = cursor.getString(index);
            final File f = new File(path);
            try {
                if (!f.delete()) {
                    LogTools.e("LocalMusicCursorSource", "Failed to delete file " + path);
                } else {
                    i++;
                }
                cursor.moveToNext();
            } catch (final SecurityException ex) {
                cursor.moveToNext();
            }
        }
        cursor.close();
        return i > 0;
    }

    @Override
    public boolean deleteArtist(Artist artist) {
        return artist != null && deleteArtist(artist.id);
    }

    @Override
    public boolean deleteArtist(long artistID) {
        String selectionStatement = "is_music=1 AND title != '' AND artist_id=" + artistID;
        int ret = mContext.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selectionStatement, null);
        if (ret > 0) {
            Cursor cursor = getSongsForArtistCursor(artistID);
            if (cursor != null && deleteSongs(cursor, 8)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteAlbum(Album album) {
        return album != null && deleteAlbum(album.id);
    }

    @Override
    public boolean deleteAlbum(long albumId) {
        Cursor cursor = getSongsForAlbumCursor(albumId);
        return cursor != null && deleteSongs(cursor, 8);
    }
}
