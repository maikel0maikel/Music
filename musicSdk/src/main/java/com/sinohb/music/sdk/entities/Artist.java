package com.sinohb.music.sdk.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bernie on 2017/4/12.
 */

public class Artist {
    public final int albumCount;
    public final long id;
    public final String name;
    public final int songCount;

    public List<Song> songs;

    public Artist() {
        this.id = -1;
        this.name = "";
        this.songCount = -1;
        this.albumCount = -1;
        songs = new ArrayList<Song>();
    }

    public Artist(long _id, String _name, int _albumCount, int _songCount, List<Song> songs) {
        this.id = _id;
        this.name = _name;
        this.songCount = _songCount;
        this.albumCount = _albumCount;
        this.songs = songs;
    }

}
