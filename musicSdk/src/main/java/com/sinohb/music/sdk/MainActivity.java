package com.sinohb.music.sdk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sinohb.music.sdk.data.db.android.DataSource;
import com.sinohb.music.sdk.data.db.android.LocalMusicCursorSource;
import com.sinohb.music.sdk.data.db.android.LocalMusicDataSource;
import com.sinohb.music.sdk.entities.Song;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        setContentView(R.layout.activity_main);
        CompositeDisposable mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.clear();
        DataSource dataSource = LocalMusicDataSource.getDataSource(this);
//        Disposable disposable = dataSource.getFolderMusicInfo().flatMap(Flowable::fromIterable).
//                toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<MusicFolderInfo>>() {
//            @Override
//            public void accept(List<MusicFolderInfo> songs) throws Exception {
//                for (MusicFolderInfo song:songs){
//                    Log.e("rx","song:"+song.folderName+","+song.folderPath);
//                }
//            }
//        });
//        mCompositeDisposable.add(disposable);

        //mCompositeDisposable.clear();

//        Disposable  disposable = dataSource.getAllArtists().flatMap(Flowable::fromIterable).
//                toList().subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<List<Artist>>() {
//                    @Override
//                    public void accept(List<Artist> lists) throws Exception {
//                        for (Artist album:lists){
//                            Log.e("rx","song:"+album.name+","+album.songCount);
//                        }
//                    }
//                });
//        mCompositeDisposable.add(disposable);

        Disposable disposable = dataSource.getAllSongs().flatMap(Flowable::fromIterable).
                toList().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lists -> {
                    for (Song album : lists) {
                        Log.e("rx", "song:" + album.getArtist() + "," + album.getTitle());
                    }
                });
        mCompositeDisposable.add(disposable);


    }
}
