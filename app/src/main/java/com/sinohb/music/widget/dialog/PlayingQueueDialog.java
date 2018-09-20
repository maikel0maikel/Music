package com.sinohb.music.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sinohb.base.tools.DensityTools;
import com.sinohb.common.adapter.recycleview.BaseRecycleViewAdapter;
import com.sinohb.music.R;
import com.sinohb.music.adapter.DialogPlayListAdapter;
import com.sinohb.music.play.IMusicPlayManager;
import com.sinohb.music.sdk.entities.Song;
import com.sinohb.music.utils.DialogUtils;
import com.sinohb.music.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class PlayingQueueDialog extends DialogFragment implements View.OnClickListener,
        PlayQueueContract.View<Song>, BaseRecycleViewAdapter.OnItemClickListener {
    private View rootView;
    private Context mContext;
    private ImageView play_mode_iv;
    private TextView play_mode_tv;
    private TextView play_list_number_tv;
    private ImageView clear_all_iv;
    private ViewStub empty_stub;
    private RecyclerView recyclerview;
    private ProgressBar progress_bar;

    private DialogPlayListAdapter mAdapter;

    private PlayQueueContract.Presenter<Song> mPresenter;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private boolean isParentVisiable = false;

    public static PlayingQueueDialog newInstance() {
        return new PlayingQueueDialog();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mPresenter = new PlayingQueuePresenter((IMusicPlayManager) context);
    }

    public void setParentVisiable(boolean isVisiable) {
        this.isParentVisiable = isVisiable;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (params == null) {
            params = new WindowManager.LayoutParams();
        }
        params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        int width = DensityTools.getScreenWidth(mContext);
        int height = DensityTools.getScreenHeight(mContext);
        params.width = width / 4 * 2;
        params.height = (int) (height - getResources().getDimension(R.dimen.actionBarSize));
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.dialog_play_queue, container, false);
            play_mode_iv = rootView.findViewById(R.id.play_mode_iv);
            play_mode_tv = rootView.findViewById(R.id.play_mode_tv);
            play_list_number_tv = rootView.findViewById(R.id.play_list_number_tv);
            clear_all_iv = rootView.findViewById(R.id.clear_all_iv);
            empty_stub = rootView.findViewById(R.id.empty_stub);
            recyclerview = rootView.findViewById(R.id.recyclerview);
            progress_bar = rootView.findViewById(R.id.progress_bar);
            play_mode_iv.setOnClickListener(this);
            clear_all_iv.setOnClickListener(this);
            buildAdapter();
            recyclerview.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, false));
            ((SimpleItemAnimator) recyclerview.getItemAnimator()).setSupportsChangeAnimations(false);
            recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        mAdapter.setScroll(false);
                        //Glide.with(mContext).resumeRequests();
                    } else {
                        //Glide.with(mContext).pauseRequests();
                        mAdapter.setScroll(true);
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        } else if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        if (!isParentVisiable) {
            dismiss();
            return super.onCreateView(inflater,container,savedInstanceState);
        }
        return rootView;
    }


    private void buildAdapter() {
        mAdapter = new DialogPlayListAdapter(mContext, new ArrayList<>());
        recyclerview.setAdapter(mAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(linearLayoutManager);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mPresenter != null) {
            mPresenter.takeView(this);
            mPresenter.loadDataSource();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == play_mode_iv) {
            mPresenter.setMode();
        } else if (view == clear_all_iv) {
            DialogUtils.getAlertDialog(mContext, mContext.getString(R.string.delete_song),
                    mContext.getResources().getString(R.string.clear_song_queue), (dialogInterface, i) -> {
                        mPresenter.clearQueue();
                        dialogInterface.dismiss();
                        dismiss();
                    }, (dialogInterface, i) -> dialogInterface.dismiss()).show();

        }
    }

    @Override
    public Song getData(int postion) {
        return mAdapter == null ? null : mAdapter.getData(postion);
    }

    @Override
    public void notifyItemPlaying(int position) {
        if (mAdapter != null) {
            mAdapter.notifyItemChange(position);
            if (recyclerview != null) {
                recyclerview.scrollToPosition(position + mAdapter.getHeadAndFootSize());
            }
        }

    }

    @Override
    public void notifyItemNormal(int pos) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyItemChange(pos);
            }
        });
    }

    @Override
    public void showTitle(String title) {

    }

    @Override
    public void showLoding() {
        progress_bar.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoding() {
        progress_bar.setVisibility(View.GONE);
    }

    @Override
    public void showEmptyView() {
        dismiss();
    }

    @Override
    public void hideEmptyView() {
        empty_stub.setVisibility(View.GONE);
        play_list_number_tv.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDataSource(List<Song> datas) {
        mAdapter.replaceData(datas);
    }

    @Override
    public void onItemClick(View itemView, int pos) {
        mPresenter.onItemClick(mAdapter.getData(pos), pos);
    }

    @Override
    public void onItemLongClick(View itemView, int pos) {

    }

    @Override
    public void notifyLoopMode() {
        play_mode_iv.setImageResource(R.drawable.ic_btn_play_loop_selector);
        play_mode_tv.setText(R.string.mode_loop_list);
    }

    @Override
    public void notifyRandomMode() {
        play_mode_iv.setImageResource(R.drawable.ic_btn_play_random_selector);
        play_mode_tv.setText(R.string.mode_random);
    }

    @Override
    public void notifySingleMode() {
        play_mode_iv.setImageResource(R.drawable.ic_btn_play_single_selector);
        play_mode_tv.setText(R.string.mode_loop_single);
    }

    @Override
    public void notifyBroseMode() {
        play_mode_iv.setImageResource(R.drawable.ic_btn_play_browse_selector);
        play_mode_tv.setText(R.string.mode_browse);
    }

    @Override
    public void showSongsCount(int count) {
        play_list_number_tv.setText("( " + count + ") ");
    }

    @Override
    public void notifySongDeleted(int pos, int songsCount) {
        if (songsCount == 0) {
            dismiss();
            return;
        }
        mHandler.post(() -> {
            mAdapter.notifySongDeleted(pos);
            showSongsCount(songsCount);
        });

    }

    @Override
    public void notifyPlayQueue(int pos) {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            if (recyclerview != null && pos < mAdapter.getRealDataSize()) {
                recyclerview.scrollToPosition(pos + mAdapter.getHeadAndFootSize());
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null && rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        mPresenter.dropView();
    }

}
