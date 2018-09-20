package com.sinohb.music.ui.player;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.sinohb.base.tools.ToastTools;
import com.sinohb.music.R;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.entities.Song;
import com.sinohb.music.sdk.tools.InjectionTools;
import com.sinohb.music.utils.Constants;
import com.sinohb.music.utils.Injection;
import com.sinohb.music.utils.RxViewUtils;
import com.sinohb.music.utils.StringUtils;
import com.sinohb.music.widget.PlayerSeekBar;
import com.sinohb.music.widget.RoundImageView;
import com.sinohb.music.widget.dialog.PlayingQueueDialog;

public class PlayerFragment extends Fragment implements View.OnClickListener, PlayerContract.PlayView<Song> {
    private View rootView;
    private ImageView collect_iv;
    private ImageView play_mode_iv;
    private ImageView play_iv;
    private TextView track_name_tv;
    private TextView artist_name_tv;
    private RoundImageView playing_round_iv;
    private PlayerSeekBar play_seek_bar;
    private PlayerContract.Presenter<Song> mPresenter;
    private TextView music_duration_played_tv;
    private TextView music_duration_tv;
    private Context mContext;

    private PlayingQueueDialog queueDialog;

    public static PlayerFragment newInstance() {
        return new PlayerFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mPresenter = new PlayerPresenter((IMusicPlayManager) context, InjectionTools.provideCollectDataRespository(context), context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_player, container, false);
            play_mode_iv = initClickView(rootView, R.id.play_mode_iv);
            initClickView(rootView, R.id.play_pre_iv);
            play_iv = initClickView(rootView, R.id.play_iv);
            initClickView(rootView, R.id.play_next_iv);
            initClickView(rootView, R.id.play_list_iv);
            collect_iv = initClickView(rootView, R.id.collect_iv);
            track_name_tv = rootView.findViewById(R.id.track_name_tv);
            artist_name_tv = rootView.findViewById(R.id.artist_name_tv);
            playing_round_iv = rootView.findViewById(R.id.playing_round_iv);
            play_seek_bar = rootView.findViewById(R.id.play_seek_bar);
            music_duration_tv = rootView.findViewById(R.id.music_duration_tv);
            music_duration_played_tv = rootView.findViewById(R.id.music_duration_played_tv);
        } else if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        RxViewUtils.setOnSeekListeners(new RxViewUtils.Action2<SeekBar>() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mPresenter != null) mPresenter.seekTo(seekBar.getProgress());
                play_seek_bar.setCanTouch(false);
            }
        }, play_seek_bar);
