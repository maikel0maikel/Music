package com.sinohb.music.play;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.sinohb.base.tools.ToastTools;
import com.sinohb.music.R;
import com.sinohb.music.sdk.IPlayCallbacker;
import com.sinohb.music.sdk.IPlayService;
import com.sinohb.music.sdk.entities.Song;
import com.sinohb.music.sdk.player.PlayingSongListener;
import com.sinohb.music.sdk.service.MusicService;
import com.sinohb.music.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayManager implements IMusicPlayManager {

    private IPlayService playService;
    private IBinder mToken;
    private Context mContext;
    private MusicPlayConnection musicPlayConnection;
    private IPlayCallbacker playCallbacker;
    private List<IMusicPlayListener> mListeners;
    private List<IMediaChangeListener> mediaChangeListeners;
    private List<ISongDeleteListener> songDeleteListeners;
    private List<PlayingSongListener> playingSongListeners;
    private static IMusicPlayManager instance;
    private boolean isExit = false;
    private MusicPlayManager(IBinder token, Context context) {
        mToken = token;
        mContext = context.getApplicationContext();
        mediaChangeListeners = new ArrayList<>();
        songDeleteListeners = new ArrayList<>();
        playingSongListeners = new ArrayList<>();
        mListeners = new ArrayList<>();
        bindRemote();
    }

    public static IMusicPlayManager getInstance(@NonNull IBinder token, @NonNull Context context) {
        if (instance == null) {
            synchronized (token) {
                if (instance == null) {
                    instance = new MusicPlayManager(token, context);
                }
            }
        }
        return instance;
    }

    private void bindRemote() {
        musicPlayConnection = new MusicPlayConnection();
        Intent musicService = new Intent(mContext, MusicService.class);
        mContext.startService(musicService);
        mContext.bindService(musicService, musicPlayConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void play(Song song) {
        if (playService == null || song == null) return;
        try {
            playService.play(song);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playNext() {
        if (playService == null) return;
        try {
            playService.playNext();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playPre() {
        if (playService == null) return;
        try {
            playService.playPre();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean pause() {
        if (playService == null) return false;
        try {
            return playService.pause();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean resume() {
        if (playService == null) return false;
        try {
            return playService.resume();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void setMode(int mode) {
        if (playService == null) return;
        try {
            playService.setMode(mode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getMode() {
        if (playService == null) return 0;
        try {
            return playService.getMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean isPlaying() {
        if (playService == null) return false;
        try {
            return playService.isPlaying();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void seekTo(int position) {
        if (playService == null) return;
        try {
            playService.seekTo(position);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCurrentPos() {
        if (playService == null) return 0;
        try {
            return playService.getCurrentPos();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void removePlaySong(Song song) {
        if (playService == null || song == null) return;
        try {
            playService.removeSong(song);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Song getCurrentPlay() {
        if (playService == null) return null;
        try {
            return playService.getCurrentPlay();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getCurrentPlayId() {
        if (playService == null)return -1;
        try {
            return playService.getCurrentPlayId();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void pushPlayQueue(List<Song> songs) {
        if (playService == null || songs == null) return;
        try {
            playService.pushPlayQueue(songs);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pushPlayQueue(List<Song> songs, int playPos) {
        if (playService == null || songs == null) return;
        try {
            playService.pushSongsToPlay(songs, playPos);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void randamPlay(List<Song> songs) {
        if (playService == null || songs == null) return;
        try {
            playService.randanPlay(songs);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addSongToPlay(Song song) {
        if (playService == null || song == null) return;
        try {
            playService.addSongToPlay(song);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getAudioSessionId() {
        if (playService == null) return 0;
        try {
            return playService.getAudioSessionId();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void removeSongs(List<Song> songs) {
        if (songs == null || playService == null) return;
        try {
            playService.removeSongs(songs);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearPlayQueue() {
        if (playService != null) {
            try {
                playService.clearPlayQueue();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSongDeleted(int type) {
        if (songDeleteListeners != null) {
            for (ISongDeleteListener listener : songDeleteListeners) {
                listener.onDeleted(type);
            }
        }
    }

    @Override
    public List<Song> getPlaySongs() {
        if (playService == null) return null;
        try {
            return playService.getPlaySongs();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void destroy() {
        isExit = true;
        if (playService != null && playCallbacker != null) {
            try {
                playService.unRegistPlayCallbacker(playCallbacker);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        clearList(mListeners);
        clearList(mediaChangeListeners);
        clearList(songDeleteListeners);
        if (musicPlayConnection != null) {
            mContext.unbindService(musicPlayConnection);
        }
        mListeners = null;
        mediaChangeListeners = null;
        songDeleteListeners = null;

        instance = null;
    }

    void clearList(List list) {
        if (list != null) {
            list.clear();
        }
    }

    @Override
    public void setMusicPlayListener(IMusicPlayListener listener) {
        if (mListeners != null && !mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    @Override
    public void removeMusicPlayListtener(IMusicPlayListener listener) {
        if (mListeners != null) {
            mListeners.remove(listener);
        }
    }

    @Override
    public void setMediaChangedListener(IMediaChangeListener listener) {
        if (mediaChangeListeners != null && !mediaChangeListeners.contains(listener)) {
            mediaChangeListeners.add(listener);
        }
    }

    @Override
    public void removeMediaChangedListener(IMediaChangeListener listener) {
        if (mediaChangeListeners != null) {
            mediaChangeListeners.remove(listener);
        }
    }

    @Override
    public void addSongDeleteListener(ISongDeleteListener listener) {
        if (songDeleteListeners != null && !songDeleteListeners.contains(listener)) {
            songDeleteListeners.add(listener);
        }
    }

    @Override
    public void removeSongdeleteListener(ISongDeleteListener listener) {
        if (songDeleteListeners != null) {
            songDeleteListeners.remove(listener);
        }
    }

    @Override
    public void addPlayingSongListener(PlayingSongListener listener) {
        if (playingSongListeners != null && !playingSongListeners.contains(listener)) {
            playingSongListeners.add(listener);
        }
    }

    @Override
    public void removePlayingSongListener(PlayingSongListener listener) {
        if (playingSongListeners != null) {
            playingSongListeners.remove(listener);
        }
    }


    class RemotePlayCallback extends IPlayCallbacker.Stub {

        @Override
        public void onPrepared() {
            if (mListeners != null) {
                for (IMusicPlayListener listener : mListeners) {
                    listener.onPrepared();
                }
            }
        }

        @Override
        public void onPlaying(Song song) {
            if (mListeners != null) {
                for (IMusicPlayListener listener : mListeners) {
                    listener.onPlaying(song);
                }
            }
        }

        @Override
        public void onPlayProgress(int progress) {
            if (mListeners != null) {
                for (IMusicPlayListener listener : mListeners) {
                    listener.onPlayProgress(progress);
                }
            }
        }

        @Override
        public void onPlayComplete(Song song) {
            if (mListeners != null) {
                for (IMusicPlayListener listener : mListeners) {
                    listener.onPlayComplete(song);
                }
            }
        }

        @Override
        public void onSeekComplete(int progress)  {
            if (mListeners != null) {
                for (IMusicPlayListener listener : mListeners) {
                    listener.onSeekComplete(progress);
                }
            }
        }

        @Override
        public void onPlayError(Song song) {
            if (mListeners != null) {
                for (IMusicPlayListener listener : mListeners) {
                    listener.onPlayError(song);
                }
            }
        }

        @Override
        public void onPlayModeChanged(int mode) {
            if (mListeners != null) {
                for (IMusicPlayListener listener : mListeners) {
                    listener.onPlayModeChanged(mode);
                }
            }
        }

        @Override
        public void onMediaChanged(int event) {
            if (mediaChangeListeners != null) {
                for (IMediaChangeListener listener : mediaChangeListeners) {
                    listener.onMediaEventChanged(event);
                }
            }
        }

        @Override
        public void onPlayStop(Song song) {
            if (mListeners != null) {
                for (IMusicPlayListener listener : mListeners) {
                    listener.onPlayStop(song);
                }
            }
        }

        @Override
        public void onPause() {
            if (mListeners != null) {
                for (IMusicPlayListener listener : mListeners) {
                    listener.onPause();
                }
            }
        }

        @Override
        public void onResume() {
            if (mListeners != null) {
                for (IMusicPlayListener listener : mListeners) {
                    listener.onResume();
                }
            }
        }

        @Override
        public void onSongAdd(int added, int sourceCount) {
            if (added > 0) {
                ToastTools.showShorts(mContext, StringUtils.parsString(mContext, R.plurals.NNNtrackstoqueue, added));
            } else {
                if (sourceCount > 1) {
                    ToastTools.showShort(mContext, R.string.songs_havaadded_queue);
                } else {
                    ToastTools.showShort(mContext, R.string.song_hasadded_queue);
                }
            }
        }

        @Override
        public void onPlayingSongDeleted(int pos) {
            if (playingSongListeners != null) {
                for (PlayingSongListener listener : playingSongListeners) {
                    listener.onPlayingSongDeleted(pos);
                }
            }
        }

        @Override
        public void onPlayingSongPlayed(int pos) {
            if (playingSongListeners != null) {
                for (PlayingSongListener listener : playingSongListeners) {
                    listener.onPlayingSongPlayed(pos);
                }
            }
        }

        @Override
        public void onPlayingSongsDeleted() {
            if (playingSongListeners != null) {
                for (PlayingSongListener listener : playingSongListeners) {
                    listener.onPlayingSongsDeleted();
                }
            }
        }

        @Override
        public void onSongQueueEmpty() {
            if (playingSongListeners != null) {
                for (PlayingSongListener listener : playingSongListeners) {
                    listener.onSongQueueEmpty();
                }
            }
        }
    }


    private class MusicPlayConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            playService = IPlayService.Stub.asInterface(iBinder);
            try {
                playService.linkClientDeath(mToken);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            playCallbacker = new RemotePlayCallback();
            try {
                playService.registPlayCallbacker(playCallbacker);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            playService = null;
            musicPlayConnection = null;
            if (!isExit){
                bindRemote();
            }
        }
    }
}
