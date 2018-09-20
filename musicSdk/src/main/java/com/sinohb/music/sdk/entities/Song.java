package com.sinohb.music.sdk.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class Song implements Parcelable {
    private long id; //id标识

    private long albumId;

    private long artistId;

    private String title; // 显示名称

    private String fileName; // 文件名称

    private String path; // 音乐文件的路径

    private int duration; // 媒体播放总时间

    private String albums; // 专辑

    private String artist; // 艺术家

    private long size;

    private long collectTime;

    private transient boolean isPlaying;

    private transient int songType;

    public Song(long id, long albumId, long artistId, String title, String fileName, String path, int duration,
                String albums, String artist, long size) {
        this.id = id;
        this.albumId = albumId;
        this.artistId = artistId;
        this.title = title;
        this.fileName = fileName;
        this.path = path;
        this.duration = duration;
        this.albums = albums;
        this.artist = artist;
        this.size = size;
    }

    public Song(long id, long albumId, long artistId, String title, String fileName, String path,
                String albums, String artist, long size) {
        this(id, albumId, artistId, title, fileName, path, 0, albums, artist, size);
    }

    public Song(long id, long albumId, long artistId, String title, String fileName, String path, int duration,
                String albums, String artist, long size, long collectTime) {
        this.id = id;
        this.albumId = albumId;
        this.artistId = artistId;
        this.title = title;
        this.fileName = fileName;
        this.path = path;
        this.duration = duration;
        this.albums = albums;
        this.artist = artist;
        this.size = size;
        this.collectTime = collectTime;
    }

    public Song(Parcel in) {
        id = in.readLong();
        albumId = in.readLong();
        artistId = in.readLong();
        title = in.readString();
        fileName = in.readString();
        path = in.readString();
        duration = in.readInt();
        albums = in.readString();
        artist = in.readString();
        size = in.readLong();
        collectTime = in.readLong();
    }

    public Song() {
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getFileName() {
        return fileName;
    }

    public void setFileName(@NonNull String fileName) {
        this.fileName = fileName;
    }

    @NonNull
    public String getPath() {
        return path;
    }

    public void setPath(@NonNull String path) {
        this.path = path;
    }

    @NonNull
    public int getDuration() {
        return duration;
    }

    public void setDuration(@NonNull int duration) {
        this.duration = duration;
    }

    @NonNull
    public String getAlbums() {
        return albums;
    }

    public void setAlbums(@NonNull String albums) {
        this.albums = albums;
    }

    @NonNull
    public String getArtist() {
        return artist;
    }

    public void setArtist(@NonNull String artist) {
        this.artist = artist;
    }

    @NonNull
    public long getSize() {
        return size;
    }

    public void setSize(@NonNull long size) {
        this.size = size;
    }

    public long getAlbumId() {
        return albumId;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public int getSongType() {
        return songType;
    }

    public void setSongType(int songType) {
        this.songType = songType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(albumId);
        parcel.writeLong(artistId);
        parcel.writeString(title);
        parcel.writeString(fileName);
        parcel.writeString(path);
        parcel.writeInt(duration);
        parcel.writeString(albums);
        parcel.writeString(artist);
        parcel.writeLong(size);
        parcel.writeLong(collectTime);
    }

    public void readFromParcel(Parcel dest) {
        id = dest.readLong();
        albumId = dest.readLong();
        artistId = dest.readLong();
        title = dest.readString();
        fileName = dest.readString();
        path = dest.readString();
        duration = dest.readInt();
        albums = dest.readString();
        artist = dest.readString();
        size = dest.readLong();
        collectTime = dest.readLong();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Song) {
            Song other = (Song) obj;
            return this.id == other.id;
        } else {
            return false;
        }
    }


}