//        play_seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
        mPresenter.takeView(this);
        return rootView;
    }


    private <T extends View> T initClickView(View root, int resId) {
        View t = root.findViewById(resId);
        t.setOnClickListener(this);
        return (T) t;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_mode_iv:
                mPresenter.setMode();
                break;
            case R.id.play_pre_iv:
                mPresenter.playPre();
                break;
            case R.id.play_iv:
                mPresenter.play();
                break;
            case R.id.play_next_iv:
                mPresenter.playNext();
                break;
            case R.id.play_list_iv:
                if (isHidden() || !isVisible() || isRemoving()) {
                    return;
                }
                if (queueDialog == null) {
                    queueDialog = PlayingQueueDialog.newInstance();
                    queueDialog.setParentVisiable(true);
                }
                if (!queueDialog.isAdded() && !queueDialog.isVisible()
                        && !queueDialog.isRemoving()) {
                    FragmentManager manager = getChildFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.add(queueDialog, "fragment_queue_dialog");
                    ft.commitAllowingStateLoss();
                }
                break;
            case R.id.collect_iv:
                mPresenter.collect();
                break;
        }
    }

    @Override
    public void displayProgress(int progress) {
        if (play_seek_bar != null) {
            play_seek_bar.setProgress(progress);
        }
        if (music_duration_played_tv != null) {
            music_duration_played_tv.setText(Constants.durationToTimeString(progress));
        }
        if (play_seek_bar != null && !play_seek_bar.isCanTouch()) {
            play_seek_bar.setCanTouch(true);
        }
    }

    @Override
    public void displaySong(Song song, int progress) {
        showSongInfo(song, progress);
        notifyPlay(progress);
    }

    @Override
    public void showSongInfo(Song song, int progress) {
        if (song == null) {
            if (play_seek_bar != null) {
                play_seek_bar.setCanTouch(false);
            }
            notifyPause(progress);
            return;
        }
        if (play_seek_bar != null) {
            play_seek_bar.setMax(song.getDuration());
            play_seek_bar.setProgress(progress);
        }
        if (track_name_tv != null) {
            track_name_tv.setText(song.getTitle());
        }
        if (artist_name_tv != null) {
            artist_name_tv.setText(song.getArtist());
        }
        if (music_duration_tv != null) {
            music_duration_tv.setText(Constants.durationToTimeString(song.getDuration()));
        }
        if (music_duration_played_tv != null) {
            music_duration_played_tv.setText(Constants.durationToTimeString(progress));
        }
        Glide.with(mContext)
                .load(StringUtils.getImageUrl(song.getAlbumId()))
                .apply(RequestOptions.placeholderOf(R.drawable.ic_record_album_default)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .error(R.drawable.ic_record_album_default))
                .into(playing_round_iv);
    }


    @Override
    public void notifyPlayPrepared() {
        notifyPlay(0);
    }

    @Override
    public void notifyPlayComplete() {
        notifyViewReset();
    }

    @Override
    public void notifyPlayError() {
        notifyViewReset();
    }

    @Override
    public void notifyPause(int progress) {
        if (playing_round_iv != null) {
            playing_round_iv.pauseDiscAnimatior();
        }
        if (play_iv != null) {
            play_iv.setImageResource(R.drawable.ic_btn_play_selector);
        }
        if (play_seek_bar != null) {
            play_seek_bar.setProgress(progress);
            play_seek_bar.setCanTouch(false);
        }
    }

    @Override
    public void notifySeekCompleted(int progress, boolean isPlaying) {
        if (play_seek_bar != null) {
            play_seek_bar.setProgress(progress);
            play_seek_bar.setCanTouch(isPlaying);
        }
        if (music_duration_played_tv != null) {
            music_duration_played_tv.setText(Constants.durationToTimeString(progress));
        }
    }

    @Override
    public void notifyPlay(int progress) {
        if (playing_round_iv != null && !playing_round_iv.isPlay()) {
            playing_round_iv.startRotateAnimation();
        }
        if (play_iv != null) {
            play_iv.setImageResource(R.drawable.ic_btn_pause_selector);
        }
        if (play_seek_bar != null) {
            play_seek_bar.setCanTouch(true);
        }
        if (music_duration_played_tv != null) {
            music_duration_played_tv.setText(Constants.durationToTimeString(progress));
        }
    }

    @Override
    public void notifyLoopMode(boolean forceToast) {
        if (play_mode_iv != null) {
            play_mode_iv.setImageResource(R.drawable.ic_btn_play_loop_selector);
            if (forceToast)
                ToastTools.showShort(mContext, R.string.mode_loop_list);
        }
    }

    @Override
    public void notifyRandomMode(boolean forceToast) {
        if (play_mode_iv != null) {
            play_mode_iv.setImageResource(R.drawable.ic_btn_play_random_selector);
            if (forceToast)
                ToastTools.showShort(mContext, R.string.mode_random);
        }
    }

    @Override
    public void notifySingleMode(boolean forceToast) {
        if (play_mode_iv != null) {
            play_mode_iv.setImageResource(R.drawable.ic_btn_play_single_selector);
            if (forceToast)
                ToastTools.showShort(mContext, R.string.mode_loop_single);
        }
    }

    @Override
    public void notifyBroseMode(boolean forceToast) {
        if (play_mode_iv != null) {
            play_mode_iv.setImageResource(R.drawable.ic_btn_play_browse_selector);
            if (forceToast)
                ToastTools.showShort(mContext, R.string.mode_browse);
        }
    }

    @Override
    public void notifyCancelCollect(boolean success) {
        if (success) {
            ToastTools.showShort(mContext, R.string.remove_favorite_success);
            displayNotCollect();
        } else
            ToastTools.showShort(mContext, R.string.remove_favorite_fail);
    }

    @Override
    public void notifyCollect(boolean success) {
        if (success) {
            ToastTools.showShort(mContext, R.string.add_favorite_success);
            displayCollected();
        } else
            ToastTools.showShort(mContext, R.string.add_favorite_fail);
    }

    @Override
    public void displayCollected() {
        if (collect_iv != null)
            collect_iv.setImageResource(R.drawable.ic_btn_collect_pressed);
    }

    @Override
    public void displayNotCollect() {
        if (collect_iv != null)
            collect_iv.setImageResource(R.drawable.ic_btn_collect_normal);
    }

    @Override
    public void notifyViewReset() {
        Log.e("music--", "notifyViewReset");
        displayNotCollect();
        notifyPause(0);
        if (track_name_tv != null) {
            track_name_tv.setText("");
        }
        if (artist_name_tv != null) {
            artist_name_tv.setText("");
        }
        if (music_duration_tv != null) {
            music_duration_tv.setText(Constants.durationToTimeString(0));
        }
        if (music_duration_played_tv != null) {
            music_duration_played_tv.setText(Constants.durationToTimeString(0));
        }
        if (play_seek_bar != null) {
            play_seek_bar.setCanTouch(false);
        }
        if (playing_round_iv != null) {
            playing_round_iv.cancelRotateAnimation();
            playing_round_iv.setImageResource(R.drawable.ic_record_album_default);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (queueDialog != null) {
            if ((!isVisible() || isHidden())) {
                if (queueDialog.isAdded() || queueDialog.isVisible()) {
                    queueDialog.dismiss();
                } else {
                    queueDialog.setParentVisiable(false);
                }
            } else {
                queueDialog.setParentVisiable(true);
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null && rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (playing_round_iv != null) {
            playing_round_iv.cancelRotateAnimation();
        }
    }
}
