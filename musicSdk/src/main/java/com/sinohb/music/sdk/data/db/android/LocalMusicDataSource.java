package com.sinohb.music.sdk.data.db.android;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;


import com.sinohb.logger.LogTools;
import com.sinohb.music.sdk.entities.Album;
import com.sinohb.music.sdk.entities.Artist;
import com.sinohb.music.sdk.entities.MusicFolderInfo;
import com.sinohb.music.sdk.entities.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class LocalMusicDataSource implements DataSource {
    private Context mContext;
    private static final String[] PROJECTION_MUSICS_FOLDER = {MediaStore.Files.FileColumns.DATA,
            "count(" + MediaStore.Files.FileColumns.PARENT + ") as " + "num_of_songs"};

    private static final String[] PROJECTION_SONGS = {"_id", "title", "artist", "album", "duration",
            "track", "artist_id", "album_id", MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.SIZE};

    private static final String[] PROJECTION_ALBUMS = {"_id", "album", "artist", "artist_id", "numsongs", "minyear"};

    private static final String[] PROJECTION_ARTISTS = {"_id", "artist", "number_of_albums", "number_of_tracks"};

    private static DataSource dataSource;

    public static DataSource getDataSource(Context context) {
        if (dataSource == null) {
            synchronized (LocalMusicCursorSource.class) {
                if (dataSource == null) {
                    dataSource = new LocalMusicDataSource(context);
                }
            }
        }
        return dataSource;
    }

    private LocalMusicDataSource(Context context) {
        this.mContext = context.getApplicationContext();
    }

    private Cursor getSongsBySelection(String selection, String[] args) {
        return mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, PROJECTION_SONGS,
                selection, args, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    }

    private String getSelectionBySongs(List<Song> list) {
        StringBuilder selectionStatement = new StringBuilder();
        selectionStatement.append("_id IN ( ");
        int i = 0;
        for (Song song : list) {
            selectionStatement.append(song.getId());
            if (i < list.size() - 1) {
                selectionStatement.append(",");
            }
            i++;
        }
        selectionStatement.append(" ) ");
        return selectionStatement.toString();
    }
    private String getSelectionBySongIds(List<Long> list) {
        StringBuilder selectionStatement = new StringBuilder();
        selectionStatement.append("_id IN ( ");
        int i = 0;
        for (Long id : list) {
            selectionStatement.append(id);
            if (i < list.size() - 1) {
                selectionStatement.append(",");
            }
            i++;
        }
        selectionStatement.append(" ) ");
        return selectionStatement.toString();
    }
    private StringBuilder getSelectionByIds(long[] songIds) {
        StringBuilder selectionStatement = new StringBuilder();
        selectionStatement.append("_id IN ( ");
        int i = 0;
        for (long id : songIds) {
            selectionStatement.append(id);
            if (i < songIds.length - 1) {
                selectionStatement.append(",");
            }
            i++;
        }
        selectionStatement.append(" ) ");
        return selectionStatement;
    }

    @Override
    public Flowable<List<MusicFolderInfo>> getFolderMusicInfo() {
        final String selection = " is_music = 1 AND title !=  '' " + " ) " + " group by ( " + MediaStore.Files.FileColumns.PARENT;
        final Cursor cursor = mContext.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                PROJECTION_MUSICS_FOLDER, selection, null, null);
        if (cursor != null) {
            final int index_data = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            final int index_num_of_songs = cursor.getColumnIndex("num_of_songs");
            return Observable.create((ObservableOnSubscribe<Cursor>) e -> {
                while (cursor.moveToNext()) {
                    e.onNext(cursor);
                }
                e.onComplete();
                cursor.close();
            }).map(cursor1 -> {
                //获取每个目录下的歌曲数量
                int songCount = cursor1.getInt(index_num_of_songs);
                //获取文件的路径,如/storage/sdcard0/sinohb/music/Baby.mp3
                String filePath = cursor1.getString(index_data);
                // 获取文件所属文件夹的路径，如/storage/sdcard0/sinohb/music
                String folderpath = filePath.substring(0, filePath.lastIndexOf(File.separator));
                // 获取文件所属文件夹的名称，如music
                String foldername = folderpath.substring(folderpath.lastIndexOf(File.separator) + 1);
                return new MusicFolderInfo(foldername, folderpath, songCount);
            }).toList().toFlowable();
        }
        return Observable.create((ObservableOnSubscribe<List<MusicFolderInfo>>) e -> {
            e.onNext(new ArrayList<>());
            e.onComplete();
        }).toFlowable(BackpressureStrategy.BUFFER);
    }


    @Override
    public Flowable<List<Song>> getSongsByFolderPath(String path) {
        String selectionStatement = "is_music=1 AND title != '' AND " + MediaStore.Audio.Media.DATA + " LIKE ?";
        final Cursor cursor = getSongsBySelection(selectionStatement, new String[]{path + "%"});
        if (cursor != null) {
            return Observable.create((ObservableOnSubscribe<Cursor>) e -> {
                while (cursor.moveToNext()) {
                    e.onNext(cursor);
                }
                e.onComplete();
                cursor.close();
            }).map(cursor1 -> {
                long id = cursor1.getLong(0);
                String title = cursor1.getString(1);
                String artist = cursor1.getString(2);
                String album = cursor1.getString(3);
                int duration = cursor1.getInt(4);
                int trackNumber = cursor1.getInt(5);
                long artistId = cursor1.getInt(6);
                long albumId = cursor1.getLong(7);
                String path1 = cursor1.getString(8);
                String displayName = cursor1.getString(9);
                long size = cursor1.getLong(10);
                return new Song(id, albumId, artistId, title, displayName, path1, duration, album, artist, size);
            }).toList().toFlowable();
        }
        return Observable.create((ObservableOnSubscribe<List<Song>>) e -> {
            e.onNext(new ArrayList<>());
            e.onComplete();
        }).toFlowable(BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<List<Song>> getQueueSongs() {
        return null;
    }

    @Override
    public Flowable<List<Song>> getAllSongs() {
        String selectionStatement = "is_music=1 AND title != ''";
        final Cursor cursor = getSongsBySelection(selectionStatement, null);
        if (cursor != null) {
            return Observable.create((ObservableOnSubscribe<Cursor>) e -> {
                while (cursor.moveToNext()) {
                    e.onNext(cursor);
                }
                e.onComplete();
                cursor.close();
            }).map(cursor1 -> {
                long id = cursor1.getLong(0);
                String title = cursor1.getString(1);
                String artist = cursor1.getString(2);
                String album = cursor1.getString(3);
                int duration = cursor1.getInt(4);
                int trackNumber = cursor1.getInt(5);
                long artistId = cursor1.getInt(6);
                long albumId = cursor1.getLong(7);
                String path = cursor1.getString(8);
                String displayName = cursor1.getString(9);
                long size = cursor1.getLong(10);
                return new Song(id, albumId, artistId, title, displayName, path, duration, album, artist, size);
            }).toList().toFlowable();
        }
        return Observable.create((ObservableOnSubscribe<List<Song>>) e -> {
            e.onNext(new ArrayList<>());
            e.onComplete();
        }).toFlowable(BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<List<Album>> getAllAlbums() {
        final Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, PROJECTION_ALBUMS,
                null, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            return Observable.create((ObservableOnSubscribe<Cursor>) e -> {
                while (cursor.moveToNext()) {
                    e.onNext(cursor);
                }
                e.onComplete();
                cursor.close();
            }).map(cursor1 -> new Album(cursor1.getLong(0), cursor1.getString(1), cursor1.getString(2),
                    cursor1.getLong(3), cursor1.getInt(4), cursor1.getInt(5))).toList().toFlowable();
        }
        return Observable.create((ObservableOnSubscribe<List<Album>>) e -> {
            e.onNext(new ArrayList<>());
            e.onComplete();
        }).toFlowable(BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<List<Artist>> getAllArtists() {
        final Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, PROJECTION_ARTISTS,
                null, null, MediaStore.Audio.Artists.DEFAULT_SORT_ORDER);
        if (cursor != null) {
            return Observable.create((ObservableOnSubscribe<Cursor>) e -> {
                while (cursor.moveToNext()) {
                    e.onNext(cursor);
                }
                e.onComplete();
                cursor.close();
            }).map(cursor1 -> new Artist(cursor1.getLong(0), cursor1.getString(1), cursor1.getInt(2), cursor1.getInt(3), null))
                    .toList().toFlowable();
        }
        return Observable.create((ObservableOnSubscribe<List<Artist>>) e -> {
            e.onNext(new ArrayList<>());
            e.onComplete();
        }).toFlowable(BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<List<Song>> getSongsForAlbum(final long albumID) {
        String string = "is_music=1 AND title != '' AND album_id=" + albumID;
        final Cursor cursor = getSongsBySelection(string, null);
        if (cursor != null) {
            return Observable.create((ObservableOnSubscribe<Cursor>) e -> {
                while (cursor.moveToNext()) {
                    e.onNext(cursor);
                }
                e.onComplete();
                cursor.close();
            }).map(cursor1 -> {
                long id = cursor1.getLong(0);
                String title = cursor1.getString(1);
                String artist = cursor1.getString(2);
                String album = cursor1.getString(3);
                int duration = cursor1.getInt(4);
                int trackNumber = cursor1.getInt(5);
                while (trackNumber >= 1000) {
                    trackNumber -= 1000;
                }
                long artistId = cursor1.getInt(6);
                long albumId = albumID;
                String path = cursor1.getString(8);
                String displayName = cursor1.getString(9);
                long size = cursor1.getLong(10);
                return new Song(id, albumId, artistId, title, displayName, path, duration, album, artist, size);
            }).toList().toFlowable();
        }
        return Observable.create((ObservableOnSubscribe<List<Song>>) e -> {
            e.onNext(new ArrayList<>());
            e.onComplete();
        }).toFlowable(BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<List<Song>> getSongsForArtist(long artistID) {
        String string = "is_music=1 AND title != '' AND artist_id=" + artistID;
        final Cursor cursor = getSongsBySelection(string, null);
        if (cursor != null) {
            return Observable.create((ObservableOnSubscribe<Cursor>) e -> {
                while (cursor.moveToNext()) {
                    e.onNext(cursor);
                }
                e.onComplete();
                cursor.close();
            }).map(cursor1 -> {
                long id = cursor1.getLong(0);
                String title = cursor1.getString(1);
                String artist = cursor1.getString(2);
                String album = cursor1.getString(3);
                int duration = cursor1.getInt(4);
                int trackNumber = cursor1.getInt(5);
                long artistId = artistID;
                long albumId = cursor1.getLong(7);
                String path = cursor1.getString(8);
                String displayName = cursor1.getString(9);
                long size = cursor1.getLong(10);
                return new Song(id, albumId, artistId, title, displayName, path, duration, album, artist, size);
            }).toList().toFlowable();
        }
        return Observable.create((ObservableOnSubscribe<List<Song>>) e -> {
            e.onNext(new ArrayList<>());
            e.onComplete();
        }).toFlowable(BackpressureStrategy.BUFFER);
    }

    @Override
    public Observable<Long> hasSong(final long songId) {
        Cursor cursor = getSongsBySelection("_id=?", new String[]{String.valueOf(songId)});
        if (cursor != null) {
            return Observable.create(e -> {
                if (cursor.getCount() > 0) {
                    e.onNext(0L);
                } else {
                    e.onNext(songId);
                }
                e.onComplete();
                cursor.close();
            });
        }
        return Observable.create(e -> e.onError(new Throwable("cursor is null")));
    }

    @Override
    public Flowable<List<Song>> hasSongs(List<Song> songs) {
        if (songs == null) return Observable.create((ObservableOnSubscribe<List<Song>>) e -> {
            e.onError(new Throwable("songs is null"));
        }).toFlowable(BackpressureStrategy.BUFFER);
        String selection = getSelectionBySongs(songs);
        Cursor cursor = getSongsBySelection(selection, null);
        if (cursor != null) {
            return Observable.create((ObservableOnSubscribe<Cursor>) e -> {
                while (cursor.moveToNext()) {
                    e.onNext(cursor);
                }
                e.onComplete();
                cursor.close();
            }).map(cursor1 -> {
                long id = cursor1.getLong(0);
                String title = cursor1.getString(1);
                String artist = cursor1.getString(2);
                String album = cursor1.getString(3);
                int duration = cursor1.getInt(4);
                int trackNumber = cursor1.getInt(5);
                long artistId = cursor1.getLong(6);
                long albumId = cursor1.getLong(7);
                String path = cursor1.getString(8);
                String displayName = cursor1.getString(9);
                long size = cursor1.getLong(10);
                return new Song(id, albumId, artistId, title, displayName, path, duration, album, artist, size);
            }).toList().toFlowable();
        }
        return Observable.create((ObservableOnSubscribe<List<Song>>) e -> {
            e.onError(new Throwable("cursor is null"));
        }).toFlowable(BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<List<Song>> hasSongsByIds(List<Long> songs) {
        if (songs == null) return Observable.create((ObservableOnSubscribe<List<Song>>) e -> {
            e.onError(new Throwable("songs is null"));
        }).toFlowable(BackpressureStrategy.BUFFER);
        String selection = getSelectionBySongIds(songs);
        Cursor cursor = getSongsBySelection(selection, null);
        if (cursor != null) {
            return Observable.create((ObservableOnSubscribe<Cursor>) e -> {
                while (cursor.moveToNext()) {
                    e.onNext(cursor);
                }
                e.onComplete();
                cursor.close();
            }).map(cursor1 -> {
                long id = cursor1.getLong(0);
                String title = cursor1.getString(1);
                String artist = cursor1.getString(2);
                String album = cursor1.getString(3);
                int duration = cursor1.getInt(4);
                int trackNumber = cursor1.getInt(5);
                long artistId = cursor1.getLong(6);
                long albumId = cursor1.getLong(7);
                String path = cursor1.getString(8);
                String displayName = cursor1.getString(9);
                long size = cursor1.getLong(10);
                return new Song(id, albumId, artistId, title, displayName, path, duration, album, artist, size);
            }).toList().toFlowable();
        }
        return Observable.create((ObservableOnSubscribe<List<Song>>) e -> {
            e.onError(new Throwable("cursor is null"));
        }).toFlowable(BackpressureStrategy.BUFFER);
    }

    @Override
    public Observable<Song> getSong(long songId) {
        Cursor cursor = getSongsBySelection("_id=?", new String[]{String.valueOf(songId)});
        if (cursor != null) {
            return Observable.create(e -> {
                if (cursor.moveToFirst()) {
                    long id = cursor.getLong(0);
                    String title = cursor.getString(1);
                    String artist = cursor.getString(2);
                    String album = cursor.getString(3);
                    int duration = cursor.getInt(4);
                    int trackNumber = cursor.getInt(5);
                    long artistId = cursor.getInt(6);
                    long albumId = cursor.getLong(7);
                    String path = cursor.getString(8);
                    String displayName = cursor.getString(9);
                    Song song = new Song(id, albumId, artistId, title, displayName, path, duration, album, artist, 0);
                    e.onNext(song);
                    e.onComplete();
                    cursor.close();
                }
            });
        }
        return Observable.create(e -> e.onError(new Throwable("cursor is null")));
    }

    @Override
    public Observable<Boolean> deleteSong(Song song) {
        return Observable.create(e -> {
            if (song == null) {
                e.onNext(false);
                e.onComplete();
                return;
            }
            boolean isSuccess = false;
            int ret = mContext.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    "_id=?", new String[]{String.valueOf(song.getId())});
            if (ret > 0) {
                File file = new File(song.getPath());
                if (file.delete()) {
                    isSuccess = true;
                } else {
                    LogTools.e("LocalMusicCursorSource", "song not delete path=" + song.getPath());
                }
            }
            e.onNext(isSuccess);
            e.onComplete();
        });

    }

    @Override
    public Observable<Boolean> deleteSong(long songId) {
        return Observable.create(e -> {
            Cursor cursor = getSongsBySelection("_id=?", new String[]{String.valueOf(songId)});
            boolean isSuccess = false;
            int ret = mContext.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    "_id=?", new String[]{String.valueOf(songId)});
            if (ret >= 0 && cursor != null && cursor.moveToFirst()) {
                String path = cursor.getString(8);
                File file = new File(path);
                cursor.close();
                if (file.delete()) {
                    isSuccess = true;
                } else {
                    LogTools.e("LocalMusicCursorSource", "song not delete path=" + path);
                }
            }
            e.onNext(isSuccess);
            e.onComplete();
        });
    }

    @Override
    public Observable<Boolean> deleteSongs(List<Song> list) {
        return Observable.create(e -> {
            if (list == null) {
                e.onNext(false);
                e.onComplete();
                return;
            }
            boolean isSuccess = false;
            String selectionStatement = getSelectionBySongs(list);
            int ret = mContext.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selectionStatement, null);
            if (ret > 0) {
                int i = 0;
                for (Song song : list) {
                    File file = new File(song.getPath());
                    if (!file.delete()) {
                        LogTools.e("LocalMusicCursorSource", "song not delete path=" + song.getPath());
                    } else {
                        i++;
                    }
                }
                isSuccess = i > 0;
            }
            e.onNext(isSuccess);
            e.onComplete();
        });
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
    public Observable<Boolean> deleteSongs(long[] songIds) {
        return Observable.create(e -> {
            if (songIds == null) {
                e.onNext(false);
                e.onComplete();
                return;
            }
            boolean isSuccess = false;
            StringBuilder selectionStatement = getSelectionByIds(songIds);
            int ret = mContext.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selectionStatement.toString(), null);
            if (ret > 0) {
                Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        PROJECTION_SONGS, selectionStatement.toString(), null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
                isSuccess = cursor != null && deleteSongs(cursor, 8);
            }
            e.onNext(isSuccess);
            e.onComplete();
        });
    }

    @Override
    public Flowable<List<Song>> deleteArtist(Artist artist) {
        if (artist == null) return Observable.create((ObservableOnSubscribe<List<Song>>) e -> {
            e.onError(new Throwable("artist is null"));
        }).toFlowable(BackpressureStrategy.BUFFER);
        return deleteArtist(artist.id);
    }

    @Override
    public Flowable<List<Song>> deleteArtist(long artistID) {
        String selectionStatement = "is_music=1 AND title != '' AND artist_id=" + artistID;
        Cursor cursor = getSongsBySelection(selectionStatement, null);
        int ret = mContext.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selectionStatement, null);
        if (cursor != null && ret > 0) {
            return Observable.create((ObservableOnSubscribe<Song>) e -> {
                cursor.moveToFirst();
                int i = 0;
                while (!cursor.isAfterLast()) {
                    String path = cursor.getString(8);
                    final File f = new File(path);
                    try {
                        if (!f.delete()) {
                            LogTools.e("LocalMusicCursorSource", "Failed to delete file " + path);
                            /**无法删除**/
                            i++;
                            Song song = getSong(cursor, path);
                            e.onNext(song);
                        } else {
                            i++;
                            Song song = getSong(cursor, path);
                            e.onNext(song);
                        }
                        cursor.moveToNext();
                    } catch (final SecurityException ex) {
                        cursor.moveToNext();
                    }
                }
                cursor.close();
                if (i > 0) {
                    e.onComplete();
                } else {
                    e.onError(new Throwable("delete failure no such artistID:" + artistID));
                }

            }).toList().toFlowable();
        }
        return Observable.create((ObservableOnSubscribe<List<Song>>) e -> {
            e.onError(new Throwable("cursor is null"));
        }).toFlowable(BackpressureStrategy.BUFFER);
    }

    @NonNull
    private Song getSong(Cursor cursor, String path) {
        long id = cursor.getLong(0);
        String title = cursor.getString(1);
        String artistName = cursor.getString(2);
        String album = cursor.getString(3);
        int duration = cursor.getInt(4);
        int trackNumber = cursor.getInt(5);
        long artistId = cursor.getInt(6);
        long albumId = cursor.getLong(7);
        String displayName = cursor.getString(9);
        long size = cursor.getLong(10);
        return new Song(id, albumId, artistId, title, displayName, path, duration, album, artistName, size);
    }

    @Override
    public Flowable<List<Song>> deleteAlbum(Album album) {
        if (album == null) return Observable.create((ObservableOnSubscribe<List<Song>>) e -> {
            e.onError(new Throwable("album is null"));
        }).toFlowable(BackpressureStrategy.BUFFER);
        return deleteAlbum(album.id);
    }

    @Override
    public Flowable<List<Song>> deleteAlbum(long albumId) {
        String selectionStatement = "is_music=1 AND title != '' AND album_id=" + albumId;
        Cursor cursor = getSongsBySelection(selectionStatement, null);
        int ret = mContext.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selectionStatement, null);
        if (cursor != null && ret > 0) {
            return Observable.create((ObservableOnSubscribe<Song>) e -> {
                cursor.moveToFirst();
                int i = 0;
                while (!cursor.isAfterLast()) {
                    String path = cursor.getString(8);
                    final File f = new File(path);
                    try {
                        if (!f.delete()) {
                            LogTools.e("LocalMusicCursorSource", "Failed to delete file " + path);
                            /**sd卡无法删除需要权限*/
                            i++;
                            Song song = getSong(albumId, cursor, path);
                            e.onNext(song);
                        } else {
                            i++;
                            Song song = getSong(albumId, cursor, path);
                            e.onNext(song);
                        }
                        cursor.moveToNext();
                    } catch (final SecurityException ex) {
                        cursor.moveToNext();
                    }
                }
                cursor.close();
                if (i > 0) {
                    e.onComplete();
                } else {
                    e.onError(new Throwable("delete failure no such albumId:" + albumId));
                }
            }).toList().toFlowable();
        }
        return Observable.create((ObservableOnSubscribe<List<Song>>) e -> e.onError(new Throwable("cursor is null")))
                .toFlowable(BackpressureStrategy.BUFFER);
    }

    @NonNull
    private Song getSong(long albumId, Cursor cursor, String path) {
        long id = cursor.getLong(0);
        String title = cursor.getString(1);
        String artistName = cursor.getString(2);
        String album = cursor.getString(3);
        int duration = cursor.getInt(4);
        int trackNumber = cursor.getInt(5);
        long artistId = cursor.getInt(6);
        String displayName = cursor.getString(9);
        long size = cursor.getLong(10);
        return new Song(id, albumId, artistId, title, displayName, path, duration, album, artistName, size);
    }

    @Override
    public Flowable<List<Song>> deleteFolder(String path) {
        if (path == null || path.length() == 0)
            return Observable.create((ObservableOnSubscribe<List<Song>>) e -> e.onError(new Throwable("path is null")))
                    .toFlowable(BackpressureStrategy.BUFFER);
        String selectionStatement = "is_music=1 AND title != '' AND " + MediaStore.Audio.Media.DATA + " LIKE ?";
        final Cursor cursor = getSongsBySelection(selectionStatement, new String[]{path + "%"});
        int ret = mContext.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selectionStatement, new String[]{path + "%"});
        if (cursor != null&&ret>0) {
            return Observable.create((ObservableOnSubscribe<Song>) e -> {
                cursor.moveToFirst();
                int i = 0;
                while (!cursor.isAfterLast()) {
                    String path1 = cursor.getString(8);
                    final File f = new File(path1);
                    try {
                        if (!f.delete()) {
                            LogTools.e("LocalMusicCursorSource", "Failed to delete file " + path1);
                            /*SD卡无法删除**/
                            i++;
                            Song song = getSong(cursor,path1);
                            e.onNext(song);
                        } else {
                            i++;
//                            long id = cursor.getLong(0);
//                            String title = cursor.getString(1);
//                            String artist = cursor.getString(2);
//                            String album = cursor.getString(3);
//                            int duration = cursor.getInt(4);
//                            int trackNumber = cursor.getInt(5);
//                            long artistId = cursor.getInt(6);
//                            long albumId = cursor.getLong(7);
//                            String displayName = cursor.getString(9);
//                            long size = cursor.getLong(10);
//                            Song song = new Song(id, albumId, artistId, title, displayName, path1, duration, album, artist, size);
                            Song song = getSong(cursor,path1);
                            e.onNext(song);
                        }
                        cursor.moveToNext();
                    } catch (final SecurityException ex) {
                        cursor.moveToNext();
                    }
                }
                cursor.close();
                if (i > 0) {
                    e.onComplete();
                } else {
                    e.onError(new Throwable("delete failure no such path" + path));
                }
            }).toList().toFlowable();
        }
        return Observable.create((ObservableOnSubscribe<List<Song>>) e -> e.onError(new Throwable("cursor is null")))
                .toFlowable(BackpressureStrategy.BUFFER);
    }

}

