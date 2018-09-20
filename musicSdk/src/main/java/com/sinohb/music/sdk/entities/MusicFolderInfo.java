package com.sinohb.music.sdk.entities;

/**
 * Created by bernie on 2017/4/5.
 */

public class MusicFolderInfo {
    public final String folderName;
    public final String folderPath;
    public final int songCount;

    public MusicFolderInfo() {
        this.folderName = "";
        this.folderPath = "";
        this.songCount = -1;
    }

    public MusicFolderInfo(String _folderName, String _folderPath, int _songCount) {
        this.folderName = _folderName;
        this.folderPath = _folderPath;
        this.songCount = _songCount;
    }
}
