package com.sinohb.music.sdk.data.db.collect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sinohb.logger.LogTools;
import com.sinohb.music.sdk.entities.Song;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;


public class DBCollectSource implements CollectSorce {
    private static final String TAG = DBCollectSource.class.getSimpleName();
    private static final String[] PROJECT = {DBHelper.Column.KEY_ID, DBHelper.Column.KEY_TITLE, DBHelper.Column.KEY_ARTIST, DBHelper.Column.KEY_ALBUM,
            DBHelper.Column.KEY_DURATION, DBHelper.Column.KEY_TRACK, DBHelper.Column.KEY_ARTIST_ID, DBHelper.Column.KEY_ALBUM_ID, DBHelper.Column.KEY_PATH,
            DBHelper.Column.KEY_DISPLAY_NAME, DBHelper.Column.KEY_COLLECT_TIME,};
    private DBHelper dbHelper;
    private List<DataChangeListener> mListeners;
    private static CollectSorce collectSorce;

    private DBCollectSource(Context context) {
        dbHelper = new DBHelper(context.getApplicationContext());
        mListeners = new ArrayList<>();
    }

    public static CollectSorce getInstance(Context context) {
        if (collectSorce == null) {
            synchronized (DBCollectSource.class) {
                if (collectSorce == null) {
                    collectSorce = new DBCollectSource(context);
                }
            }
        }
        return collectSorce;
    }

