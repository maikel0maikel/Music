package com.sinohb.music.sdk.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.sinohb.music.sdk.IPlayCallbacker;
import com.sinohb.music.sdk.IPlayService;
import com.sinohb.music.sdk.data.db.android.DataSource;
import com.sinohb.music.sdk.data.db.collect.CollectSorce;
import com.sinohb.music.sdk.entities.Song;
import com.sinohb.music.sdk.player.IMusicPlayerManager;
import com.sinohb.music.sdk.player.PlayingSongListener;
import com.sinohb.music.sdk.tools.ConstantTools;
import com.sinohb.music.sdk.tools.SharedPreferencesTools;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MusicPlayServiceManager extends IPlayService.Stub implements IMusicPlayerManager.PlayListener<Song>,
        PlayingSongListener, MediaHandler.AudioListener {
    private IMusicPlayerManager<Song> mPlayer;//音乐播放管理
    private List<IPlayCallbacker> iPlayCallbacker;//远程客户端回调
    private ClientDeathRecipient clientDeathRecipient;//客户端死亡通知回调

    private static MusicPlayServiceManager service;
    private Handler mHandler;
    private ProgressRunnalbe mRunnable;
    private static final long DELAY_GET_PROGRESS = 500;
    private Context mContext;
    private MediaChangeReceiver mMediaChangeReceiver;
    private CollectSorce collectSorce;
    private DataSource dataSource;
    //    private StorageObserver storageObserver;
    public static final int MEDIA_MOUNTED = 1;
    public static final int MEDIA_UNMOUNTED = 2;
    public static final int MEDIA_REMOVED = 3;
    public static final int MEDIA_SCANNER_STARTED = 4;
    public static final int MEDIA_SCANNER_FINISHED = 5;
    public static final int MEDIA_EJECT = 6;
    private MediaHandler mediaHandler;

    private MusicPlayServiceManager(@NonNull IMusicPlayerManager<Song> player,
                                    @NonNull ClientDeathRecipient clientDeathRecipient,
                                    @NonNull Context context, CollectSorce collectSorce, DataSource dataSource) {
        this.mPlayer = player;
        this.iPlayCallbacker = new ArrayList<>();
        this.clientDeathRecipient = clientDeathRecipient;
        mPlayer.setPlayListener(this);
        mPlayer.setPlayingSongListener(this);
        mHandler = new Handler(Looper.getMainLooper());
        mRunnable = new ProgressRunnalbe(this);
        mContext = context.getApplicationContext();
        registMediaReceiver();
        this.collectSorce = collectSorce;
        this.dataSource = dataSource;
//        storageObserver = new StorageObserver("/sdcard/");
//        storageObserver.startWatching();
        initPlayMode();
        initSongQueue();
        mediaHandler = new MediaHandler(mContext);
        mediaHandler.setAudioListener(this);
    }

    static MusicPlayServiceManager getInstance(@NonNull IMusicPlayerManager<Song> player,
                                               @NonNull ClientDeathRecipient clientDeathRecipient,
                                               @NonNull Context context,
                                               CollectSorce collectSorce,
                                               DataSource dataSource) {
        if (service == null) {
            synchronized (MusicPlayServiceManager.class) {
                if (service == null) {
                    service = new MusicPlayServiceManager(player, clientDeathRecipient, context, collectSorce, dataSource);
                }
            }
        }
        return service;
    }

    /**
     * sd卡变动广播
     */
    private void registMediaReceiver() {
        if (mMediaChangeReceiver == null) {
            mMediaChangeReceiver = new MediaChangeReceiver();
            IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
            filter.setPriority(1000);
            filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            filter.addAction(Intent.ACTION_MEDIA_REMOVED);
            filter.addAction(Intent.ACTION_MEDIA_SHARED);
            filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
            filter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
            filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
            filter.addAction(Intent.ACTION_MEDIA_CHECKING);
            filter.addAction(Intent.ACTION_MEDIA_EJECT);
            filter.addAction(Intent.ACTION_MEDIA_NOFS);
            filter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            filter.addAction(ConstantTools.ACTION_KEY_CODE);
            filter.addDataScheme("file");
            mContext.registerReceiver(mMediaChangeReceiver, filter);
        }
    }

    private void unRegistMediaReceiver() {
        if (mMediaChangeReceiver != null) {
            mContext.unregisterReceiver(mMediaChangeReceiver);
        }
    }

    private void initPlayMode() {
        int mode = SharedPreferencesTools.getInt(mContext, ConstantTools.KEY_PLAY_MODE, 0);
        if (mode != 0) setMode(mode);
    }

    private void initSongQueue() {
        collectSorce.getPlaySongs().flatMap(Flowable::fromIterable).toList()
                .flatMap((Function<List<Long>, SingleSource<List<Song>>>)
                        songs -> dataSource.hasSongsByIds(songs)
                                .flatMap(Flowable::fromIterable)
                                .toList()).subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<List<Song>>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onSuccess(List<Song> songs) {
                        if (songs != null && !songs.isEmpty()) {
                            mPlayer.resumePlayBackgroundQueue(songs);
                            findCurrentSong(songs);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });

    }

    private void findCurrentSong(List<Song> songs) {
        Observable.create((ObservableOnSubscribe<Song>) e -> {
            long songId = SharedPreferencesTools.getLong(mContext, ConstantTools.KEY_PLAYING_SONG_ID, -1);
            for (Song song : songs) {
                if (song.getId() == songId) {
                    e.onNext(song);
                    break;
                }
            }
            e.onComplete();
        }).subscribeOn(Schedulers.io()).subscribe(new Observer<Song>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Song song) {
                if (song != null) {
                    int progerss = SharedPreferencesTools.getInt(mContext, ConstantTools.KEY_PROGRESS, 0);
                    mPlayer.resumePlayBackgroundSong(song, progerss);
                    onPlay(song);
                }
            }

            @Override
            public void onError(Throwable e) {
                disposable.dispose();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
    }

    /**
     * 播放指定音乐
     *
     * @param song 音乐
     */
    @Override
    public void play(Song song) {
        if (mediaHandler.requestAudioFocus() != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return;
        }
        if (mPlayer != null)
            mPlayer.playAsync(song);
    }

    /**
     * 播放下一首
     */
    @Override
    public synchronized void playNext() {
        if (mediaHandler.requestAudioFocus() != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            onPlayStop();
            Log.e("music--","playNext stop");
            return;
        }
        if (mPlayer != null) {
            mPlayer.playNext(true);
        }
        Log.e("music--","playNext");
    }

    /**
     * 播放上一首
     */
    @Override
    public void playPre() {
        if (mediaHandler.requestAudioFocus() != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return;
        }
        if (mPlayer != null)
            mPlayer.playPre(true);
    }

    /**
     * 暂停
     *
     * @return 返回操作是否成功
     */
    @Override
    public boolean pause() {
        if (mPlayer != null && mPlayer.pause()) {
            mHandler.removeCallbacks(mRunnable);
            onPause();
            return true;
        }
        return false;
    }

    private void onPause() {
        for (IPlayCallbacker callbacker : iPlayCallbacker) {
            try {
                callbacker.onPause();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 恢复播放
     *
     * @return 返回操作是否成功
     */
    @Override
    public boolean resume() {
        if (mediaHandler.requestAudioFocus() != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return false;
        }
        if (mPlayer != null && mPlayer.resume()) {
            mHandler.removeCallbacks(mRunnable);
            mHandler.postDelayed(mRunnable, DELAY_GET_PROGRESS);
            onResume();
            return true;
        }
        return false;

    }

    private void onResume() {
        for (IPlayCallbacker callbacker : iPlayCallbacker) {
            try {
                callbacker.onResume();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置播放模式
     *
     * @param mode 模式：随机、单曲循环等
     */
    @Override
    public void setMode(int mode) {
        if (mPlayer != null) {
            mPlayer.setMode(mode);
            for (IPlayCallbacker callbacker : iPlayCallbacker) {
                try {
                    callbacker.onPlayModeChanged(mode);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int getMode() {
        return mPlayer == null ? -1 : mPlayer.getMode();
    }

    @Override
    public boolean isPlaying() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    /**
     * 调节进度
     *
     * @param position 进度
     */
    @Override
    public synchronized void seekTo(int position) {
        if (getMode() == IMusicPlayerManager.MODE_BROWSE&&position>=10000) {
            //removeRunnable();
            Log.e("music--","seekTo");
            playNext();
            return;
        }
        if (mPlayer != null && mPlayer.seekTo(position)) {
        }
    }

    /**
     * 获取当前播放位置
     *
     * @return 返回播放位置
     */
    @Override
    public int getCurrentPos() {
        return mPlayer == null ? -1 : mPlayer.getCurrentPlayPosition();
    }

    @Override
    public Song getCurrentPlay() {
        return mPlayer == null ? null : mPlayer.getCurrentPlay();
    }

    @Override
    public long getCurrentPlayId() {
        return getCurrentPlay() == null ? -1 : getCurrentPlay().getId();
    }

    /**
     * 添加一首歌到播放列表播放
     *
     * @param song 歌曲
     */
    @Override
    public void addSongToPlay(Song song) {
        if (mPlayer != null) mPlayer.addSongToPlay(song);
    }

    /**
     * 添加歌曲列表
     *
     * @param songs 歌曲列表
     */
    @Override
    public void pushPlayQueue(List<Song> songs) {
        if (mPlayer != null) mPlayer.pushPlayQueue(songs);
    }

    /**
     * 随机播放这个列表
     *
     * @param songs 歌曲列表
     */
    @Override
    public void randanPlay(List<Song> songs) {
        if (mPlayer != null) mPlayer.randamPlay(songs);
    }

    /**
     * 添加歌曲列表并替换当前播放的列表
     *
     * @param songs 歌曲列表
     * @param pos   歌曲中的位置
     */
    @Override
    public void pushSongsToPlay(List<Song> songs, int pos) {
        if (mPlayer != null) mPlayer.pushPlayQueue(songs, pos);
    }

    @Override
    public int getAudioSessionId() {
        return mPlayer == null ? 0 : mPlayer.getAudioSessionId();
    }

    /**
     * 获取当前播放的列表
     *
     * @return 返回歌曲列表
     */
    @Override
    public List<Song> getPlaySongs() {
        return mPlayer == null ? null : mPlayer.getPlayQueuq();
    }

    @Override
    public void clearPlayQueue() {
        if (mPlayer != null) mPlayer.clearPlayQueue();
    }

    /**
     * 删除播放列表中的歌曲
     *
     * @param song 歌曲
     */
    @Override
    public void removeSong(Song song) {
        if (mPlayer != null) mPlayer.removeSong(song);
    }

    /**
     * 删除歌曲列表
     *
     * @param songs 歌曲
     */
    @Override
    public void removeSongs(List<Song> songs) {
        if (mPlayer != null) mPlayer.removeSongs(songs);
    }

    @Override
    public void registPlayCallbacker(IPlayCallbacker callbacker) {
        if (callbacker == null) return;
        if (!iPlayCallbacker.contains(callbacker)) {
            iPlayCallbacker.add(callbacker);
        }
        if (getCurrentPlay() != null) {
            try {
                callbacker.onPlaying(getCurrentPlay());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void unRegistPlayCallbacker(IPlayCallbacker callbacker) {
        iPlayCallbacker.remove(callbacker);
        callbacker = null;
    }


    @Override
    public void linkClientDeath(IBinder client) throws RemoteException {
        client.linkToDeath(clientDeathRecipient, 0);
    }

    @Override
    public synchronized void onPlay(Song song) {
        if (iPlayCallbacker != null)
            for (IPlayCallbacker callbacker : iPlayCallbacker) {
                try {
                    callbacker.onPlaying(song);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
    }

    @Override
    public void onAddSong(int added, int sourceCount) {
        if (iPlayCallbacker != null)
            for (IPlayCallbacker callbacker : iPlayCallbacker) {
                try {
                    callbacker.onSongAdd(added, sourceCount);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
    }

//    @Override
//    public void onSongDeleted(int type) {
//        for (IPlayCallbacker callbacker : iPlayCallbacker) {
//            try {
//                callbacker.onSongDeleted(type);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    public synchronized void onPlayComplete() {
        Log.e("music--", "service --- onPlayComplete");
        if (iPlayCallbacker != null)
            for (IPlayCallbacker callbacker : iPlayCallbacker) {
                try {
                    callbacker.onPlayComplete(mPlayer.getCurrentPlay());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                try {
                    callbacker.onPlayProgress(getCurrentPos());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        removeRunnable();
        playNext();
    }

    @Override
    public synchronized void onPlayPrepared() {
        if (iPlayCallbacker != null)
            for (IPlayCallbacker callbacker : iPlayCallbacker) {
                try {
                    callbacker.onPrepared();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
            mHandler.postDelayed(mRunnable, DELAY_GET_PROGRESS);
        }
        savePlayed();
        Log.e("music--","onPlayPrepared");
    }

    @Override
    public void onPlayError() {
        removeRunnable();
        SharedPreferencesTools.putInt(mContext, ConstantTools.KEY_PROGRESS, 0);
        SharedPreferencesTools.putLong(mContext, ConstantTools.KEY_PLAYING_SONG_ID, 0);
        if (iPlayCallbacker != null)
            for (IPlayCallbacker callbacker : iPlayCallbacker) {
                try {
                    callbacker.onPlayError(mPlayer.getCurrentPlay());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        playNext();
    }

    @Override
    public void onSeekComplete() {
        if (iPlayCallbacker != null)
            for (IPlayCallbacker callbacker : iPlayCallbacker) {
                try {
                    callbacker.onSeekComplete(getCurrentPos());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
            mHandler.postDelayed(mRunnable, DELAY_GET_PROGRESS);
        }
    }

    @Override
    public void onPlayStop() {
        removeRunnable();
        if (iPlayCallbacker != null)
            for (IPlayCallbacker callbacker : iPlayCallbacker) {
                try {
                    callbacker.onPlayStop(null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
    }

    @Override
    public void onPlayingSongDeleted(int pos) {
        if (iPlayCallbacker != null)
            for (IPlayCallbacker callbacker : iPlayCallbacker) {
                try {
                    callbacker.onPlayingSongDeleted(pos);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
    }

    @Override
    public void onPlayingSongPlayed(int pos) {
        if (iPlayCallbacker != null)
            for (IPlayCallbacker callbacker : iPlayCallbacker) {
                try {
                    callbacker.onPlayingSongPlayed(pos);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
    }

    @Override
    public void onPlayingSongsDeleted() {
        if (iPlayCallbacker != null)
            for (IPlayCallbacker callbacker : iPlayCallbacker) {
                try {
                    callbacker.onPlayingSongsDeleted();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
    }

    @Override
    public void onSongQueueEmpty() {
        removeRunnable();
        SharedPreferencesTools.putInt(mContext, ConstantTools.KEY_PROGRESS, 0);
        SharedPreferencesTools.putLong(mContext, ConstantTools.KEY_PLAYING_SONG_ID, 0);
        if (iPlayCallbacker != null)
            for (IPlayCallbacker callbacker : iPlayCallbacker) {
                try {
                    callbacker.onSongQueueEmpty();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
    }

    private void removeRunnable() {
        mHandler.removeCallbacks(mRunnable);
        mHandler.removeCallbacksAndMessages(null);
        mediaHandler.abandonAudioFocus();
    }

    @Override
    public void notifyPause() {
        pause();
    }

    @Override
    public void notifyResume() {
        resume();
    }

    @Override
    public void notifyPlayNext() {
        playNext();
    }

    @Override
    public void notifyPlayPre() {
        playPre();
    }

    private static class ProgressRunnalbe implements Runnable {
        private WeakReference<MusicPlayServiceManager> weakReference;

        ProgressRunnalbe(MusicPlayServiceManager manager) {
            weakReference = new WeakReference<>(manager);
        }

        @Override
        public void run() {
            if (weakReference == null) return;
            MusicPlayServiceManager manager = weakReference.get();
            if (manager == null) return;
            int progress = manager.getCurrentPos();
            if (manager.getMode() == IMusicPlayerManager.MODE_BROWSE && progress >= 10000) {
                manager.onPlayComplete();
                return;
            }
            if (progress != -1) {
                SharedPreferencesTools.putInt(manager.mContext, ConstantTools.KEY_PROGRESS, progress);
                if (manager.iPlayCallbacker != null) {
                    for (IPlayCallbacker callbacker : manager.iPlayCallbacker) {
                        try {
                            callbacker.onPlayProgress(progress);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                SharedPreferencesTools.putInt(manager.mContext, ConstantTools.KEY_PLAY_MODE, manager.getMode());
                Song played = manager.getCurrentPlay();
                if (played != null) {
                    SharedPreferencesTools.putLong(manager.mContext, ConstantTools.KEY_PLAYING_SONG_ID, played.getId());
                }
            }
            if (manager.isPlaying()) {
                manager.mHandler.postDelayed(this, MusicPlayServiceManager.DELAY_GET_PROGRESS);
            } else {
                manager.mHandler.removeCallbacks(this);
            }

        }
    }

    class MediaChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;
            Log.e("MediaChangeReceiver", "MediaChangeReceiver:" + action);
            if (ConstantTools.ACTION_KEY_CODE.equals(action)) {
                int keyCode = intent.getIntExtra(ConstantTools.ACTION_KEY_CODE_EXTRA, 0);
                if (keyCode == 0x04 || keyCode == 0x07) {//上一首
                    playNext();
                } else if (keyCode == 0x03 || keyCode == 0x06) {//下一首
                    playPre();
                } else if (keyCode == 0x08) {//播放
                    resume();
                } else if (keyCode == 0x09) {//暂停
                    pause();
                }
            } else {
                onMediaChanged(action);
            }
        }
    }

    private void onMediaChanged(String action) {
        int mediaState = 0;
        switch (action) {
            case Intent.ACTION_MEDIA_MOUNTED:
                mediaState = MEDIA_MOUNTED;
                break;
            case Intent.ACTION_MEDIA_UNMOUNTED:
                mediaState = MEDIA_UNMOUNTED;
                break;
            case Intent.ACTION_MEDIA_REMOVED:
                mediaState = MEDIA_REMOVED;
                break;
            case Intent.ACTION_MEDIA_SCANNER_STARTED:
                mediaState = MEDIA_SCANNER_STARTED;
                break;
            case Intent.ACTION_MEDIA_SCANNER_FINISHED:
                mediaState = MEDIA_SCANNER_FINISHED;
                saveMusicQueue();
                break;
            case Intent.ACTION_MEDIA_EJECT:
                mediaState = MEDIA_EJECT;
                if (mPlayer != null) {
                    mPlayer.stop();
                }
                onPlayStop();
                break;
            case Intent.ACTION_MEDIA_CHECKING:
                break;
            case Intent.ACTION_MEDIA_SCANNER_SCAN_FILE:
                break;
        }
        if (iPlayCallbacker != null) {
            for (IPlayCallbacker callbacker : iPlayCallbacker) {
                try {
                    callbacker.onMediaChanged(mediaState);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void savePlayed() {
        SharedPreferencesTools.putInt(mContext, ConstantTools.KEY_PLAY_MODE, getMode());
        int progress = getCurrentPos();
        SharedPreferencesTools.putInt(mContext, ConstantTools.KEY_PROGRESS, progress);
        Song played = getCurrentPlay();
        if (played != null) {
            SharedPreferencesTools.putLong(mContext, ConstantTools.KEY_PLAYING_SONG_ID, played.getId());
        }
        Observable.create((ObservableOnSubscribe<List<Long>>) e -> {
            List<Song> songs = getPlaySongs();
            if (songs != null) {
                List<Long> ids = new ArrayList<>();
                for (Song song : songs) {
                    ids.add(song.getId());
                }
                e.onNext(ids);

            }
            e.onComplete();
        }).flatMap((Function<List<Long>, ObservableSource<Boolean>>) songs -> collectSorce.savePlaySongs(songs))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.i("MusicPlayServiceManager", "savePlayQueue " + aBoolean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        disposable.dispose();
                    }

                    @Override
                    public void onComplete() {
                        disposable.dispose();
                    }
                });

    }

    private void saveMusicQueue() {
        SharedPreferencesTools.putInt(mContext, ConstantTools.KEY_PLAY_MODE, getMode());
        Song played = getCurrentPlay();
        if (played != null) {
            dataSource.hasSong(played.getId()).subscribeOn(Schedulers.io()).subscribe(new Observer<Long>() {
                Disposable disposable;

                @Override
                public void onSubscribe(Disposable d) {
                    disposable = d;
                }

                @Override
                public void onNext(Long aLong) {
                    if (aLong == null || aLong <= 0) {
                        SharedPreferencesTools.putInt(mContext, ConstantTools.KEY_PROGRESS, -1);
                        SharedPreferencesTools.putLong(mContext, ConstantTools.KEY_PLAYING_SONG_ID, -1);
                        onPlayStop();
                    } else {
                        int progress = getCurrentPos();
                        SharedPreferencesTools.putInt(mContext, ConstantTools.KEY_PROGRESS, progress);
                        SharedPreferencesTools.putLong(mContext, ConstantTools.KEY_PLAYING_SONG_ID, aLong);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    disposable.dispose();
                }

                @Override
                public void onComplete() {
                    disposable.dispose();
                }
            });

        }

        List<Song> playSongs = getPlaySongs();
        if (playSongs != null) {
            List<Song> queueSong = new ArrayList<>();
            queueSong.addAll(playSongs);
            dataSource.hasSongs(queueSong).toObservable().flatMap((Function<List<Song>, ObservableSource<Boolean>>) songs -> {
                collectSorce.saveSongs(songs);
                queueSong.removeAll(songs);
                removeSongs(queueSong);
                List<Long> ids = new ArrayList<>();
                for (Song song : queueSong) {
                    ids.add(song.getId());
                }
                return collectSorce.deletePlaySongs(ids);
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Boolean>() {
                Disposable disposable;

                @Override
                public void onSubscribe(Disposable d) {
                    disposable = d;
                }

                @Override
                public void onNext(Boolean aBoolean) {
                    if (aBoolean) {
                        onPlayingSongsDeleted();
                    }
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });

        }
    }

//    class StorageObserver extends FileObserver{
//
//        public StorageObserver(String path) {
//            super(path);
//        }
//
//        @Override
//        public void onEvent(int i, @Nullable String s) {
//            Log.e("StorageObserver","StorageObserver:"+s);
//        }
//    }

    public void destroy() {
        mPlayer.destroy();
        mHandler.removeCallbacks(mRunnable);
        mediaHandler.abandonAudioFocus();
        mHandler.removeCallbacksAndMessages(null);
        unRegistMediaReceiver();
//        storageObserver.stopWatching();
    }


}
