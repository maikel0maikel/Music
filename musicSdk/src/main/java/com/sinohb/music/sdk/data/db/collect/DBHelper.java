package com.sinohb.music.sdk.data.db.collect;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "music.db";
    public static final String TABLE_NAME = "song";
    public static final String TABLE_QUEUE_PLAY_BACK = "queue_play_back";
    public static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private String buildCreateCollectSql() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ").append(TABLE_NAME).append("(")
                .append(Column.KEY_ID).append(" integer PRIMARY KEY,").append(Column.KEY_TITLE)
                .append(" varchar(100),").append(Column.KEY_ARTIST).append(" varchar(100),")
                .append(Column.KEY_ALBUM).append(" varchar(100),").append(Column.KEY_DURATION)
                .append(" integer,").append(Column.KEY_TRACK).append(" integer,").append(Column.KEY_ARTIST_ID)
                .append(" integer,").append(Column.KEY_ALBUM_ID).append(" integer,").append(Column.KEY_PATH)
                .append(" text,").append(Column.KEY_DISPLAY_NAME).append(" varchar(100),").append(Column.KEY_COLLECT_TIME)
                .append(" integer )");/**.append(Column.KEY_PLAY_OR_COLLECT).append(" integer DEFAULT 1)")**/
        return builder.toString();
    }

    private String buildCreatePlayBackSql() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ").append(TABLE_QUEUE_PLAY_BACK).append("(")
                .append(Column.KEY_ID).append(" integer PRIMARY KEY )");
        return builder.toString();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(buildCreateCollectSql());
        sqLiteDatabase.execSQL(buildCreatePlayBackSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS song");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS queue_play_back");
        onCreate(sqLiteDatabase);
    }

    public static class Column {
        public static final String KEY_ID = "_id";
        public static final String KEY_TITLE = "title";
        public static final String KEY_ARTIST = "artist";
        public static final String KEY_ALBUM = "album";
        public static final String KEY_DURATION = "duration";
        public static final String KEY_TRACK = "track";
        public static final String KEY_ARTIST_ID = "artist_id";
        public static final String KEY_ALBUM_ID = "album_id";
        public static final String KEY_PATH = "_data";
        public static final String KEY_DISPLAY_NAME = "_display_name";
        public static final String KEY_COLLECT_TIME = "collect_time";
//        public static final String KEY_PLAY_OR_COLLECT = "type";
    }
}