    @Override
    public Observable clearAll() {
        return Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("DELETE FROM " + DBHelper.TABLE_NAME);
            e.onNext(true);
            e.onComplete();
        });
    }

    @Override
    public Flowable<List<Song>> getAllSongs() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final Cursor cursor = db.query(DBHelper.TABLE_NAME, PROJECT,
                null, null, null, null, DBHelper.Column.KEY_COLLECT_TIME);
        if (cursor != null) {
            return Observable.create((ObservableOnSubscribe<Cursor>) e -> {
                while (cursor.moveToNext() && !cursor.isClosed()) {
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
                Song song = new Song(id, albumId, artistId, title, displayName, path, duration, album, artist, 0);
                song.setCollectTime(cursor.getInt(10));
                return song;
            }).toList().toFlowable();
        }
        return Observable.create((ObservableOnSubscribe<List<Song>>) e -> {
            e.onNext(new ArrayList<>());
            e.onComplete();
        }).toFlowable(BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<List<Long>> getPlaySongs() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final Cursor cursor = db.query(DBHelper.TABLE_QUEUE_PLAY_BACK, new String[]{DBHelper.Column.KEY_ID},
                null, null,
                null, null, null);
        if (cursor != null) {
            return Observable.create((ObservableOnSubscribe<Cursor>) e -> {
                while (cursor.moveToNext() && !cursor.isClosed()) {
                    e.onNext(cursor);
                }
                e.onComplete();
                cursor.close();
            }).map(cursor1 -> cursor1.getLong(0)).toList().toFlowable();
        }
        return Observable.create((ObservableOnSubscribe<List<Long>>) e -> {
            e.onNext(new ArrayList<>());
            e.onComplete();
        }).toFlowable(BackpressureStrategy.BUFFER);
    }

    private Flowable<List<Long>> hasSongs(List<Long> songs) {

        if (songs == null) return Observable.create((ObservableOnSubscribe<List<Long>>) e -> {
            e.onError(new Throwable("songs is null"));
        }).toFlowable(BackpressureStrategy.BUFFER);
        String selection = getSelectionBySongs(songs);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final Cursor cursor = db.query(DBHelper.TABLE_QUEUE_PLAY_BACK, new String[]{DBHelper.Column.KEY_ID},
                selection, null, null, null, null);
        if (cursor != null) {
            return Observable.create((ObservableOnSubscribe<Cursor>) e -> {
                while (cursor.moveToNext()) {
                    e.onNext(cursor);
                }
                e.onComplete();
                cursor.close();
            }).map(cursor1 -> cursor1.getLong(0)).toList().toFlowable();
        }
        return Observable.create((ObservableOnSubscribe<List<Long>>) e -> {
            e.onError(new Throwable("cursor is null"));
        }).toFlowable(BackpressureStrategy.BUFFER);
    }

    private String getSelectionBySongs(List<Long> list) {
        StringBuilder selectionStatement = new StringBuilder();
        selectionStatement.append("_id IN ( ");
        int i = 0;
        for (long songId : list) {
            selectionStatement.append(songId);
            if (i < list.size() - 1) {
                selectionStatement.append(",");
            }
            i++;
        }
        selectionStatement.append(" ) ");
        return selectionStatement.toString();
    }

    @Override
    public Observable<Boolean> savePlaySongs(List<Long> songs) {
        if (songs == null) {
            return Observable.create(e -> {
                e.onNext(false);
                e.onComplete();
            });
        }
        return hasSongs(songs).map(longs -> {
            songs.removeAll(longs);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                boolean result = false;
                for (Long id : songs) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBHelper.Column.KEY_ID, id);
                    long ret = db.insert(DBHelper.TABLE_QUEUE_PLAY_BACK, null, contentValues);
                    result = ret >= 0;
                }
                if (result) {
                    db.setTransactionSuccessful();
                    //notifyDataChange();
                    return true;
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            } finally {
                try {
                    if (null != db) {
                        db.endTransaction();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            return false;
        }).toObservable();
    }

    @Override
    public Observable<Boolean> deletePlaySongs(List<Long> songs) {
        if (songs == null) {
            return Observable.just(false);
        }
        return Observable.create(e -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                int delete = 0;
                for (long id : songs) {
                    int ret = db.delete(DBHelper.TABLE_QUEUE_PLAY_BACK, DBHelper.Column.KEY_ID + "=? ",
                            new String[]{String.valueOf(id)});
                    if (ret > 0) {
                        delete++;
                    }
                }
                transResult(e, db, delete);
            } catch (Exception e1) {
                e1.printStackTrace();
                e.onNext(false);
                e.onComplete();
            } finally {
                try {
                    if (null != db) {
                        db.endTransaction();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });
    }

    @Override
    public Observable<Song> getSong(long songId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final Cursor cursor = db.query(DBHelper.TABLE_NAME, PROJECT,
                DBHelper.Column.KEY_ID + "=? ",
                new String[]{String.valueOf(songId)}, null, null, DBHelper.Column.KEY_COLLECT_TIME);
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
                    song.setCollectTime(cursor.getInt(10));
                    e.onNext(song);
                    e.onComplete();
                    cursor.close();
                }
            });
        }
        return Observable.create(e -> e.onError(new Throwable("cursor is null")));
    }

    @Override
    public Observable<Boolean> hasSong(long songId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final Cursor cursor = db.query(DBHelper.TABLE_NAME, new String[]{DBHelper.Column.KEY_ID},
                DBHelper.Column.KEY_ID + "=? ",
                new String[]{String.valueOf(songId)},
                null, null, DBHelper.Column.KEY_COLLECT_TIME);
        if (cursor != null) {
            return Observable.create(e -> {
                e.onNext(cursor.getCount() > 0);
                e.onComplete();
                cursor.close();
            });
        }
        return Observable.create(e -> {
            e.onNext(false);
            e.onComplete();
        });
    }


    @Override
    public Observable<Boolean> saveSong(Song song) {
        if (song == null) {
            LogTools.e(TAG, "saveFreq is error object is null");
            return Observable.create(e -> {
                e.onNext(false);
                e.onComplete();
            });
        }
        return Observable.create(e -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.Column.KEY_ID, song.getId());
            contentValues.put(DBHelper.Column.KEY_TITLE, song.getTitle());
            contentValues.put(DBHelper.Column.KEY_ARTIST, song.getArtist());
            contentValues.put(DBHelper.Column.KEY_ALBUM, song.getAlbums());
            contentValues.put(DBHelper.Column.KEY_DURATION, song.getDuration());
            contentValues.put(DBHelper.Column.KEY_TRACK, 0);
            contentValues.put(DBHelper.Column.KEY_ARTIST_ID, song.getArtistId());
            contentValues.put(DBHelper.Column.KEY_ALBUM_ID, song.getAlbumId());
            contentValues.put(DBHelper.Column.KEY_PATH, song.getPath());
            contentValues.put(DBHelper.Column.KEY_DISPLAY_NAME, song.getFileName());
            contentValues.put(DBHelper.Column.KEY_COLLECT_TIME, System.currentTimeMillis());
//            if (song.getSongType() == TYPE_PLAY_BACKGROUND) {
//                contentValues.put(DBHelper.Column.KEY_PLAY_OR_COLLECT, TYPE_PLAY_BACKGROUND);
//            } else {
//                contentValues.put(DBHelper.Column.KEY_PLAY_OR_COLLECT, TYPE_COLLECT);
//            }
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            long ret = db.insert(DBHelper.TABLE_NAME, null, contentValues);
            e.onNext(ret >= 0);
            e.onComplete();
            notifyDataChange();
        });
    }

    @Override
    public Observable<Boolean> saveSongs(List<Song> songs) {
        if (songs == null) {
            return Observable.create(e -> {
                e.onNext(false);
                e.onComplete();
            });
        }
        return Observable.create(e -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                boolean result = true;
                for (Song song : songs) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBHelper.Column.KEY_ID, song.getId());
                    contentValues.put(DBHelper.Column.KEY_TITLE, song.getTitle());
                    contentValues.put(DBHelper.Column.KEY_ARTIST, song.getArtist());
                    contentValues.put(DBHelper.Column.KEY_ALBUM, song.getAlbums());
                    contentValues.put(DBHelper.Column.KEY_DURATION, song.getDuration());
                    contentValues.put(DBHelper.Column.KEY_TRACK, 0);
                    contentValues.put(DBHelper.Column.KEY_ARTIST_ID, song.getArtistId());
                    contentValues.put(DBHelper.Column.KEY_ALBUM_ID, song.getAlbumId());
                    contentValues.put(DBHelper.Column.KEY_PATH, song.getPath());
                    contentValues.put(DBHelper.Column.KEY_DISPLAY_NAME, song.getFileName());
                    contentValues.put(DBHelper.Column.KEY_COLLECT_TIME, System.currentTimeMillis());
//                    if (song.getSongType() == TYPE_PLAY_BACKGROUND) {
//                        contentValues.put(DBHelper.Column.KEY_PLAY_OR_COLLECT, TYPE_PLAY_BACKGROUND);
//                    } else {
//                        contentValues.put(DBHelper.Column.KEY_PLAY_OR_COLLECT, TYPE_COLLECT);
//                    }
                    long ret = db.insert(DBHelper.TABLE_NAME, null, contentValues);
                    result = ret >= 0;
                }
                if (result) {
                    db.setTransactionSuccessful();

                    notifyDataChange();
                } else {
                    e.onNext(false);
                }
                e.onComplete();
            } catch (Exception e1) {
                e1.printStackTrace();
                e.onNext(false);
                e.onComplete();
            } finally {
                try {
                    if (null != db) {
                        db.endTransaction();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });
    }

    @Override
    public Observable<Boolean> updateSong(Song song) {
        if (song == null) {
            return Observable.create(e -> {
                e.onNext(false);
                e.onComplete();
            });
        }
        return Observable.create(e -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.Column.KEY_TITLE, song.getTitle());
            contentValues.put(DBHelper.Column.KEY_ARTIST, song.getArtist());
            contentValues.put(DBHelper.Column.KEY_ALBUM, song.getAlbums());
            contentValues.put(DBHelper.Column.KEY_DURATION, song.getDuration());
            contentValues.put(DBHelper.Column.KEY_TRACK, 0);
            contentValues.put(DBHelper.Column.KEY_ARTIST_ID, song.getArtistId());
            contentValues.put(DBHelper.Column.KEY_ALBUM_ID, song.getAlbumId());
            contentValues.put(DBHelper.Column.KEY_PATH, song.getPath());
            contentValues.put(DBHelper.Column.KEY_DISPLAY_NAME, song.getFileName());
            contentValues.put(DBHelper.Column.KEY_COLLECT_TIME, System.currentTimeMillis());
            int ret = db.update(DBHelper.TABLE_NAME, contentValues, DBHelper.Column.KEY_ID + "=? ",
                    new String[]{String.valueOf(song.getId())});
            e.onNext(ret >= 0);
            e.onComplete();
            notifyDataChange();
        });

    }

    @Override
    public Observable<Boolean> updateSongs(List<Song> songs) {
        if (songs == null) {
            return Observable.create(e -> {
                e.onNext(false);
                e.onComplete();
            });
        }
        return Observable.create(e -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                boolean result = true;
                for (Song song : songs) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBHelper.Column.KEY_TITLE, song.getTitle());
                    contentValues.put(DBHelper.Column.KEY_ARTIST, song.getArtist());
                    contentValues.put(DBHelper.Column.KEY_ALBUM, song.getAlbums());
                    contentValues.put(DBHelper.Column.KEY_DURATION, song.getDuration());
                    contentValues.put(DBHelper.Column.KEY_TRACK, 0);
                    contentValues.put(DBHelper.Column.KEY_ARTIST_ID, song.getArtistId());
                    contentValues.put(DBHelper.Column.KEY_ALBUM_ID, song.getAlbumId());
                    contentValues.put(DBHelper.Column.KEY_PATH, song.getPath());
                    contentValues.put(DBHelper.Column.KEY_DISPLAY_NAME, song.getFileName());
                    contentValues.put(DBHelper.Column.KEY_COLLECT_TIME, System.currentTimeMillis());
                    int ret = db.update(DBHelper.TABLE_NAME, contentValues, DBHelper.Column.KEY_ID + "=? ",
                            new String[]{String.valueOf(song.getId())});
                    result = ret >= 0;
                }
                if (result) {
                    db.setTransactionSuccessful();
                    e.onNext(true);
                    e.onComplete();
                    notifyDataChange();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                e.onNext(false);
                e.onComplete();
            } finally {
                try {
                    if (null != db) {
                        db.endTransaction();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });
    }

    @Override
    public Observable<Boolean> deleteSong(Song song) {
        if (song == null) {
            LogTools.e(TAG, "saveFreq is error object is null");
            return Observable.create(e -> {
                e.onNext(false);
                e.onComplete();
            });
        }
        return deleteSong(song.getId());

    }

    @Override
    public Observable<Boolean> deleteSong(long songId) {
        return Observable.create(e -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            int ret = db.delete(DBHelper.TABLE_NAME, DBHelper.Column.KEY_ID + "=? ",
                    new String[]{String.valueOf(songId)});
            e.onNext(ret > 0);
            e.onComplete();
            notifyDataChange();
        });
    }

    @Override
    public Observable<Boolean> deleteSongs(List<Song> songs) {
        if (songs == null) {
            return Observable.just(false);
        }
        return Observable.create(e -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                int delete = 0;
                for (Song song : songs) {
                    int ret = db.delete(DBHelper.TABLE_NAME, DBHelper.Column.KEY_ID + "=? ",
                            new String[]{String.valueOf(song.getId())});
                    if (ret > 0) {
                        delete++;
                    }
                }
                transResult(e, db, delete);
            } catch (Exception e1) {
                e1.printStackTrace();
                e.onNext(false);
                e.onComplete();
            } finally {
                try {
                    if (null != db) {
                        db.endTransaction();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });
    }

    private void transResult(ObservableEmitter<Boolean> e, SQLiteDatabase db, int delete) {
        if (delete > 0) {
            db.setTransactionSuccessful();
            e.onNext(true);
            e.onComplete();
            notifyDataChange();
        } else {
            e.onNext(false);
            e.onComplete();
        }
    }

    @Override
    public Observable<Boolean> deleteSongs(long[] songIds) {
        return Observable.create(e -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                int delete = 0;
                for (long id : songIds) {
                    int ret = db.delete(DBHelper.TABLE_NAME, DBHelper.Column.KEY_ID + "=? ",
                            new String[]{String.valueOf(id)});
                    if (ret > 0) {
                        delete++;
                    }
                }
                transResult(e, db, delete);
            } catch (Exception e1) {
                e1.printStackTrace();
                e.onNext(false);
                e.onComplete();
            } finally {
                try {
                    if (null != db) {
                        db.endTransaction();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });
    }

    @Override
    public Observable<Boolean> deleteSongByIds(List<Long> ids) {
        if (ids == null) {
            return Observable.just(false);
        }
        return Observable.create(e -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                db.beginTransaction();
                int delete = 0;
                for (long id : ids) {
                    int ret = db.delete(DBHelper.TABLE_NAME, DBHelper.Column.KEY_ID + "=?",
                            new String[]{String.valueOf(id)});
                    if (ret > 0) {
                        delete++;
                    }
                }
                transResult(e, db, delete);
            } catch (Exception e1) {
                e1.printStackTrace();
                e.onNext(false);
                e.onComplete();
            } finally {
                try {
                    if (null != db) {
                        db.endTransaction();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });
    }

    @Override
    public Observable<Boolean> deleteAritst(long artistId) {
        return Observable.create(e -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            int ret = db.delete(DBHelper.TABLE_NAME, DBHelper.Column.KEY_ARTIST_ID + "=? ",
                    new String[]{String.valueOf(artistId)});
            e.onNext(ret > 0);
            e.onComplete();
            if (ret > 0) {
                notifyDataChange();
            }
        });
    }

    @Override
    public Observable<Boolean> deleteAlbum(long albumId) {
        return Observable.create(e -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            int ret = db.delete(DBHelper.TABLE_NAME, DBHelper.Column.KEY_ALBUM_ID + "=? ",
                    new String[]{String.valueOf(albumId)});
            e.onNext(ret > 0);
            e.onComplete();
            if (ret > 0) {
                notifyDataChange();
            }
        });
    }

    @Override
    public Observable<Boolean> deleteFolder(String path) {
        return Observable.create(e -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            int ret = db.delete(DBHelper.TABLE_NAME, DBHelper.Column.KEY_PATH + " LIKE ? ",
                    new String[]{path + "%"});
            e.onNext(ret > 0);
            e.onComplete();
            notifyDataChange();
        });
    }

    @Override
    public void close() {
        if (dbHelper != null) {
            dbHelper.getWritableDatabase().close();
        }
        mListeners.clear();
        mListeners = null;
    }

    @Override
    public void setDataChangeListener(DataChangeListener listener) {
        if (mListeners != null && !mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    @Override
    public void removeChangeListener(DataChangeListener listener) {
        if (mListeners != null)
            mListeners.remove(listener);
    }

    private void notifyDataChange() {
        if (mListeners != null) {
            for (DataChangeListener listener : mListeners) {
                listener.onDataChange();
            }
        }
    }
}
