package com.sinohb.music.sdk.player;

import android.support.annotation.NonNull;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public abstract class MusicPlayerManager<T> implements IMusicPlayerManager<T>, IMusicPlayer.MusicPlayerListener {
    protected List<T> mPlayQueue;//播放列表
    protected IMusicPlayer musicPlayer;
    protected T mCurrentPlay;//当前播放
    private int mCurrrentPlayIndex = -1;//当前播放的位置
    private int mPlayMode = MODE_LOOP;//播放模式
    private Random rand = new Random();
    private List<Integer> mHistoryRandoms = new ArrayList<>();//记录随机播放过的位置，用于上一首操作
    private int mHistoryIndex = -1;//历史位置
    protected PlayListener<T> mPlayListener;
    protected CompositeDisposable compositeDisposable;
    private PlayingSongListener playingSongListener;

    protected MusicPlayerManager(@NonNull MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
        this.musicPlayer.setMusicPlayerListener(this);
        compositeDisposable = new CompositeDisposable();
    }


    @Override
    public void setPlayListener(PlayListener<T> listener) {
        mPlayListener = listener;
    }

    @Override
    public void setPlayingSongListener(PlayingSongListener playingSongListener) {
        this.playingSongListener = playingSongListener;
        if (playingSongListener != null && mCurrrentPlayIndex != -1)
            playingSongListener.onPlayingSongPlayed(mCurrrentPlayIndex);
    }

    /**
     * 获取随机播放的位置
     *
     * @param size 列表大小
     * @return 返回要播放的位置
     */
    private int shuffle(int size) {
        if (size <= 0) {
            return -1;
        }
        if (size == 1) {
            return 0;
        }
        int key = -1;
        for (int i = 0; i < size; i++) {
            key = rand.nextInt(size);
            if (key == mCurrrentPlayIndex) continue;
            if (mHistoryRandoms.contains(key)) {
                continue;
            } else if (mHistoryRandoms.size() >= (size + 1)) {
                mHistoryRandoms.clear();
                return key;
            } else {
                return key;
            }
        }
        return key;
    }

    /**
     * @param force 强制播放下一首，主要用于单曲循环
     */
    @Override
    public void playNext(boolean force) {
        if (mPlayQueue == null || mPlayQueue.isEmpty()) {
            playAsync(mCurrentPlay);
        } else {
            switch (mPlayMode) {
                case MODE_SINGLE:
                    if (force) {
                        orderPlayNext();
                    } else {
                        playAsync(mCurrentPlay);
                    }
                    break;
                case MODE_LOOP:
                case MODE_BROWSE:
                    orderPlayNext();
                    break;
                case MODE_RANDOM:
                    int randomPlayIndex = shuffle(mPlayQueue.size());
                    if (randomPlayIndex < mPlayQueue.size()) {
                        playAsync(mPlayQueue.get(randomPlayIndex));
                        if (mCurrrentPlayIndex != -1 && !mHistoryRandoms.contains(mCurrrentPlayIndex)) {
                            mHistoryRandoms.add(mCurrrentPlayIndex);
                        }
                        mCurrrentPlayIndex = randomPlayIndex;
                        mHistoryIndex = -1;
                        //mHistoryIndex = mCurrrentPlayIndex;
                    }
                    break;
            }
        }
    }

    private void orderPlayNext() {
        if (mCurrrentPlayIndex == mPlayQueue.size() - 1) {
            mCurrrentPlayIndex = 0;
        } else if (mCurrrentPlayIndex > mPlayQueue.size() || mCurrrentPlayIndex < 0) {
            mCurrrentPlayIndex = 0;
        } else {
            mCurrrentPlayIndex++;
        }
        playAsync(mPlayQueue.get(mCurrrentPlayIndex));
    }

    /**
     * 下一首
     *
     * @param force 强制播放上一首，主要用于单曲循环
     */
    @Override
    public void playPre(boolean force) {
        if (mPlayQueue == null || mPlayQueue.isEmpty()) {
            playAsync(mCurrentPlay);
        } else {
            switch (mPlayMode) {
                case MODE_SINGLE:
                    if (force) {
                        orderPlayPre();
                    } else {
                        playAsync(mCurrentPlay);
                    }
                    break;
                case MODE_LOOP:
                case MODE_BROWSE:
                    orderPlayPre();
                    break;
                case MODE_RANDOM:
                    if (mHistoryRandoms.isEmpty()) {

//                        int randomPlayIndex = shuffle(mPlayQueue.size() - 1);
//                        if (randomPlayIndex == -1) {
//                            randomPlayIndex = 0;
//                        }
//                        if (mPlayQueue.size() > 0) {
//                            playAsync(mPlayQueue.get(randomPlayIndex));
//                            mHistoryRandoms.add(randomPlayIndex);
//                            mCurrrentPlayIndex = randomPlayIndex;
//
//                            // mHistoryIndex = mCurrrentPlayIndex;
//                        }
                    } else {
                        int playIndex;
                        if (mHistoryRandoms.size() == 1) {
                            playIndex = mHistoryRandoms.get(0);
                        } else {
                            playIndex = mHistoryRandoms.remove(mHistoryRandoms.size() - 1);
                        }
                        if (mCurrrentPlayIndex == playIndex) return;
                        if (mCurrrentPlayIndex < mPlayQueue.size()) {
                            playAsync(mPlayQueue.get(playIndex));
                            mCurrrentPlayIndex = playIndex;
                        } else {
                            playAsync(mCurrentPlay);
                        }
                    }
                    break;
            }
        }
    }

    private void orderPlayPre() {
        if (mCurrrentPlayIndex == 0) {
            mCurrrentPlayIndex = mPlayQueue.size() - 1;
        } else if (mCurrrentPlayIndex > mPlayQueue.size() || mCurrrentPlayIndex < 0) {
            mCurrrentPlayIndex = 0;
        } else {
            mCurrrentPlayIndex--;
        }
        playAsync(mPlayQueue.get(mCurrrentPlayIndex));
    }

    protected synchronized void startPlay() {
        if (!musicPlayer.isPlaying()) {
            playNext(false);
        }
    }

    @Override
    public void randamPlay(List<T> lists) {
        if (lists == null) return;
        compositeDisposable.clear();
        Disposable disposable = Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            if (mPlayQueue == null) {
                mPlayQueue = new ArrayList<>();
            } else {
                mPlayQueue.clear();
            }
            mPlayQueue.addAll(lists);
            e.onNext(true);
            e.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(added -> {
            if (mPlayMode != MODE_RANDOM) {
                setMode(MODE_RANDOM);
            }
            playNext(false);
        });

        compositeDisposable.add(disposable);
    }

    @Override
    public void addSongToPlay(T song) {
        if (mPlayQueue == null) {
            mPlayQueue = new ArrayList<>();
        }
        if (!mPlayQueue.contains(song)) {
            mPlayQueue.add(song);
            mPlayListener.onAddSong(1, 1);
        } else {
            mPlayListener.onAddSong(0, 1);
        }
        if (!isPlaying()) {
            startPlay();
        }
    }

    @Override
    public void removeSong(T song) {
        if (song == null || mPlayQueue == null) return;
        Observable.create((ObservableOnSubscribe<T>) e -> {
            T nextPlay = null;
            int currPos = 0;
            if (song == mCurrentPlay || song.equals(mCurrentPlay)) {
                switch (mPlayMode) {
                    case MODE_BROWSE:
                    case MODE_LOOP:
                    case MODE_SINGLE:
                        currPos = mCurrrentPlayIndex + 1;
                        if (currPos >= mPlayQueue.size()) currPos = 0;
                        nextPlay = mPlayQueue.get(currPos);
                        if (mPlayQueue.remove(song)) {
                            if (playingSongListener != null)
                                playingSongListener.onPlayingSongDeleted(mCurrrentPlayIndex);
                            e.onNext(nextPlay);
                        }
                        break;
                    case MODE_RANDOM:
                        onSongDeleted(song);
                        int randomPlayIndex = shuffle(mPlayQueue.size() - 1);
                        mHistoryRandoms.remove(Integer.valueOf(mCurrrentPlayIndex));
                        if (randomPlayIndex == -1 && mPlayQueue.size() > 0) {
                            randomPlayIndex = 0;
                        }
                        if (randomPlayIndex != -1) {
                            nextPlay = mPlayQueue.get(randomPlayIndex);
                            if (!mHistoryRandoms.contains(randomPlayIndex)){
                                mHistoryRandoms.add(randomPlayIndex);
                            }
                            //mHistoryIndex = randomPlayIndex;
                            e.onNext(nextPlay);
                        }
                        break;
                }
            } else {
                onSongDeleted(song);
            }
            e.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<T>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(T t) {
                mCurrentPlay = null;
                playAsync(t);
            }

            @Override
            public void onError(Throwable e) {
                disposable.dispose();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
                //notifySongDelete(type);
                if (mPlayQueue == null || mPlayQueue.isEmpty()) {
                    if (playingSongListener != null)
                        playingSongListener.onSongQueueEmpty();
                    stop();
                    mPlayListener.onPlayStop();
                    mCurrentPlay = null;
                    return;
                }
                findPlayPos();
            }
        });

    }

    private void onSongDeleted(T song) {
        if (mPlayQueue.remove(song)) {
            if (playingSongListener != null)
                playingSongListener.onPlayingSongDeleted(mCurrrentPlayIndex);
        }
    }

    protected void findPlayPos() {
        Observable.create((ObservableOnSubscribe<Integer>) e -> {
            if (mPlayQueue != null) {
                int findPos = -1;
                int i = 0;
                for (T t : mPlayQueue) {
                    if (t == mCurrentPlay || t.equals(mCurrentPlay)) {
                        findPos = i;
                        break;
                    }
                    i++;
                }
                e.onNext(findPos);
                //if (findPos != -1) {
//                    if (mPlayMode == MODE_RANDOM && !mHistoryRandoms.contains(findPos)) {
//                        mHistoryRandoms.add(findPos);
//                    }
               // }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Integer integer) {
                if (integer != -1) {
                    mCurrrentPlayIndex = integer;
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });
    }

    @Override
    public void pushPlayQueue(List<T> lists) {
        if (lists == null) return;
        if (mPlayQueue == null) {
            mPlayQueue = new LinkedList<>();
            mPlayQueue.addAll(lists);
            return;
        }
        addSongsToQueue(lists);
    }

    private void addSongsToQueue(List<T> datas) {
        Observable.fromIterable(datas).filter(song -> !mPlayQueue.contains(song))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<T>() {
                    Disposable disposable;
                    int addCount = 0;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(T song) {
                        mPlayQueue.add(song);
                        addCount++;
                    }

                    @Override
                    public void onError(Throwable e) {
                        disposable.dispose();
                        addCount = 0;
                    }

                    @Override
                    public void onComplete() {
                        disposable.dispose();
                        if (!isPlaying()) {
                            startPlay();
                        }
                        if (mPlayListener != null)
                            mPlayListener.onAddSong(addCount, datas.size());
                        addCount = 0;
                    }
                });
    }

    @Override
    public void removeSongs(List<T> songs) {
        if (songs == null || mPlayQueue == null) return;
        Observable.create((ObservableOnSubscribe<T>) e -> {
            int size = songs.size();
            int currPos = mCurrrentPlayIndex;
            T nextPlay = null;
            for (int i = 0; i < size; i++) {
                T t = songs.get(i);
                if (t == mCurrentPlay || t.equals(mCurrentPlay)) {
                    switch (mPlayMode) {
                        case MODE_BROWSE:
                        case MODE_LOOP:
                        case MODE_SINGLE:
                            currPos += size - i;
                            if (currPos >= mPlayQueue.size()) currPos = 0;
                            nextPlay = mPlayQueue.get(currPos);
                            break;
                    }
                    break;
                }
            }
            int dele = 0;
            for (T t : songs) {
                if (mPlayQueue.remove(t)) dele++;
            }
            if (mPlayMode == MODE_RANDOM) {
                int randomPlayIndex = shuffle(mPlayQueue.size() - 1);
                if (randomPlayIndex != -1) {
                    nextPlay = mPlayQueue.get(randomPlayIndex);
                    mHistoryRandoms.remove(Integer.valueOf(mCurrrentPlayIndex));
                    if (!mHistoryRandoms.contains(randomPlayIndex)){
                        mHistoryRandoms.add(randomPlayIndex);
                    }

                    //mHistoryIndex = randomPlayIndex;
                }
            }
            if (dele > 0) {
                if (nextPlay != null) {
                    e.onNext(nextPlay);
                }
                if (playingSongListener != null) playingSongListener.onPlayingSongsDeleted();
            }
            findPlayPos();
            e.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<T>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(T t) {
                playAsync(t);
            }

            @Override
            public void onError(Throwable e) {
                disposable.dispose();
            }

            @Override
            public void onComplete() {
                if (mPlayQueue == null || mPlayQueue.isEmpty()) {
                    if (playingSongListener != null)
                        playingSongListener.onSongQueueEmpty();
                    stop();
                    mPlayListener.onPlayStop();
                    mCurrentPlay = null;
                }
                //notifySongDelete(type);
                disposable.dispose();
            }
        });
    }

    @Override
    public void pushPlayQueue(List<T> lists, int playPos) {
        if (lists == null) return;
        if (playPos == -1) mCurrrentPlayIndex = 0;
        if (mPlayQueue == null) {
            mPlayQueue = new LinkedList<>();
        }
        mPlayQueue.clear();
        mPlayQueue.addAll(lists);
        mCurrrentPlayIndex = playPos;
    }

    @Override
    public void onPlayComplete() {
        if (mPlayListener != null) mPlayListener.onPlayComplete();
    }

    @Override
    public void onPlayPrepared() {
        if (mPlayListener != null) mPlayListener.onPlayPrepared();
        if (playingSongListener != null && mCurrrentPlayIndex != -1)
            playingSongListener.onPlayingSongPlayed(mCurrrentPlayIndex);
    }

    @Override
    public void onPlayError() {
        if (mPlayListener != null) mPlayListener.onPlayError();
    }

    @Override
    public void onSeekComplete() {
        if (mPlayListener != null) mPlayListener.onSeekComplete();
    }

    @Override
    public void onPlayStop() {
        if (mPlayListener != null) mPlayListener.onPlayStop();
    }

    @Override
    public void clearPlayQueue() {
        stop();
        if (mPlayListener != null)
            mPlayListener.onPlayStop();
        mHistoryRandoms.clear();
        mCurrrentPlayIndex = -1;
        mHistoryIndex = -1;
        if (mPlayQueue != null) {
            mPlayQueue.clear();
        }
        mCurrentPlay = null;
    }

    @Override
    public void setMode(int mode) {
        mPlayMode = mode;
    }

    @Override
    public int getMode() {
        return mPlayMode;
    }

    @Override
    public boolean pause() {
        return musicPlayer != null && musicPlayer.pause();
    }

    @Override
    public boolean resume() {
        if (mCurrentPlay == null) return false;
        return musicPlayer != null && musicPlayer.resume();
    }

    @Override
    public void stop() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }

    @Override
    public int getAudioSessionId() {
        return musicPlayer == null ? 0 : musicPlayer.getAudioSessionId();
    }

    @Override
    public void resumePlayBackgroundQueue(List<T> list) {
        if (mPlayQueue == null) {
            mPlayQueue = new ArrayList<>();
        }
        mPlayQueue.clear();
        mPlayQueue.addAll(list);
    }

    @Override
    public T getCurrentPlay() {
        return mCurrentPlay;
    }

    @Override
    public void resumePlayBackgroundSong(T t, int progress) {
        mCurrentPlay = t;
        resumeSavedSong(progress);
        findPlayPos();
    }

    protected abstract void resumeSavedSong(int progress);

    @Override
    public List<T> getPlayQueuq() {
        return mPlayQueue;
    }

    @Override
    public int getCurrentPlayPosition() {
        return musicPlayer == null ? -1 : musicPlayer.getCurrentPosition();
    }

    @Override
    public boolean isPlaying() {
        return musicPlayer != null && musicPlayer.isPlaying();
    }

    @Override
    public boolean seekTo(int position) {
        return musicPlayer != null && musicPlayer.seekto(position);
    }

    @Override
    public void destroy() {
        if (musicPlayer != null) {
            musicPlayer.destroy();
        }
        if (mPlayQueue != null) {
            mPlayQueue.clear();
        }
        if (mHistoryRandoms != null)
            mHistoryRandoms.clear();
        compositeDisposable.clear();
        mCurrentPlay = null;
        mPlayQueue = null;
        mHistoryRandoms = null;
        mPlayListener = null;
    }

}
